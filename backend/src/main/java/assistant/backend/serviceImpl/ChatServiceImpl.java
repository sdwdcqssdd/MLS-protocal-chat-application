package assistant.backend.serviceImpl;

import assistant.backend.mapper.ChatMapper;
import assistant.backend.mapper.ChatMemberMapper;
import assistant.backend.mapper.ChatMessageMapper;
import assistant.backend.mapper.UserMapper;
import assistant.backend.pojo.Chat;
import assistant.backend.pojo.ChatMember;
import assistant.backend.pojo.ChatMessage;
import assistant.backend.service.ChatService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private UserMapper userMapper;

    private ChatMapper chatMapper;

    private ChatMemberMapper chatMemberMapper;

    private ChatMessageMapper chatMessageMapper;

    public ChatServiceImpl() {
    }

    @Autowired
    public ChatServiceImpl(UserMapper userMapper, ChatMapper chatMapper, ChatMemberMapper chatMemberMapper,
                           ChatMessageMapper chatMessageMapper) {
        this.userMapper = userMapper;
        this.chatMapper = chatMapper;
        this.chatMemberMapper = chatMemberMapper;
        this.chatMessageMapper = chatMessageMapper;
    }

    @Override
    public List<Chat> listChats(Long userId) {
        QueryWrapper<ChatMember> wrapper = new QueryWrapper<>();
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        return chatMemberMapper.selectList(wrapper).stream()
                .map(e -> {
                    Chat chat = chatMapper.selectById(e.getChatId());
                    QueryWrapper<ChatMember> wrapper1 = new QueryWrapper<>();
                    wrapper1.eq("chat_id", e.getChatId());
                    chat.setReadUntil(e.getReadUntil());
                    chat.setMemberIds(
                            chatMemberMapper.selectList(wrapper1).stream().map(ChatMember::getUserId).toList()
                    );
                    return chat;
                }).toList();
    }

    @Override
    public Long allocateId() {
        return chatMapper.allocateId();
    }

    @Override
    public Chat addChat(List<Long> memberIds) {
        Chat chat = new Chat();
        chat.setId(allocateId());
        chatMapper.insert(chat);
        chat.setMemberIds(memberIds);
        for (Long memberId : memberIds) {
            ChatMember chatMember = new ChatMember();
            chatMember.setChatId(chat.getId());
            chatMember.setUserId(memberId);
            chatMemberMapper.insert(chatMember);
        }
        return chat;
    }

    @Override
    public List<ChatMessage> listMessages(Long chatId) {
        QueryWrapper<ChatMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("chat_id", chatId);
        return chatMessageMapper.selectList(wrapper).stream()
                .sorted(Comparator.comparing(ChatMessage::getTime)).toList();
    }

    @Override
    public boolean addMessage(ChatMessage message) {
        chatMessageMapper.insert(message);
        return true;
    }

    @Override
    public boolean setReadUntil(Long userId, Long chatId, Long readUntil) {
        QueryWrapper<ChatMember> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("chat_id", chatId);
        ChatMember chatMember = chatMemberMapper.selectOne(wrapper);
        if (chatMember == null) {
            return false;
        }
        if (chatMember.getReadUntil() < readUntil) {
            chatMember.setReadUntil(readUntil);
            chatMemberMapper.update(chatMember, wrapper);
        }
        return true;
    }
}
