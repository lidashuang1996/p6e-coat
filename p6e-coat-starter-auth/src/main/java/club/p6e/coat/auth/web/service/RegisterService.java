package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.RegisterContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Register Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface RegisterService {

    /**
     * Execution Register
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param    Register Context Request Object
     * @return Register Context Dto Object
     */
    Mono<RegisterContext.Dto> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RegisterContext.Request param);

}
