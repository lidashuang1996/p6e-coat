package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Account Password Login Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AccountPasswordLoginService {

    /**
     * 执行账号密码登录操作
     *
     * @param exchange ServerWebExchange 对象
     * @param param    请求对象
     * @return 结果对象
     */
    Mono<User> execute(ServerWebExchange exchange, LoginContext.AccountPassword.Request param);

}
