package club.p6e.coat.auth.web.reactive;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Server Http Request Param Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ServerHttpRequestParameterValidator {

    /**
     * 执行验证
     *
     * @param param 需要验证的参数
     * @return 验证的结果
     */
    public abstract Mono<Object> execute(ServerWebExchange exchange, Object param);



    /**
     * 执行验证
     *
     * @param param 需要验证的参数
     * @return 验证的结果
     */
    public abstract Mono<Object> execute(ServerWebExchange exchange, Object param)


}
