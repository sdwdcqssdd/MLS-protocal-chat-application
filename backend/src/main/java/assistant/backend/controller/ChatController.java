package assistant.backend.controller;


import assistant.backend.entity.Response;
import assistant.backend.enums.UserType;
import assistant.backend.interceptor.AuthorizedRoles;
import assistant.backend.pojo.Chat;
import assistant.backend.pojo.User;
import assistant.backend.service.ChatService;
import assistant.backend.service.UserService;
import assistant.backend.socket.ChatSocket;
import assistant.backend.utils.ClientList;
import assistant.backend.utils.MessageClient;
import assistant.backend.utils.Validator;
import assistant.backend.utils.XECKeyPair;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.XECPrivateKey;
import java.security.interfaces.XECPublicKey;
import java.security.spec.NamedParameterSpec;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/chat")
public class ChatController {

    private UserService userService;

    private ChatService chatService;
    private Map<String, MessageClient> map;

    @Autowired
    public ChatController(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
        this.map = ClientList.map;
    }

    /**
     * Add a new chat, and notify all its members
     *
     * @param request   HTTP request
     * @param memberIds ids of the members, the creator's id will be added automatically
     * @return whether creation succeed
     */
    @PostMapping("/new-chat")
    @AuthorizedRoles(roles = {UserType.USER, UserType.ADMIN})
    public Response addChat(HttpServletRequest request, @RequestBody List<Long> memberIds) {
        Long userId = Validator.parseId(request.getHeader("User-Id"));
        String name = userService.getUser(userId).getName();
        MessageClient admin;
        if (!map.containsKey(name)) {
            admin = new MessageClient(name);
            map.put(name, admin);
        } else {
            admin = map.get(name);
        }
        Set<Long> memberIdSet = new HashSet<>(memberIds);
        if (memberIdSet.isEmpty()) {
            return Response.makeResponse(false, "Too few members.");
        }
        for (Long memberId : memberIdSet) {
            User user = userService.getUser(memberId);
            if (user == null) {
                return Response.makeResponse(false, "Invalid member.");
            }
        }
        if (memberIdSet.size() == 1) {
            Long anotherId = memberIdSet.stream().reduce(Long::sum).orElseThrow() - userId;
            boolean alreadyExists = chatService.listChats(userId).stream()
                    .anyMatch(e -> e.getMemberIds().size() == 2 && e.getMemberIds().contains(anotherId));
            if (alreadyExists) {
                return Response.makeResponse(false, "Such chat already exists.");
            }
        }
        memberIdSet.add(userId);
        Chat chat = chatService.addChat(memberIdSet.stream().toList());
        if (chat == null) {
            return Response.makeResponse(false, "");
        }
        long groupId = chat.getId();
        admin.creatNewGroup(groupId);
        String setUpMessage = "";
        for (Long memberId : memberIdSet) {
            User user = userService.getUser(memberId);
            if (Objects.equals(user.getName(), name)) {
                continue;
            }
            MessageClient client;
            if (!map.containsKey(user.getName())) {
                client = new MessageClient(user.getName());
                map.put(user.getName(),client);
            } else {
                client = map.get(user.getName());
            }
            XECPublicKey publicKey = client.joinGroup(groupId);
            setUpMessage = admin.addMember(groupId, user.getName(),client.getIdKey(),publicKey);
        }
        for (Long memberId : memberIdSet) {
            User user = userService.getUser(memberId);
            if (Objects.equals(user.getName(), name)) {
                continue;
            }
            MessageClient client = map.get(user.getName());
            client.receiveSetupMessage(setUpMessage);
        }
        chat.setReadUntil(0L);
        ChatSocket.notifyNewChat(chat);
        return Response.makeResponse(true, "");
    }
}
