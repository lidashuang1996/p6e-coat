package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 二维码回调服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface QuickResponseCodeCallbackService {

    /**
     * 执行二维码回调
     *
     * @param param 请求对象
     * @return 结果对象
     */
    Mono<LoginContext.QrCodeCallback.Dto> execute(ServerWebExchange exchange, LoginContext.QrCodeCallback.Request param);

}
