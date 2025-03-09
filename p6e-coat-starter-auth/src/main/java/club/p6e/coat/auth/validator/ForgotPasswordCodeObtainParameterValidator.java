package club.p6e.coat.auth.validator;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 忘记密码的验证码获取的参数验证器
 *
 * @author lidashuang
 * @version 1.0
 */
public class ForgotPasswordCodeObtainParameterValidator implements ParameterValidatorInterface {

    /**
     * 执行顺序
     */
    private static final int ORDER = 0;

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 构造方法初始化
     *
     * @param properties 配置文件对象
     */
    public ForgotPasswordCodeObtainParameterValidator(Properties properties) {
        this.properties = properties;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public Class<?> select() {
        return ForgotPasswordContext.CodeObtain.Request.class;
    }

    @Override
    public Mono<Boolean> execute(ServerWebExchange exchange, Object data) {
        if (data instanceof final ForgotPasswordContext.CodeObtain.Request param && param.getAccount() != null) {
            return executeAccount(properties, param.getAccount());
        }
        return Mono.just(false);
    }

    private Mono<Boolean> executeAccount(Properties properties, String account) {
        return switch (properties.getMode()) {
            case PHONE -> Mono.just(VerificationUtil.validationPhone(account));
            case MAILBOX -> Mono.just(VerificationUtil.validationMailbox(account));
            case PHONE_OR_MAILBOX -> Mono.just(VerificationUtil.validationPhone(account)
                    || VerificationUtil.validationMailbox(account));
            default -> Mono.just(false);
        };
    }
}
