package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 忘记密码发送验证码服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ForgotPasswordObtainService {

    /**
     * 忘记密码发送验证码
     *
     * @param exchange ServerWebExchange 对象
     * @param param    请求对象
     * @return 结果对象
     */
    Mono<ForgotPasswordContext.CodeObtain.Dto> execute(
            ServerWebExchange exchange,
            ForgotPasswordContext.CodeObtain.Request param
    );

}
