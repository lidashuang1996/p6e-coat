package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ForgotPasswordService {

    /**
     * Execute Forgot Password
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param    Forgot Password Context Request Object
     * @return Forgot Password Context Dto Object
     */
    Mono<ForgotPasswordContext.Dto> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ForgotPasswordContext.Request param);

}
