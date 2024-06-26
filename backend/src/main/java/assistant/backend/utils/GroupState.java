package assistant.backend.utils;

import java.security.interfaces.XECPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupState {
    private final long groupId;
    private final List<String> members;
    private Node root;
    private Map<String, XECPublicKey> idKeys;
    private final Map<String, XECPublicKey> ephemeralKeys;

    public GroupState(long groupId) {
        this.groupId = groupId;
        members = new ArrayList<>();
        idKeys = new HashMap<>();
        ephemeralKeys = new HashMap<>();
    }

    public void addMember(String username, XECPublicKey idKey, XECPublicKey ephemeralKey) {
        members.add(username);
        idKeys.put(username, idKey);
        ephemeralKeys.put(username, ephemeralKey);
    }

    public void removeMember(String username) {
        members.remove(username);
        idKeys.remove(username);
        ephemeralKeys.remove(username);
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void setIdKeys(Map<String, XECPublicKey> idKeys) {
        this.idKeys = idKeys;
    }

    public long getGroupId() {
        return groupId;
    }

    public List<String> getMembers() {
        return members;
    }

    public Map<String, XECPublicKey> getIdKeys() {
        return idKeys;
    }

    public Map<String, XECPublicKey> getEphemeralKeys() {
        return ephemeralKeys;
    }
}
