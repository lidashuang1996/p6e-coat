package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

/**
 * Authentication Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AuthenticationLoginService {

    /**
     * Execute Authorization Verification
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Login Context Authentication Request Object
     * @return User Object
     */
    Mono<User> execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.Authentication.Request param
    );

}
