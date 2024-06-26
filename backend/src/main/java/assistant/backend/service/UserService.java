package assistant.backend.service;

import assistant.backend.pojo.User;

import java.util.List;

public interface UserService {
    /**
     * get user by id
     *
     * @param id the id of the user
     * @return the user
     */
    User getUser(Long id);

    /**
     * get user by name
     *
     * @param username the name of the user
     * @return the user
     */
    User getUser(String username);

    /**
     * register a new user
     *
     * @param username username
     * @param password password
     * @return the new user
     */
    User registerUser(String username, String password);

    /**
     * change user's name
     *
     * @param id      the id of the user
     * @param newName new name
     */
    void changeUsername(Long id, String newName);

    /**
     * change user's password
     *
     * @param id          the id of the user
     * @param newPassword new password
     */
    void changePassword(Long id, String newPassword);

    /**
     * change user's avatar
     *
     * @param id        the id of the user
     * @param newAvatar the new avatar
     */
    void changeAvatar(Long id, String newAvatar);

    /**
     * search user
     *
     * @param pattern search pattern
     * @param limit   max number of results
     * @param exclude exclude the user with the given name
     * @return the search results
     */
    List<User> searchUser(String pattern, Integer limit, String exclude);

    /**
     * get all users of the given type (user of admin)
     *
     * @param isAdmin the type, null means both types
     * @return users of the given type
     */
    List<User> getUsers(Boolean isAdmin);
}
