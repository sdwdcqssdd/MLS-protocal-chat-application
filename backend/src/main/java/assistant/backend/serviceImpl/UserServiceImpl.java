package assistant.backend.serviceImpl;

import assistant.backend.enums.UserType;
import assistant.backend.mapper.UserMapper;
import assistant.backend.pojo.User;
import assistant.backend.service.UserService;
import assistant.backend.utils.Generator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getUser(Long id) {
        if (id == null) {
            return null;
        }
        return userMapper.selectById(id);
    }

    @Override
    public User getUser(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("name", username);
        return userMapper.selectOne(wrapper);
    }

    @Override
    public User registerUser(String username, String password) {
        User user = new User();
        user.setName(username);
        user.setPassword(password);
        user.setType(UserType.USER);
        user.setToken(Generator.generateToken());
        user.setAvatar(null);
        userMapper.insert(user);
        return user;
    }

    @Override
    public void changeUsername(Long id, String newName) {
        User user = userMapper.selectById(id);
        user.setName(newName);
        userMapper.updateById(user);
    }

    @Override
    public void changePassword(Long id, String newPassword) {
        User user = userMapper.selectById(id);
        user.setPassword(newPassword);
        userMapper.updateById(user);
    }

    @Override
    public void changeAvatar(Long id, String newAvatar) {
        User user = userMapper.selectById(id);
        user.setAvatar(newAvatar);
        userMapper.updateById(user);
    }

    @Override
    public List<User> searchUser(String pattern, Integer limit, String exclude) {
        if (pattern == null) {
            return List.of();
        }
        List<User> result = new ArrayList<>();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("name", pattern);
        if (exclude != null) {
            wrapper.ne("name", exclude);
        }
        if (!Objects.equals(exclude, pattern)) {
            User user = getUser(pattern);
            if (user != null) {
                result.add(user);
                limit -= 1;
                wrapper.ne("name", pattern);
            }
        }
        wrapper.orderBy(true, true, "id");
        wrapper.last("limit " + limit.toString());
        result.addAll(userMapper.selectList(wrapper));
        return result;
    }

    @Override
    public List<User> getUsers(Boolean isAdmin) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (isAdmin != null) {
            wrapper.eq("type", isAdmin ? UserType.ADMIN : UserType.USER);
        }
        return userMapper.selectList(wrapper);
    }
}
