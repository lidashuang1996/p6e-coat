package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.OAuth2Context;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * OAUTH2 认证服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface OAuth2AuthorizeService {

    /**
     * 执行 OAUTH2 认证
     *
     * @param param 请求对象
     * @return 结果对象
     */
    Mono<Void> execute(ServerWebExchange exchange, OAuth2Context.Auth.Request param);

}
