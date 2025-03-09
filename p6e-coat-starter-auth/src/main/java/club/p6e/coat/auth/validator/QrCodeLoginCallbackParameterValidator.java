package club.p6e.coat.auth.validator;

import club.p6e.coat.auth.context.LoginContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 二维码登录的回调参数验证器
 *
 * @author lidashuang
 * @version 1.0
 */
public class QrCodeLoginCallbackParameterValidator implements ParameterValidatorInterface {

    /**
     * 执行顺序
     */
    private static final int ORDER = 0;

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public Class<?> select() {
        return LoginContext.QrCodeCallback.Request.class;
    }

    @Override
    public Mono<Boolean> execute(ServerWebExchange exchange, Object data) {
        return Mono.just(data instanceof final LoginContext.QrCodeCallback.Request param && param.getContent() != null);
    }

}