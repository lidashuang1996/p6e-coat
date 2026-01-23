package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.aspect.ReactiveVoucherAspect;
import club.p6e.coat.auth.cache.ReactiveForgotPasswordVerificationCodeCache;
import club.p6e.coat.auth.web.reactive.event.PushVerificationCodeEvent;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
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
 * Forgot Password Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ForgotPasswordVerificationCodeAcquisitionService.class,
        ignored = ForgotPasswordVerificationCodeAcquisitionServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
@Component("club.p6e.coat.auth.web.reactive.service.ForgotPasswordVerificationCodeAcquisitionServiceImpl")
public class ForgotPasswordVerificationCodeAcquisitionServiceImpl implements ForgotPasswordVerificationCodeAcquisitionService {

    /**
     * FORGOT PASSWORD TEMPLATE
     */
    private static final String FORGOT_PASSWORD_TEMPLATE = "FORGOT_PASSWORD_TEMPLATE";

    /**
     * Repository Object
     */
    private final ReactiveUserRepository repository;

    /**
     * Forgot Password Verification Code Cache Object
     */
    private final ReactiveForgotPasswordVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository Repository Object
     * @param cache      Forgot Password Verification Code Cache Object
     */
    public ForgotPasswordVerificationCodeAcquisitionServiceImpl(ReactiveUserRepository repository, ReactiveForgotPasswordVerificationCodeCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<ForgotPasswordContext.VerificationCodeAcquisition.Dto> execute(ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request param) {
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
                "forgot password verification code account does not exist exception"
        ))).map(u -> true);
    }

    private Mono<ForgotPasswordContext.VerificationCodeAcquisition.Dto> execute(ServerWebExchange exchange, String account, String language) {
        final String code = GeneratorUtil.random();
        final boolean pb = VerificationUtil.validationPhone(account);
        final boolean mb = VerificationUtil.validationMailbox(account);
        if (pb || mb) {
            exchange.getRequest().getAttributes().put(ReactiveVoucherAspect.MyServerHttpRequestDecorator.ACCOUNT, account);
            return cache
                    .set(account, code)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun execute(ServerWebExchange exchange, String account, String language)",
                            "forgot password verification code cache write exception"
                    )))
                    .flatMap(s -> {
                        final PushVerificationCodeEvent event = new PushVerificationCodeEvent(this, List.of(account), FORGOT_PASSWORD_TEMPLATE, language, new HashMap<>() {{
                            put("code", code);
                        }});
                        SpringUtil.getBean(ApplicationContext.class).publishEvent(event);
                        final PushVerificationCodeEvent.Callback callback = event.getCallback();
                        if (callback == null) {
                            return Mono.just(new ForgotPasswordContext.VerificationCodeAcquisition.Dto().setAccount(account));
                        } else {
                            return callback.execute().map(c -> new ForgotPasswordContext.VerificationCodeAcquisition.Dto().setAccount(account));
                        }
                    });
        } else {
            return Mono.error(GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, String account, String language)",
                    "forgot password verification code account format exception"
            ));
        }
    }

}
