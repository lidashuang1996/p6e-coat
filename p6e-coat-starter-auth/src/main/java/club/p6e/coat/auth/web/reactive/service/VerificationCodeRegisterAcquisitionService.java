package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.RegisterContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Register Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface VerificationCodeRegisterAcquisitionService {

    /**
     * 注册验证码发送
     *
     * @param exchange ServerWebExchange 对象
     * @param param    请求对象
     * @return 结果对象
     */
    Mono<RegisterContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request param);

}
