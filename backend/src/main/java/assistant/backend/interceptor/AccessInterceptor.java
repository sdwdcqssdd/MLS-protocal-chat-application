package assistant.backend.interceptor;

import assistant.backend.entity.Response;
import assistant.backend.pojo.User;
import assistant.backend.service.UserService;
import assistant.backend.utils.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

@Component
public class AccessInterceptor implements HandlerInterceptor {

    private UserService userService;

    @Autowired
    public AccessInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        AuthorizedRoles roles = handlerMethod.getMethod().getAnnotation(AuthorizedRoles.class);
        if (roles == null) {
            return true;
        }
        Long userId = Validator.parseId(request.getHeader("User-Id"));
        if (userId == null || !Validator.validateToken(request.getHeader("User-Token"))) {
            forbidden(response);
            return false;
        }
        String userToken = request.getHeader("User-Token");
        User user = userService.getUser(userId);
        if (user == null || !user.getToken().equals(userToken)) {
            forbidden(response);
            return false;
        }
        if (Arrays.stream(roles.roles()).noneMatch(e -> e.equals(user.getType()))) {
            forbidden(response);
            return false;
        }
        return true;
    }

    private void forbidden(HttpServletResponse response) {
        response.setStatus(200);
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(Response.makeResponse(false, "Access Denied"));
            response.getWriter().println(json);
            response.getWriter().flush();
        } catch (IOException ignored) {

        }
    }
}
