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
     * 执行二维码获取操作
     *
     * @param exchange Server Web Exchange Object
     * @param param    Login Context Quick Response Code Obtain Request Object
     * @return Login Context Quick Response Code Obtain Dto Object
     */
    Mono<LoginContext.QuickResponseCodeObtain.Dto> execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeObtain.Request param);

}
