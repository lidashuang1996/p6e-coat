package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.OAuth2Context;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * OAUTH2 二次认证确认的服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface OAuth2ReconfirmService {

    /**
     * 默认的页面
     * @param exchange 请求对象
     * @return 结果对象
     */
    Mono<Void> def(ServerWebExchange exchange);

    /**
     * 执行客户端刷新令牌服务
     *
     * @param request 请求对象
     * @return 结果对象
     */
    Mono<OAuth2Context.Confirm.Dto> execute(ServerWebExchange exchange, OAuth2Context.Confirm.Request request);

}
