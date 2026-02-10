package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.aspect.ReactiveVoucherAspect;
import club.p6e.coat.auth.cache.ReactiveLoginVerificationCodeCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Verification Code Login Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.service.ReactiveLoginVerificationCodeServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginVerificationCodeServiceImpl implements ReactiveLoginVerificationCodeService {

    /**
     * Reactive User Repository Object
     */
    private final ReactiveUserRepository repository;

    /**
     * Reactive Login Verification Code Cache Object
     */
    private final ReactiveLoginVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository Reactive User Repository Object
     * @param cache      Reactive Verification Code Login Cache Object
     */
    public ReactiveLoginVerificationCodeServiceImpl(ReactiveUserRepository repository, ReactiveLoginVerificationCodeCache cache) {
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
        final String account = TransformationUtil.objectToString(exchange.getRequest().getAttributes().get(ReactiveVoucherAspect.MyServerHttpRequestDecorator.ACCOUNT));
        return cache
                .get(account)
                .switchIfEmpty(Mono.error(new CacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param)",
                        "login verification code cache data does not exist or expire exception"
                ))).flatMap(list -> {
                    if (list != null && !list.isEmpty() && list.contains(code)) {
                        return cache.del(account);
                    } else {
                        return Mono.error(new CacheException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param)",
                                "login verification code cache data does not exist or expire exception"
                        ));
                    }
                }).flatMap(r -> getUser(account));
    }

}
