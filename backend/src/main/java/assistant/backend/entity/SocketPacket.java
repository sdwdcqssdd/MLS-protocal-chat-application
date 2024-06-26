package assistant.backend.entity;

import assistant.backend.enums.SocketPacketType;

/**
 * data class to standardize socket messages
 */
public class SocketPacket {
    SocketPacketType type;

    String data;

    public SocketPacketType getType() {
        return type;
    }

    public void setType(SocketPacketType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
