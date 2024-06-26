package assistant.backend.interceptor;

import assistant.backend.enums.UserType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizedRoles {

    UserType[] roles() default {};
}
