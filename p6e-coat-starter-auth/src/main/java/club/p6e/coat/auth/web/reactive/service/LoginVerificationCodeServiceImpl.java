package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.aspect.VoucherAspect;
import club.p6e.coat.auth.web.reactive.cache.LoginVerificationCodeCache;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Verification Code Login Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(LoginVerificationCodeService.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class LoginVerificationCodeServiceImpl implements LoginVerificationCodeService {

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Login Verification Code Cache Object
     */
    private final LoginVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache      Verification Code Login Cache Object
     * @param repository User Repository Object
     */
    public LoginVerificationCodeServiceImpl(UserRepository repository, LoginVerificationCodeCache cache) {
        this.cache = cache;
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
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param) {
        final String code = param.getCode();
        final String account = TransformationUtil.objectToString(exchange.getRequest().getAttributes().get(VoucherAspect.MyServerHttpRequestDecorator.ACCOUNT));
        return cache
                .get(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param)",
                        "login verification code cache data does not exist or expire exception"
                ))).flatMap(list -> {
                    if (list != null && !list.isEmpty() && list.contains(code)) {
                        return cache.del(account);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeCacheException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param)",
                                "login verification code cache data does not exist or expire exception"
                        ));
                    }
                }).flatMap(r -> getUser(account));
    }
    
}
