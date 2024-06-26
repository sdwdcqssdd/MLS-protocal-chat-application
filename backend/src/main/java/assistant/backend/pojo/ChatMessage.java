package assistant.backend.pojo;


import com.baomidou.mybatisplus.annotation.TableName;

@TableName("chat_message")
public class ChatMessage {
    private Long chatId, senderId, time;

    private String content;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long sendId) {
        this.senderId = sendId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
