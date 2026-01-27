package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.aspect.ReactiveVoucherAspect;
import club.p6e.coat.auth.cache.ReactiveForgotPasswordVerificationCodeCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Forgot Password Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveForgotPasswordService.class,
        ignored = ReactiveForgotPasswordServiceImpl.class
)
@Component("club.p6e.coat.auth.web.reactive.service.ReactiveForgotPasswordServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveForgotPasswordServiceImpl implements ReactiveForgotPasswordService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Reactive User Repository Object
     */
    private final ReactiveUserRepository repository;

    /**
     * Reactive Forgot Password Verification Code Cache Object
     */
    private final ReactiveForgotPasswordVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param repository Reactive User Repository Object
     * @param cache      Reactive Forgot Password Verification Code Cache Object
     */
    public ReactiveForgotPasswordServiceImpl(PasswordEncryptor encryptor, ReactiveUserRepository repository, ReactiveForgotPasswordVerificationCodeCache cache) {
        this.cache = cache;
        this.encryptor = encryptor;
        this.repository = repository;
    }

    /**
     * Query User By Account
     *
     * @param account Account
     * @return User Object
     */
    private Mono<User> getUser(String account) {
        return switch (Properties.getInstance().getMode()) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        };
    }

    @Override
    public Mono<ForgotPasswordContext.Dto> execute(ServerWebExchange exchange, ForgotPasswordContext.Request param) {
        final String account = TransformationUtil.objectToString(
                exchange.getRequest().getAttributes().get(ReactiveVoucherAspect.MyServerHttpRequestDecorator.ACCOUNT));
        return cache
                .get(account)
                .filter(l -> l.contains(param.getCode()))
                .flatMap(l -> cache.del(account))
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, ForgotPasswordContext.Request param)",
                        "forgot password verification code cache data does not exist or expire exception"
                )))
                .flatMap(v -> getUser(account))
                .flatMap(u -> repository.updatePassword(Integer.valueOf(u.id()), encryptor.execute(param.getPassword())))
                .map(u -> new ForgotPasswordContext.Dto().setAccount(account));
    }

}
