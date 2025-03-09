package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.RegisterContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 注册验证码发送
 *
 * @author lidashuang
 * @version 1.0
 */
public interface RegisterObtainService {

    /**
     * 注册验证码发送
     *
     * @param exchange ServerWebExchange 对象
     * @param param    请求对象
     * @return 结果对象
     */
    Mono<RegisterContext.Obtain.Dto> execute(ServerWebExchange exchange, RegisterContext.Obtain.Request param);

}
