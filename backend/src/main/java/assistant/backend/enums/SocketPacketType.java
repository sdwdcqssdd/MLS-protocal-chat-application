package assistant.backend.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SocketPacketType {
    INIT(0),
    NEW_CHAT(1),
    NEW_MESSAGE(2),
    READ_MESSAGE(3);

    @JsonValue
    private Integer type;

    SocketPacketType(Integer type) {
        this.type = type;
    }
}
