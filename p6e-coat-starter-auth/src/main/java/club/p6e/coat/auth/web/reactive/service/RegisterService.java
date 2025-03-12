package club.p6e.coat.auth.web.reactive.service;

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
     * 注册服务执行
     *
     * @param exchange Server Web Exchange 对象
     * @param param    请求对象
     * @return 结果对象
     */
    Mono<RegisterContext.Dto> execute(ServerWebExchange exchange, RegisterContext.Request param);

}
