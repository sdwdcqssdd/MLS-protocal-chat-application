package assistant.backend.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum UserType {
    USER("user"),
    ADMIN("admin");

    @EnumValue
    private String typeName;

    UserType(String typeName) {
        this.typeName = typeName;
    }

    public static UserType getTypeByName(String typeName) {
        for (UserType type : UserType.values()) {
            if (type.typeName.equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        return null;
    }
}
