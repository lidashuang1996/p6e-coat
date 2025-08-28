package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.cache.VerificationCodeForgotPasswordCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = ForgotPasswordService.class,
        ignored = ForgotPasswordServiceImpl.class
)
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Forgot Password Code Cache Object
     */
    private final VerificationCodeForgotPasswordCache cache;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param cache      Forgot Password Code Cache Object
     */
    public ForgotPasswordServiceImpl(PasswordEncryptor encryptor, VerificationCodeForgotPasswordCache cache) {
        this.cache = cache;
        this.encryptor = encryptor;
    }

    /**
     * Query User By Account
     *
     * @param account Account
     * @return User Object
     */
    private Mono<User> getUser(String account) {
//        return switch (Properties.getInstance().getMode()) {
//            case PHONE -> repository.findByPhone(account);
//            case MAILBOX -> repository.findByMailbox(account);
//            case ACCOUNT -> repository.findByAccount(account);
//            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
//        };
        return Mono.empty();
    }

    @Override
    public Mono<ForgotPasswordContext.Dto> execute(ServerWebExchange exchange, ForgotPasswordContext.Request param) {
        final String account = TransformationUtil.objectToString(exchange.getRequest().getAttributes().get("xxx"));
        return Mono.just(new ForgotPasswordContext.Dto());
//        return cache
//                .get(account)
//                // verify if the verification code matches
//                .filter(l -> l.contains(param.getCode()))
//                // delete account cache data
//                .flatMap(l -> cache.del(account))
//                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionForgotPasswordCodeException(
//                        this.getClass(),
//                        "fun execute(ServerWebExchange exchange, ForgotPasswordContext.Request param).",
//                        "forgot password submit verification code cache data does not exist or expire exception."
//                )))
//                .flatMap(v -> getUser(account))
//                .flatMap(m -> repository.updatePassword(Integer.valueOf(m.id()), encryptor.execute(param.getPassword()))) // update user passWord to the latest
//                .map(l -> new ForgotPasswordContext.Dto().setAccount(account));
    }

}
