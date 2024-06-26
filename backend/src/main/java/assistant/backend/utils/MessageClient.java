package assistant.backend.utils;


import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.XECPublicKey;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MessageClient {

    private final String username;
    private final XECKeyPair idKeyPair;
    /**
     * Ephemeral key pair of each froup
     */
    private final Map<Long, XECKeyPair> ephemeralKeyPairs;
    private final Map<Long, GroupState> groupStates;

    public MessageClient(String username) {
        this.username = username;
        idKeyPair = X25519Utils.newKeyPair();
        ephemeralKeyPairs = new HashMap<>();
        groupStates = new HashMap<>();
    }

    /**
     * For admin <br>
     * Create a new group with an id
     * @param id id of new group
     */
    public void creatNewGroup(long id) {
        GroupState newGroupState = new GroupState(id);
        XECKeyPair setupKey = X25519Utils.newKeyPair();
        ephemeralKeyPairs.put(id, setupKey);
        groupStates.put(id, newGroupState);
        newGroupState.addMember(
                username,
                idKeyPair.getPublic(),
                setupKey.getPublic()
        );
    }

    /**
     * For admin <br>
     * add a new member to a group
     * @param groupId id of group
     * @param member new member alias
     * @param memberIdKey identity key of new member
     * @param memberEphemeralKey ephemeral key of new member
     * @return a json string present the tree
     */
    public String addMember(long groupId, String member, XECPublicKey memberIdKey, XECPublicKey memberEphemeralKey) {
        GroupState groupState = groupStates.get(groupId);
        groupState.addMember(member, memberIdKey, memberEphemeralKey);
        return getSetupMessage(groupId, groupState);
    }

    /**
     * For admin <br>
     * to remove a member of group
     * @param member alias of member
     * @return a json string present the tree
     */
    public String removeMember(long groupId, String member) {
        GroupState groupState = groupStates.get(groupId);
        groupState.removeMember(member);
        return getSetupMessage(groupId, groupState);
    }


    public XECPublicKey joinGroup(long groupId) {
        XECKeyPair keyPair = X25519Utils.newKeyPair();
        ephemeralKeyPairs.put(groupId, keyPair);
        return keyPair.getPublic();
    }

    /**
     * For admin <br>
     * to compute a group setup message
     */
    private String getSetupMessage(long groupId, GroupState groupState) {
        Node root = DHTree.buildSecretTree(DHTree.setupLeavesNode(
                username,
                idKeyPair,
                ephemeralKeyPairs.get(groupId),
                groupState.getMembers(),
                groupState.getIdKeys(),
                groupState.getEphemeralKeys()
        ));
        groupState.setRoot(root);
        String jsonTree = SerializeUtils.toJson(root);
        String jsonMembers = SerializeUtils.toJson(groupState.getIdKeys());
        SetupMessage setupMessage = new SetupMessage(
                groupId,
                jsonTree,
                jsonMembers,
                idKeyPair.getPublic().getU(),
                ephemeralKeyPairs.get(groupId).getPublic().getU()
        );
        return SerializeUtils.toJson(setupMessage);
    }

    /**
     * For member <br>
     * to receive a group setup message
     * @param jsonMsg setup msg in json form
     */
    public void receiveSetupMessage(String jsonMsg) {
        SetupMessage setupMessage = SerializeUtils.toSetupMessage(jsonMsg);
        GroupState groupState = new GroupState(setupMessage.getGroupId());
        Node root = SerializeUtils.toTree(setupMessage.getJsonTree());
        Map<String, XECPublicKey> membersIdKey = SerializeUtils.toMap(setupMessage.getJsonMembers());
        XECKeyPair ephemeralKeyPair = ephemeralKeyPairs.get(setupMessage.getGroupId());
        XECKeyPair secretKeyPair = CryptoUtils.recomputeExchangeKey(
                idKeyPair,
                X25519Utils.fromU(setupMessage.getAdminIdKey()),
                ephemeralKeyPair,
                X25519Utils.fromU(setupMessage.getSetupKey())
        );
        LeafNode leafNode = DHTree.findLeafNode(username, root);
        leafNode.setKeyPair(secretKeyPair);
        DHTree.rebuildSecretTree(leafNode);
        groupState.setRoot(root);
        groupState.setIdKeys(membersIdKey);
        groupStates.put(groupState.getGroupId(), groupState);
    }

    /**
     * For both admin and member to send message
     * @param groupId id of group want to send
     * @param plaintext message want to send
     * @return a json message
     */
    public String sendMessage(long groupId, String plaintext) {
        // gen new key pair and update tree
        XECKeyPair sendKey = X25519Utils.newKeyPair();
        Node root = groupStates.get(groupId).getRoot();
        LeafNode leafNode = DHTree.findLeafNode(this.username, root);
        leafNode.setKeyPair(sendKey);
        Queue<BigInteger> path = new LinkedList<>();
        DHTree.createPublicPath(leafNode, path);

        byte[] key = DHTree.getGroupKey(root);
        byte[] ciphertext = CryptoUtils.encrypt(plaintext.getBytes(StandardCharsets.UTF_8), key);

        String cipherHex = DatatypeConverter.printHexBinary(ciphertext);
        String jsonPath = SerializeUtils.toJson(path);
        Message message = new Message(this.username, groupId, jsonPath, cipherHex);
        return SerializeUtils.toJson(message);
    }

    public String receiveMessage(String msgJson) {
        Message message = SerializeUtils.toMessage(msgJson);
        Queue<BigInteger> path = SerializeUtils.toQueue(message.getUpdatePath());
        Node root = groupStates.get(message.getGroupId()).getRoot();
        LeafNode fromLeaf = DHTree.findLeafNode(message.getFrom(), root);
        DHTree.updatePath(fromLeaf, path);
        byte[] key = DHTree.getGroupKey(root);
        String cipherHex = message.getCipherText();
        byte[] rawText = CryptoUtils.decrypt(
          DatatypeConverter.parseHexBinary(cipherHex),
          key);
        return new String(rawText, StandardCharsets.UTF_8);
    }

    public XECPublicKey getIdKey() {
        return idKeyPair.getPublic();
    }

}
