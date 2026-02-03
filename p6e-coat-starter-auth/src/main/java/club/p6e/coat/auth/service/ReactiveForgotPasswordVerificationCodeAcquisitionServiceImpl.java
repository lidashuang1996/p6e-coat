package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.aspect.ReactiveVoucherAspect;
import club.p6e.coat.auth.cache.ReactiveForgotPasswordVerificationCodeCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.event.ReactivePushVerificationCodeEvent;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
import club.p6e.coat.common.error.AccountException;
import club.p6e.coat.common.error.CacheException;
import club.p6e.coat.common.utils.GeneratorUtil;
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
 * Reactive Forgot Password Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveForgotPasswordVerificationCodeAcquisitionService.class,
        ignored = ReactiveForgotPasswordVerificationCodeAcquisitionServiceImpl.class
)
@Component("club.p6e.coat.auth.service.ReactiveForgotPasswordVerificationCodeAcquisitionServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveForgotPasswordVerificationCodeAcquisitionServiceImpl implements ReactiveForgotPasswordVerificationCodeAcquisitionService {

    /**
     * FORGOT PASSWORD TEMPLATE
     */
    private static final String FORGOT_PASSWORD_TEMPLATE = "FORGOT_PASSWORD_TEMPLATE";

    /**
     * Application Context Object
     */
    private final ApplicationContext context;

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
     * @param context    Application Context Object
     * @param repository Reactive User Repository Object
     * @param cache      Reactive Forgot Password Verification Code Cache Object
     */
    public ReactiveForgotPasswordVerificationCodeAcquisitionServiceImpl(ApplicationContext context, ReactiveUserRepository repository, ReactiveForgotPasswordVerificationCodeCache cache) {
        this.cache = cache;
        this.context = context;
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
        }).switchIfEmpty(Mono.error(new AccountException(
                this.getClass(),
                "fun validate(String account)",
                "forgot password verification code account does not exist exception"
        ))).map(u -> true);
    }

    private Mono<ForgotPasswordContext.VerificationCodeAcquisition.Dto> execute(ServerWebExchange exchange, String account, String language) {
        final String code = GeneratorUtil.random();
        final boolean pb = VerificationUtil.validatePhone(account);
        final boolean mb = VerificationUtil.validateMailbox(account);
        if (pb || mb) {
            exchange.getRequest().getAttributes().put(ReactiveVoucherAspect.MyServerHttpRequestDecorator.ACCOUNT, account);
            return cache
                    .set(account, code)
                    .switchIfEmpty(Mono.error(new CacheException(
                            this.getClass(),
                            "fun execute(ServerWebExchange exchange, String account, String language)",
                            "forgot password verification code cache write exception"
                    )))
                    .flatMap(s -> {
                        final ReactivePushVerificationCodeEvent event = new ReactivePushVerificationCodeEvent(this, List.of(account), FORGOT_PASSWORD_TEMPLATE, language, new HashMap<>() {{
                            put("code", code);
                        }});
                        context.publishEvent(event);
                        final ReactivePushVerificationCodeEvent.Callback callback = event.getCallback();
                        if (callback == null) {
                            return Mono.just(new ForgotPasswordContext.VerificationCodeAcquisition.Dto().setAccount(account));
                        } else {
                            return callback.execute().map(c -> new ForgotPasswordContext.VerificationCodeAcquisition.Dto().setAccount(account));
                        }
                    });
        } else {
            return Mono.error(new AccountException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, String account, String language)",
                    "forgot password verification code account format exception"
            ));
        }
    }

}
