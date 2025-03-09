package club.p6e.coat.auth.validator;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 忘记密码的参数验证器
 *
 * @author lidashuang
 * @version 1.0
 */
public class ForgotPasswordParameterValidator implements ParameterValidatorInterface {

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
        return ForgotPasswordContext.Request.class;
    }

    @Override
    public Mono<Boolean> execute(ServerWebExchange exchange, Object data) {
        if (data instanceof final ForgotPasswordContext.Request param) {
            if (param.getCode() == null || param.getPassword() == null) {
                return Mono.just(false);
            } else {
                if (!VerificationUtil.validationCode(param.getCode())) {
                    return Mono.just(false);
                }
                if (!VerificationUtil.validationPassword(param.getPassword())) {
                    return Mono.just(false);
                }
                return Mono.just(true);
            }
        }
        return Mono.just(false);
    }
}
