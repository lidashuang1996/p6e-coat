package club.p6e.coat.auth.validator;

import club.p6e.coat.common.utils.VerificationUtil;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 账号密码登录的参数验证器
 *
 * @author lidashuang
 * @version 1.0
 */
public class AccountPasswordLoginParameterValidator implements ParameterValidatorInterface {

    /**
     * 执行顺序
     */
    private static final int ORDER = 0;

    /**
     * 构造方法初始化
     *
     * @param properties 配置文件对象
     */
    public AccountPasswordLoginParameterValidator(Properties properties) {
    }

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public Class<?> select() {
        return LoginContext.AccountPassword.Request.class;
    }

    @Override
    public Mono<Boolean> execute(ServerWebExchange request, Object data) {
        if (data instanceof final LoginContext.AccountPassword.Request param) {
            if (param.getAccount() == null || param.getPassword() == null) {
                return Mono.just(false);
            } else {
                return executeAccount(properties, param.getAccount());
            }
        }
        return Mono.just(false);
    }

    private Mono<Boolean> executeAccount(Properties properties, String account) {
        return switch (properties.getMode()) {
            case PHONE -> Mono.just(VerificationUtil.validationPhone(account));
            case MAILBOX -> Mono.just(VerificationUtil.validationMailbox(account));
            case PHONE_OR_MAILBOX -> Mono.just(VerificationUtil.validationPhone(account)
                    || VerificationUtil.validationMailbox(account));
            default -> Mono.just(true);
        };
    }

}
