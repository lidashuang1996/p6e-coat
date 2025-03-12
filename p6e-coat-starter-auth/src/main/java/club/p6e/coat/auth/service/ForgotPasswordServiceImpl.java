package club.p6e.coat.auth.service;

import club.p6e.coat.auth.PasswordEncryptor;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.ServerHttpRequest;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.cache.VerificationCodeForgotPasswordCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.repository.UserAuthRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * User Auth Repository Object
     */
    private final UserAuthRepository repository;

    /**
     * Forgot Password Code Cache Object
     */
    private final VerificationCodeForgotPasswordCache cache;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param repository User Auth Repository Object
     * @param cache      Forgot Password Code Cache Object
     */
    public ForgotPasswordServiceImpl(PasswordEncryptor encryptor, UserAuthRepository repository, VerificationCodeForgotPasswordCache cache) {
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
        final String account = ((ServerHttpRequest) exchange.getRequest()).getAccountContent();
        return cache
                .get(account)
                // verify if the verification code matches
                .filter(l -> l.contains(param.getCode()))
                // delete account cache data
                .flatMap(l -> cache.del(account))
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionForgotPasswordCodeException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, ForgotPasswordContext.Request param).",
                        "forgot password submit verification code cache data does not exist or expire exception."
                )))
                .flatMap(v -> getUser(account))
                .flatMap(m -> repository.updatePassword(m.id(), encryptor.execute(param.getPassword()))) // update user passWord to the latest
                .map(l -> new ForgotPasswordContext.Dto().setAccount(account));
    }

}
