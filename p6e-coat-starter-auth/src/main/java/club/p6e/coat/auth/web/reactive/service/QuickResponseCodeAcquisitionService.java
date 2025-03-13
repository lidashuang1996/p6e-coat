package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface QuickResponseCodeAcquisitionService {

    /**
     * Execute Quick Response Code Acquisition
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Quick Response Code Acquisition Request Object
     * @return Login Context Quick Response Code Acquisition Dto Object
     */
    Mono<LoginContext.QuickResponseCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request param);

}
