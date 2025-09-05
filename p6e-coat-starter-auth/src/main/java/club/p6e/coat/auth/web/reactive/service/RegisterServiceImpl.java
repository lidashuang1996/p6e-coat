package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.web.reactive.aspect.VoucherAspect;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class RegisterServiceImpl implements RegisterService {

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param repository User Repository Object
     */
    public RegisterServiceImpl(PasswordEncryptor encryptor, UserRepository repository) {
        this.encryptor = encryptor;
        this.repository = repository;
    }

    @Override
    public Mono<RegisterContext.Dto> execute(ServerWebExchange exchange, RegisterContext.Request param) {
        param.setPassword(encryptor.execute(param.getPassword()));
        final String account = TransformationUtil.objectToString(exchange.getRequest().getAttributes().get(VoucherAspect.MyServerHttpRequestDecorator.ACCOUNT));
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
        return repository
                .findByPhone(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun executeAccountMode(String account, RegisterContext.Request param)",
                        "register create user account [ " + account + "/(exist) ] exception"
                )))
                .flatMap(u -> SpringUtil
                        .getBean(TransactionalOperator.class)
                        .execute(status -> repository
                                .create(SpringUtil.getBean(UserBuilder.class).create(param.getData()))
                                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                        this.getClass(),
                                        "fun executeAccountMode(String account, RegisterContext.Request param)",
                                        "register create user account exception"
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
        return repository
                .findByPhone(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun executePhoneMode(String account, RegisterContext.Request param)",
                        "register create user phone account [ " + account + "/(exist) ] exception"
                )))
                .flatMap(u -> SpringUtil
                        .getBean(TransactionalOperator.class)
                        .execute(status -> repository
                                .create(SpringUtil.getBean(UserBuilder.class).create(param.getData()))
                                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                        this.getClass(),
                                        "fun executePhoneMode(String account, RegisterContext.Request param)",
                                        "register create user phone account exception"
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
        return repository
                .findByPhone(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun executeMailboxMode(String account, RegisterContext.Request param)",
                        "register create user mailbox account [ " + account + "/(exist) ] exception"
                )))
                .flatMap(u -> SpringUtil
                        .getBean(TransactionalOperator.class)
                        .execute(status -> repository
                                .create(SpringUtil.getBean(UserBuilder.class).create(param.getData()))
                                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                        this.getClass(),
                                        "fun executeMailboxMode(String account, RegisterContext.Request param)",
                                        "register create user mailbox account exception"
                                )))
                                .flux()
                        ).collectList().map(list -> list.get(0))
                );
    }

    /**
     * Execute Phone Or Mailbox Register
     *
     * @return User Object
     */
    protected Mono<User> executePhoneOrMailboxMode(String account, RegisterContext.Request param) {
        return repository
                .findByPhone(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param)",
                        "register create user phone/mailbox account [ " + account + "/(exist) ] exception"
                )))
                .flatMap(u -> SpringUtil
                        .getBean(TransactionalOperator.class)
                        .execute(status -> repository
                                .create(SpringUtil.getBean(UserBuilder.class).create(param.getData()))
                                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                        this.getClass(),
                                        "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param)",
                                        "register create user phone/mailbox account exception"
                                )))
                                .flux()
                        ).collectList().map(list -> list.get(0))
                );
    }

}
