package assistant.backend.controller;

import assistant.backend.config.ResourcesConfig;
import assistant.backend.entity.Response;
import assistant.backend.enums.UserType;
import assistant.backend.interceptor.AuthorizedRoles;
import assistant.backend.pojo.User;
import assistant.backend.service.UserService;
import assistant.backend.utils.AvatarManager;
import assistant.backend.utils.Generator;
import assistant.backend.utils.TokenUtils;
import assistant.backend.utils.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    private AvatarManager avatarManager;

    private ResourcesConfig resourcesConfig;

    @Autowired
    public UserController(UserService userService, AvatarManager avatarManager, ResourcesConfig resourcesConfig) {
        this.userService = userService;
        this.avatarManager = avatarManager;
        this.resourcesConfig = resourcesConfig;
    }

    /**
     * get the information of the given user
     *
     * @param id the id of the user
     * @return the user
     */
    @GetMapping("/get")
    public Response getUser(@Param("id") Long id) {
        User user = userService.getUser(id);
        return Response.makeResponse(user != null, "", user);
    }

    /**
     * user login
     *
     * @param payload the username and password
     * @return the data to be stored in localStorage
     */
    @PostMapping("/login")
    public Response userLogin(@RequestBody Map<String, String> payload) throws JsonProcessingException {
        String username = payload.get("name");
        String password = payload.get("password");
        if (!Validator.validateUsername(username) || !Validator.validatePassword(password)) {
            return Response.makeResponse(false, "");
        }
        User user = userService.getUser(username);
        if (user == null || !user.getPassword().equals(password)) {
            return Response.makeResponse(false, "");
        }
        Map<String, String> data = new HashMap<>();
        data.put("user_token", user.getToken());
        data.put("user_id", user.getId().toString());

        String tokenData = user.getId() + "-" + user.getType().name();  // 注意这里
        String token = TokenUtils.createToken(tokenData, user.getPassword());
        ObjectMapper mapper = new ObjectMapper();
        data.put("xm-user", mapper.writeValueAsString(Map.of(
                "id", user.getId(),
                "token", token
        )));

        if (user.getType() == UserType.ADMIN) {
            data.put("is_admin", "");
        }
        return Response.makeResponse(true, "", data);
    }

    /**
     * check whether a username is valid
     *
     * @param payload the username
     * @return whether the username is valid
     */
    @PostMapping("/check-username")
    public Response checkUsername(@RequestBody Map<String, String> payload) {
        String username = payload.get("name");
        if (!Validator.validateUsername(username)) {
            return Response.makeResponse(false, "");
        } else {
            return Response.makeResponse(userService.getUser(username) == null, "");
        }
    }

    /**
     * register a user
     *
     * @param payload the username and password
     * @return whether the operation succeed
     */
    @PostMapping("/register")
    public Response userRegister(@RequestBody Map<String, String> payload) {
        String username = payload.get("name");
        String password = payload.get("password");
        if (!Validator.validateUsername(username) || !Validator.validatePassword(password)) {
            return Response.makeResponse(false, "");
        }
        User user = userService.getUser(username);
        if (user != null) {
            return Response.makeResponse(false, "");
        }
        user = userService.registerUser(username, password);
        Map<String, String> data = Map.of("token", user.getToken(), "id", user.getId().toString());
        return Response.makeResponse(true, "", data);
    }

    /**
     * change user's name
     *
     * @param request HTTP request
     * @param payload the new name
     * @return whether the operation succeed
     */
    @PostMapping("/change-username")
    @AuthorizedRoles(roles = {UserType.USER, UserType.ADMIN})
    public Response changeUsername(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String newName = payload.get("newName");
        Long userId = Validator.parseId(request.getHeader("User-Id"));
        if (!Validator.validateUsername(newName)) {
            return Response.makeResponse(false, "Invalid username");
        }
        User user = userService.getUser(newName);
        if (user != null && !user.getId().equals(userId)) {
            return Response.makeResponse(false, "Username not available");
        }
        userService.changeUsername(userId, newName);
        return Response.makeResponse(true, "");
    }

    /**
     * change user's password
     *
     * @param request HTTP request
     * @param payload the new password
     * @return whether the operation succeed
     */
    @PostMapping("/change-password")
    @AuthorizedRoles(roles = {UserType.USER, UserType.ADMIN})
    public Response changePassword(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String newPassword = payload.get("newPassword");
        Long userId = Validator.parseId(request.getHeader("User-Id"));
        if (!Validator.validatePassword(newPassword)) {
            return Response.makeResponse(false, "Invalid password");
        }
        userService.changePassword(userId, newPassword);
        return Response.makeResponse(true, "");
    }

    /**
     * get user's avatar
     *
     * @param id the id of the user
     * @return the avatar of the user
     */
    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar(@Param("id") Long id) {
        User user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        String path = user.getAvatar() == null ? resourcesConfig.getDefaultAvatar() : user.getAvatar();
        try (InputStream inputStream = new FileInputStream(path)) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(inputStream.readAllBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * update the user's avatar
     *
     * @param request HTTP request
     * @param avatar  the new avatar
     * @return whether the operation succeed
     */
    @PostMapping("/change-avatar")
    @AuthorizedRoles(roles = {UserType.USER, UserType.ADMIN})
    public Response changeAvatar(HttpServletRequest request, @RequestParam("avatar") MultipartFile avatar) {
        System.out.println(avatar);
        if (avatar == null || avatar.isEmpty()) {
            return Response.makeResponse(false, "");
        }
        Long userId = Validator.parseId(request.getHeader("User-Id"));
        if (avatar.getSize() > resourcesConfig.getAvatarSizeLimit()) {
            return Response.makeResponse(false, "");
        }
        Path filePath;
        do {
            filePath = Path.of(resourcesConfig.getAvatarPath(), Generator.generateString(10) + ".png");
        } while (filePath.toFile().exists());
        try {
            User user = userService.getUser(userId);
            avatarManager.updateAvatar(user.getAvatar(), filePath, avatar);
            userService.changeAvatar(userId, filePath.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return Response.makeResponse(false, "");
        }
        return Response.makeResponse(true, "");
    }

    /**
     * search users
     *
     * @param request HTTP request
     * @param pattern the search pattern
     * @return search results, max 10 items
     */
    @GetMapping("/search")
    @AuthorizedRoles(roles = {UserType.USER, UserType.ADMIN})
    public Response searchUser(HttpServletRequest request, @RequestParam("pattern") String pattern) {
        if (!Validator.validateName(pattern, 1, 18)) {
            return Response.makeResponse(false, "");
        }
        Long userId = Validator.parseId(request.getHeader("User-Id"));
        String username = userService.getUser(userId).getName();
        return Response.makeResponse(true, "", userService.searchUser(pattern, 10, username));
    }

    /**
     * get all users of the given type (user or admin)
     *
     * @param isAdmin the type of the user, null means both
     * @return all users of the given type
     */
    @GetMapping("list")
    @AuthorizedRoles(roles = {UserType.ADMIN})
    public Response listUsers(@RequestParam(value = "admin", required = false) Boolean isAdmin) {
        return Response.makeResponse(true, "", userService.getUsers(isAdmin));
    }
}
