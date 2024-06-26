package assistant.backend.pojo;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("chat_member")
public class ChatMember {
    private Long chatId, userId, readUntil;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReadUntil() {
        return readUntil;
    }

    public void setReadUntil(Long readUntil) {
        this.readUntil = readUntil;
    }
}
