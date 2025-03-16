package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.cache.VerificationCodeLoginCache;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Verification Code Login Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeLoginServiceImpl implements VerificationCodeLoginService {

    /**
     * Web Flux User Repository Object
     */
    private final UserRepository repository;

    /**
     * Web Flux Verification Code Login Cache Object
     */
    private final VerificationCodeLoginCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache      Verification Code Login Cache Object
     * @param repository User Repository Object
     */
    public VerificationCodeLoginServiceImpl(UserRepository repository, VerificationCodeLoginCache cache) {
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
        final ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        final String code = param.getCode();
        final String account = request.getAccount();
        return cache
                .get(account)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param)",
                        "verification code login cache data does not exist or expire exception."
                ))).flatMap(list -> {
                    // verify if there are any matching verification codes in the list
                    if (list != null && !list.isEmpty()) {
                        final int index = list.indexOf(code);
                        if (index >= 0) {
                            // successfully verified and deleted all verification codes under the account
                            return cache.del(account);
                        }
                    }
                    return Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param)",
                            "verification code login cache data does not exist or expire exception."
                    ));
                }).flatMap(r -> getUser(account));
    }
}
