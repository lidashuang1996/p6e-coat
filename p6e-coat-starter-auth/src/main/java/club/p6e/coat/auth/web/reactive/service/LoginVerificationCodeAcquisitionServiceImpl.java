package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.reactive.cache.LoginVerificationCodeCache;
import club.p6e.coat.auth.web.reactive.event.PushVerificationCodeEvent;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * Login Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(LoginVerificationCodeAcquisitionService.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class LoginVerificationCodeAcquisitionServiceImpl implements LoginVerificationCodeAcquisitionService {

    /**
     * VERIFICATION CODE LOGIN TEMPLATE
     */
    private static final String VERIFICATION_CODE_LOGIN_TEMPLATE = "VERIFICATION_CODE_LOGIN_TEMPLATE";

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
     * @param repository User Repository Object
     * @param cache      Login Verification Code Cache Object
     */
    public LoginVerificationCodeAcquisitionServiceImpl(UserRepository repository, LoginVerificationCodeCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<LoginContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request param) {
        return validate(param.getAccount()).flatMap(b -> execute(exchange, param.getAccount(), param.getLanguage()));
    }

    /**
     * Validate Account Exist Status
     *
     * @param account Account Content
     * @return Account Verification Result
     */
    private Mono<Boolean> validate(String account) {
        return (switch (Properties.getInstance().getMode()) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        }).switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountNoExistException(
                this.getClass(),
                "fun validate(String account)",
                "login verification code acquisition account does not exist exception"
        ))).map(u -> true);
    }

    private Mono<LoginContext.VerificationCodeAcquisition.Dto> execute(ServerWebExchange exchange, String account, String language) {
        final String code = GeneratorUtil.random();
        final boolean pb = VerificationUtil.validationPhone(account);
        final boolean mb = VerificationUtil.validationMailbox(account);
        if (pb || mb) {
            exchange.getRequest().getAttributes().put(VoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            return cache
                    .set(account, code)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun execute(ServerWebExchange exchange, String account, String language).",
                            "login verification code cache write exception"
                    )))
                    .flatMap(s -> {
                        final PushVerificationCodeEvent event = new PushVerificationCodeEvent(this, List.of(account), VERIFICATION_CODE_LOGIN_TEMPLATE, language, new HashMap<>() {{
                            put("code", code);
                        }});
                        SpringUtil.getBean(ApplicationContext.class).publishEvent(event);
                        final club.p6e.coat.auth.web.reactive.event.PushVerificationCodeEvent.Callback callback = event.getCallback();
                        if (callback == null) {
                            return Mono.just(new LoginContext.VerificationCodeAcquisition.Dto().setAccount(account));
                        } else {
                            return callback.execute().map(c -> new LoginContext.VerificationCodeAcquisition.Dto().setAccount(account));
                        }
                    });
        } else {
            return Mono.error(GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, String account, String language)",
                    "login verification code acquisition account format exception"
            ));
        }
    }

}

