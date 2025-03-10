package club.p6e.coat.auth.service;

import club.p6e.coat.auth.ServerHttpRequest;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.cache.VerificationCodeLoginCache;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.repository.UserRepository;
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
    public VerificationCodeLoginServiceImpl(
            UserRepository repository,
            VerificationCodeLoginCache cache
    ) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param) {
        final ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        final String code = param.getCode();
        final String accountType = request.getAccountType();
        final String accountContent = request.getAccountContent();
        return cache
                .get(accountContent)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param)",
                        "Verification code login cache data does not exist or expire exception."
                ))).flatMap(list -> {
                    // verify if there are any matching verification codes in the list
                    if (list != null && !list.isEmpty()) {
                        final int index = list.indexOf(code);
                        if (index >= 0) {
                            // successfully verified and deleted all verification codes under the account
                            return cache.del(accountContent);
                        }
                    }
                    return Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param)",
                            "Verification code login cache data does not exist or expire exception."
                    ));
                }).flatMap(r -> switch (Properties.Mode.structure(accountType)) {
                    case PHONE -> repository.findByPhone(accountContent);
                    case MAILBOX -> repository.findByMailbox(accountContent);
                    case ACCOUNT -> repository.findByAccount(accountContent);
                    case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(accountContent);
                });
    }
}
