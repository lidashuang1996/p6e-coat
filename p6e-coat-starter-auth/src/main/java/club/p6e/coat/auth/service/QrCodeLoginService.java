package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 二维码登录服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface QrCodeLoginService {

    /**
     * 执行二维码登录
     *
     * @param param 请求对象
     * @return 结果对象
     */
    Mono<AuthUser.Model> execute(ServerWebExchange exchange, LoginContext.QrCode.Request param);

}
