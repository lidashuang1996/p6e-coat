package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 二维码获取服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface QrCodeObtainService {

    /**
     * 执行二维码获取操作
     *
     * @param param 请求对象
     * @return 结果对象
     */
    Mono<LoginContext.QrCodeObtain.Dto> execute(ServerWebExchange exchange, LoginContext.QrCodeObtain.Request param);

}
