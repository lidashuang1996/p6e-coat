package club.p6e.coat.auth.validator;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeObtainParameterValidator implements ParameterValidatorInterface {

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
    public VerificationCodeObtainParameterValidator(Properties properties) {
        this.properties = properties;
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public Class<?> select() {
        return LoginContext.VerificationCodeObtain.Request.class;
    }

    @Override
    public Mono<Boolean> execute(ServerWebExchange exchange, Object data) {
        if (data instanceof final LoginContext.VerificationCodeObtain.Request param && param.getAccount() != null) {
            return executeAccount(properties, param.getAccount());
        }
        return Mono.just(false);
    }

    private Mono<Boolean> executeAccount(Properties properties, String account) {
        return switch (properties.getMode()) {
            case MAILBOX -> Mono.just(VerificationUtil.validationMailbox(account));
            case PHONE -> Mono.just(VerificationUtil.validationPhone(account));
            case PHONE_OR_MAILBOX -> Mono.just(VerificationUtil.validationPhone(account)
                    || VerificationUtil.validationMailbox(account));
            default -> Mono.just(false);
        };
    }
}
