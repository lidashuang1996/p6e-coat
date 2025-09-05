package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.token.web.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Login Authentication ServiceImpl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = LoginAuthenticationService.class,
        ignored = LoginAuthenticationServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class LoginAuthenticationServiceImpl implements LoginAuthenticationService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     */
    public LoginAuthenticationServiceImpl(TokenValidator validator) {
        this.validator = validator;
    }

    @Override
    public User execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.Authentication.Request param
    ) {
        return validator.execute(httpServletRequest, httpServletResponse);
    }

}
