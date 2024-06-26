package assistant.backend.service;

import assistant.backend.pojo.Chat;
import assistant.backend.pojo.ChatMessage;

import java.util.List;

public interface ChatService {
    /**
     * list all chats of the user
     *
     * @param userId the id of the user
     * @return the chats
     */
    List<Chat> listChats(Long userId);


    /**
     * allocate an id for a new chat
     *
     * @return the id
     */
    Long allocateId();

    /**
     * add a new chat
     *
     * @param memberIds the members in the chat
     * @return the new chat
     */
    Chat addChat(List<Long> memberIds);

    /**
     * get the messages in a chat
     *
     * @param chatId the id of the chat
     * @return the messages
     */
    List<ChatMessage> listMessages(Long chatId);

    /**
     * add a new message
     *
     * @param message the new message
     * @return whether the operation succeed
     */
    boolean addMessage(ChatMessage message);

    /**
     * set the last time a user read the message of a chat
     *
     * @param userId    the id of the user
     * @param chatId    the id of the chat
     * @param readUntil the time
     * @return whether the operation succeed
     */
    boolean setReadUntil(Long userId, Long chatId, Long readUntil);

}
