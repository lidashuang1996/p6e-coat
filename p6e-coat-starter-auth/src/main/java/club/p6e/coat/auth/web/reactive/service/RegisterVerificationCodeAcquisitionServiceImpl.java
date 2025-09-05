package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.reactive.cache.RegisterVerificationCodeCache;
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
 * Register Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = RegisterVerificationCodeAcquisitionService.class,
        ignored = RegisterVerificationCodeAcquisitionServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class RegisterVerificationCodeAcquisitionServiceImpl implements RegisterVerificationCodeAcquisitionService {

    /**
     * REGISTER TEMPLATE
     */
    private static final String REGISTER_TEMPLATE = "REGISTER_TEMPLATE";

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Verification Code Register Cache Object
     */
    private final RegisterVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository User Repository Object
     * @param cache      Verification Code Login Cache Object
     */
    public RegisterVerificationCodeAcquisitionServiceImpl(UserRepository repository, RegisterVerificationCodeCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<RegisterContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request param) {
        return validate(param.getAccount()).flatMap(b -> execute(exchange, param.getAccount(), param.getLanguage()));
    }

    /**
     * Validate Account Exist Status
     *
     * @param account Account Content
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
                "register verification code acquisition account does not exist exception"
        ))).map(u -> true);
    }

    /**
     * Execute Register Verification Code Push
     *
     * @param exchange Server Web Exchange Object
     * @param account  Account Content
     * @param language Language Content
     * @return Register Context Acquisition Dto Object
     */
    private Mono<RegisterContext.VerificationCodeAcquisition.Dto> execute(ServerWebExchange exchange, String account, String language) {
        final String code = GeneratorUtil.random();
        final boolean pb = VerificationUtil.validationPhone(account);
        final boolean mb = VerificationUtil.validationMailbox(account);
        if (pb || mb) {
            exchange.getRequest().getAttributes().put(VoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            return cache
                    .set(account, code)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun execute(ServerWebExchange exchange, String account, String language)",
                            "register verification code acquisition cache write exception"
                    )))
                    .flatMap(s -> {
                        final PushVerificationCodeEvent event = new PushVerificationCodeEvent(this, List.of(account), REGISTER_TEMPLATE, language, new HashMap<>() {{
                            put("code", code);
                        }});
                        SpringUtil.getBean(ApplicationContext.class).publishEvent(event);
                        final PushVerificationCodeEvent.Callback callback = event.getCallback();
                        if (callback == null) {
                            return Mono.just(new RegisterContext.VerificationCodeAcquisition.Dto().setAccount(account));
                        } else {
                            return callback.execute().map(c -> new RegisterContext.VerificationCodeAcquisition.Dto().setAccount(account));
                        }
                    });
        } else {
            return Mono.error(GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, String account, String language)",
                    "register verification code acquisition account does not exist exception"
            ));
        }
    }

}
