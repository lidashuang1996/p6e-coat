package club.p6e.coat.auth;

import club.p6e.coat.common.context.ResultContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 认证机构
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AuthCertificateAuthority {

    /**
     * 执行证书废除
     *
     * @param exchange ServerWebExchange 对象
     * @return 空的返回
     */
    Mono<Void> abolish(ServerWebExchange exchange);

    /**
     * 执行证书颁发
     *
     * @param exchange ServerWebExchange 对象
     * @param model    用户认证模型
     * @return 读取的令牌里面的认证信息
     */
    Mono<ResultContext> award(ServerWebExchange exchange, AuthUser.Model model);

}
