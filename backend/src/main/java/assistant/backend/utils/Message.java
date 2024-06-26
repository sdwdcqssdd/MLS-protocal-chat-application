package assistant.backend.utils;

public class Message {
    private final String from;
    private final long groupId;
    private final String updatePath;
    private final String cipherText;

    public Message(String from, long groupId, String updatePath, String cipherText) {
        this.from = from;
        this.groupId = groupId;
        this.updatePath = updatePath;
        this.cipherText = cipherText;
    }

    public String getFrom() {
        return from;
    }

    public long getGroupId() {
        return groupId;
    }

    public String getUpdatePath() {
        return updatePath;
    }

    public String getCipherText() {
        return cipherText;
    }
}
