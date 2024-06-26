package assistant.backend.socket;

import assistant.backend.entity.SocketPacket;
import assistant.backend.enums.SocketPacketType;
import assistant.backend.pojo.Chat;
import assistant.backend.pojo.ChatMessage;
import assistant.backend.pojo.User;
import assistant.backend.service.ChatService;
import assistant.backend.service.UserService;
import assistant.backend.utils.ClientList;
import assistant.backend.utils.MessageClient;
import assistant.backend.utils.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/socket/chat")
public class ChatSocket {

    /**
     * max waiting time before the client send authentication information
     */
    private static final Long authenticationTime = 10000L;

    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    private static final ConcurrentMap<Session, Long> sessionIdMap = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Long, CopyOnWriteArraySet<Session>> observers = new ConcurrentHashMap<>();

    private static final Timer timer = new Timer();

    private static ChatService chatService;

    private static UserService userService;

    private static void registerObserver(Session session, Long userId) {
        List<Chat> chats = getChats(userId);
        for (Chat chat : chats) {
            if (!observers.containsKey(chat.getId())) {
                observers.put(chat.getId(), new CopyOnWriteArraySet<>());
            }
            observers.get(chat.getId()).add(session);
        }
    }

    private static void handleInit(Session session, Map<String, String> payload) {
        Long userId = Validator.parseId(payload.get("userId"));
        String userToken = payload.get("userToken");
        if (userId == null || !Validator.validateToken(userToken)) {
            closeSession(session);
            return;
        }
        User user = userService.getUser(userId);
        if (user == null || !user.getToken().equals(userToken)) {
            closeSession(session);
            return;
        }
        sessions.add(session);
        sessionIdMap.put(session, userId);
        registerObserver(session, userId);
        pushData(session, userId);
    }

    private static void handleNewMessage(Long userId, ChatMessage chatMessage) {
        chatMessage.setSenderId(userId);
        String name = userService.getUser(userId).getName();
        if (chatMessage.getChatId() == null || chatMessage.getContent() == null) {
            return;
        }
        List<Chat> userChats = getChats(userId);
        if (userChats.stream().map(Chat::getId).noneMatch(e -> e.equals(chatMessage.getChatId()))) {
            return;
        }
        String plainMsg = chatMessage.getContent();
        String msg = ClientList.map.get(name).sendMessage(chatMessage.getChatId(),plainMsg);
        chatMessage.setContent(msg);
        insertMessage(chatMessage);
        CopyOnWriteArraySet<Session> observerSet = observers.get(chatMessage.getChatId());
        if (observerSet == null) {
            return;
        }
        List<Long> send = new ArrayList<>();
        for (Session observer : observerSet) {
            try {
                long Id = sessionIdMap.get(observer);
                if (Id == userId) {
                    if (send.contains(Id)) {
                        continue;
                    }
                    chatMessage.setContent(plainMsg);
                    pushMessage(observer,chatMessage);
                    send.add(Id);
                }
                if (send.contains(Id)) {
                    continue;
                }
                send.add(Id);
                String plain = ClientList.map.get(userService.getUser(Id).getName()).receiveMessage(msg);
                chatMessage.setContent(plain);
                pushMessage(observer, chatMessage);
            } catch (IllegalStateException e) {
                closeSession(observer);
            }
        }
    }

    private static void handleReadMessage(Long userId, Map<String, Long> payload) {
        Long chatId = payload.get("chatId");
        Long readUntil = payload.get("readUntil");
        if (chatId == null || readUntil == null) {
            return;
        }
        List<Chat> userChats = getChats(userId);
        if (userChats.stream().map(Chat::getId).noneMatch(e -> e.equals(chatId))) {
            return;
        }
        setReadUntil(userId, chatId, readUntil);
    }

    public static void push(Session session, SocketPacketType type, Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SocketPacket packet = new SocketPacket();
            packet.setType(type);
            packet.setData(mapper.writeValueAsString(data));
            session.getBasicRemote().sendText(mapper.writeValueAsString(packet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pushChat(Session session, Chat chat) {
        push(session, SocketPacketType.NEW_CHAT, chat);
    }

    public static void pushMessage(Session session, ChatMessage message) {
        push(session, SocketPacketType.NEW_MESSAGE, message);
    }

    public static void pushData(Session session, Long userId) {
        try {
            for (Chat chat : getChats(userId)) {
                pushChat(session, chat);
                List<ChatMessage> messages = getMessages(chat.getId());
                MessageClient client = ClientList.map.get(userService.getUser(userId).getName());
                for (ChatMessage message : messages) {
                    String plainMsg = client.receiveMessage(message.getContent());
                    message.setContent(plainMsg);
                    pushMessage(session, message);
                }
            }
        } catch (IllegalStateException e) {
            closeSession(session);
        }
    }

    private static void closeSession(Session session) {
        try {
            session.close();
        } catch (IOException ignored) {

        }
        sessions.remove(session);
        sessionIdMap.remove(session);
        for (CopyOnWriteArraySet<Session> set : observers.values()) {
            set.remove(session);
        }
    }

    private static void insertMessage(ChatMessage message) {
        message.setTime(System.currentTimeMillis());
        chatService.addMessage(message);
    }

    private static void setReadUntil(Long userId, Long chatId, Long readUntil) {
        chatService.setReadUntil(userId, chatId, readUntil);
    }

    public static List<Chat> getChats(Long userId) {
        return chatService.listChats(userId);
    }

    public static List<ChatMessage> getMessages(Long chatId) {
        return chatService.listMessages(chatId);
    }

    public static void notifyNewChat(Chat chat) {
        CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<>();
        for (Session session : sessions) {
            Long id = sessionIdMap.get(session);
            if (chat.getMemberIds().contains(id)) {
                sessionSet.add(session);
            }
        }
        for (Session session : sessionSet) {
            pushChat(session, chat);
        }
        observers.put(chat.getId(), sessionSet);
    }

    @Autowired
    public void setChatService(ChatService chatService) {
        ChatSocket.chatService = chatService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        ChatSocket.userService = userService;
    }

    @OnOpen
    public void onOpen(Session session) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!sessions.contains(session)) {
                    closeSession(session);
                }
            }
        }, authenticationTime);
    }

    @OnClose
    public void onClose(Session session) {
        closeSession(session);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SocketPacket packet = mapper.readValue(message, SocketPacket.class);

            if (packet.getType() == SocketPacketType.INIT) {
                Map<String, String> payload = mapper.readValue(packet.getData(), new TypeReference<>() {
                });
                handleInit(session, payload);
                return;
            }

            Long userId = sessionIdMap.get(session);
            if (userId == null) {
                closeSession(session);
                return;
            }
            if (packet.getType() == SocketPacketType.NEW_CHAT) {
                // illegal packet type
            } else if (packet.getType() == SocketPacketType.NEW_MESSAGE) {
                ChatMessage chatMessage = mapper.readValue(packet.getData(), new TypeReference<>() {
                });
                handleNewMessage(userId, chatMessage);
            } else if (packet.getType() == SocketPacketType.READ_MESSAGE) {
                Map<String, Long> payload = mapper.readValue(packet.getData(), new TypeReference<>() {
                });
                handleReadMessage(userId, payload);
            }
        } catch (JsonProcessingException | ClassCastException e) {
            e.printStackTrace();
        }
    }
}
