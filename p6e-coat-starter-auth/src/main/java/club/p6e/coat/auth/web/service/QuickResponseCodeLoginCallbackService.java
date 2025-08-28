package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface QuickResponseCodeLoginCallbackService {

    /**
     * Execute Quick Response Code Login
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param    Login Context Quick Response Code Request Object
     * @return User Object
     */
    Mono<User> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request param);

}
