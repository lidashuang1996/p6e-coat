package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 验证码获取服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeObtainService {

    /**
     * 执行验证码获取
     *
     * @param exchange ServerWebExchange 对象
     * @param param    请求对象
     * @return 结果对象
     */
    Mono<LoginContext.VerificationCodeObtain.Dto> execute(
            ServerWebExchange exchange, LoginContext.VerificationCodeObtain.Request param);

}
