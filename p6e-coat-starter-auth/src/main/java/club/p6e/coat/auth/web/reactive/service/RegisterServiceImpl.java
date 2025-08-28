package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.*;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Register Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = RegisterService.class,
        ignored = RegisterServiceImpl.class
)
public class RegisterServiceImpl implements RegisterService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * User Repository Object
     */
    private final UserRepository userRepository;

    /**
     * Constructor Initialization
     *
     * @param encryptor          Password Encryptor Object
     * @param userRepository     User Repository Object
     */
    public RegisterServiceImpl(
            PasswordEncryptor encryptor,
            UserRepository userRepository
    ) {
        this.encryptor = encryptor;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<RegisterContext.Dto> execute(ServerWebExchange exchange, RegisterContext.Request param) {
        final String account = TransformationUtil.objectToString(exchange.getRequest().getAttributes().get("xxx"));
        return (switch (Properties.getInstance().getMode()) {
            case PHONE -> executePhoneMode(account, param);
            case MAILBOX -> executeMailboxMode(account, param);
            case ACCOUNT -> executeAccountMode(account, param);
            case PHONE_OR_MAILBOX -> executePhoneOrMailboxMode(account, param);
        }).map(u -> {
            final RegisterContext.Dto result = new RegisterContext.Dto();
            result.getData().putAll(u.toMap());
            return result;
        });
    }

    /**
     * Execute Account Register
     *
     * @return User Object
     */
    protected Mono<User> executeAccountMode(String account, RegisterContext.Request param) {
        return userRepository
                .findByPhone(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun executeAccountMode(String account, RegisterContext.Request param).",
                        "create user account [ " + account + "/(exist) ] exception."
                )))
                .flatMap(u -> SpringUtil
                        .getBean(TransactionalOperator.class)
                        .execute(status -> userRepository
                                .create(SpringUtil.getBean(UserBuilder.class).create(param.getData()))
                                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                        this.getClass(),
                                        "fun executeAccountMode(String account, RegisterContext.Request param).",
                                        "create user account info data exception."
                                )))
                                .flux()
                        ).collectList().map(list -> list.get(0))
                );
    }

    /**
     * Execute Phone Register
     *
     * @return User Object
     */
    private Mono<User> executePhoneMode(String account, RegisterContext.Request param) {
        return userRepository
                .findByPhone(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun executePhoneMode(String account, RegisterContext.Request param).",
                        "create user phone account [ " + account + "/(exist) ] exception."
                )))
                .flatMap(u -> SpringUtil
                        .getBean(TransactionalOperator.class)
                        .execute(status -> userRepository
                                .create(SpringUtil.getBean(UserBuilder.class).create(param.getData()))
                                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                        this.getClass(),
                                        "fun executePhoneMode(String account, RegisterContext.Request param).",
                                        "create user phone account info data exception."
                                )))
                                .flux()
                        ).collectList().map(list -> list.get(0))
                );
    }

    /**
     * Execute Mailbox Register
     *
     * @return User Object
     */
    private Mono<User> executeMailboxMode(String account, RegisterContext.Request param) {
        return userRepository
                .findByPhone(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun executeMailboxMode(String account, RegisterContext.Request param).",
                        "create user mailbox account [ " + account + "/(exist) ] exception."
                )))
                .flatMap(u -> SpringUtil
                        .getBean(TransactionalOperator.class)
                        .execute(status -> userRepository
                                .create(SpringUtil.getBean(UserBuilder.class).create(param.getData()))
                                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                        this.getClass(),
                                        "fun executeMailboxMode(String account, RegisterContext.Request param).",
                                        "create user mailbox account info data exception."
                                )))
                                .flux()
                        ).collectList().map(list -> list.get(0))
                );
    }

    /**
     * 执行手机号码/邮箱登录
     *
     * @return 结果对象
     */
    protected Mono<User> executePhoneOrMailboxMode(String account, RegisterContext.Request param) {
        return userRepository
                .findByPhone(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param).",
                        "create user phone/mailbox account [ " + account + "/(exist) ] exception."
                )))
                .flatMap(u -> SpringUtil
                        .getBean(TransactionalOperator.class)
                        .execute(status -> userRepository
                                .create(SpringUtil.getBean(UserBuilder.class).create(param.getData()))
                                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                        this.getClass(),
                                        "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param).",
                                        "create user phone/mailbox account info data exception."
                                )))
                                .flux()
                        ).collectList().map(list -> list.get(0))
                );
    }

}
