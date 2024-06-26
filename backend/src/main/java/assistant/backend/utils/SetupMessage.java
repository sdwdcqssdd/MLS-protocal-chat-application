package assistant.backend.utils;

import java.math.BigInteger;

public class SetupMessage {
    public final long groupId;
    public final String jsonTree;
    public final String jsonMembers;
    public final BigInteger adminIdKey;
    public final BigInteger setupKey;

    public SetupMessage(long groupId, String jsonTree, String jsonMembers, BigInteger adminIdKey, BigInteger setupKey) {
        this.groupId = groupId;
        this.jsonTree = jsonTree;
        this.jsonMembers = jsonMembers;
        this.adminIdKey = adminIdKey;
        this.setupKey = setupKey;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getJsonTree() {
        return jsonTree;
    }

    public String getJsonMembers() {
        return jsonMembers;
    }

    public BigInteger getAdminIdKey() {
        return adminIdKey;
    }

    public BigInteger getSetupKey() {
        return setupKey;
    }
}
