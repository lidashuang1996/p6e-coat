package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.aspect.ReactiveVoucherAspect;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
import club.p6e.coat.common.exception.AccountException;
import club.p6e.coat.common.exception.DataBaseException;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Register Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.service.ReactiveRegisterServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveRegisterServiceImpl implements ReactiveRegisterService {

    /**
     * User Builder Object
     */
    private final UserBuilder builder;

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Transactional Operator Object
     */
    private final TransactionalOperator transactional;

    /**
     * Reactive User Repository Object
     */
    private final ReactiveUserRepository repository;

    /**
     * Constructor Initialization
     *
     * @param builder       User Builder Object
     * @param encryptor     Password Encryptor Object
     * @param transactional Transactional Operator Object
     * @param repository    Reactive User Repository Object
     */
    public ReactiveRegisterServiceImpl(UserBuilder builder, PasswordEncryptor encryptor, TransactionalOperator transactional, ReactiveUserRepository repository) {
        this.builder = builder;
        this.encryptor = encryptor;
        this.repository = repository;
        this.transactional = transactional;
    }

    @Override
    public Mono<RegisterContext.Dto> execute(ServerWebExchange exchange, RegisterContext.Request param) {
        param.setPassword(encryptor.execute(param.getPassword()));
        final String account = TransformationUtil.objectToString(exchange.getRequest().getAttributes().get(ReactiveVoucherAspect.MyServerHttpRequestDecorator.ACCOUNT));
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
                .switchIfEmpty(Mono.error(new AccountException(
                        this.getClass(),
                        "fun executeAccountMode(String account, RegisterContext.Request param)",
                        "register create user account [ " + account + "/(exist) ] exception"
                )))
                .flatMap(u -> transactional.execute(status -> repository
                                .create(builder.create(param.getData()))
                                .switchIfEmpty(Mono.error(new DataBaseException(
                                        this.getClass(),
                                        "fun executeAccountMode(String account, RegisterContext.Request param)",
                                        "register create user account exception"
                                ))).flux()
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
                .switchIfEmpty(Mono.error(new AccountException(
                        this.getClass(),
                        "fun executePhoneMode(String account, RegisterContext.Request param)",
                        "register create user phone account [ " + account + "/(exist) ] exception"
                )))
                .flatMap(u -> transactional.execute(status -> repository
                                .create(builder.create(param.getData()))
                                .switchIfEmpty(Mono.error(new DataBaseException(
                                        this.getClass(),
                                        "fun executePhoneMode(String account, RegisterContext.Request param)",
                                        "register create user phone account exception"
                                ))).flux()
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
                .switchIfEmpty(Mono.error(new AccountException(
                        this.getClass(),
                        "fun executeMailboxMode(String account, RegisterContext.Request param)",
                        "register create user mailbox account [ " + account + "/(exist) ] exception"
                )))
                .flatMap(u -> transactional.execute(status -> repository
                                .create(builder.create(param.getData()))
                                .switchIfEmpty(Mono.error(new DataBaseException(
                                        this.getClass(),
                                        "fun executeMailboxMode(String account, RegisterContext.Request param)",
                                        "register create user mailbox account exception"
                                ))).flux()
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
                .switchIfEmpty(Mono.error(new AccountException(
                        this.getClass(),
                        "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param)",
                        "register create user phone/mailbox account [ " + account + "/(exist) ] exception"
                )))
                .flatMap(u -> transactional.execute(status -> repository
                                .create(builder.create(param.getData()))
                                .switchIfEmpty(Mono.error(new DataBaseException(
                                        this.getClass(),
                                        "fun executePhoneOrMailboxMode(String account, RegisterContext.Request param)",
                                        "register create user phone/mailbox account exception"
                                ))).flux()
                        ).collectList().map(list -> list.get(0))
                );
    }

}
