package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.ReactiveRegisterVerificationCodeCache;
import club.p6e.coat.auth.event.ReactivePushVerificationCodeEvent;
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
 * Reactive Register Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveVerificationCodeAcquisitionService.class,
        ignored = ReactiveVerificationCodeAcquisitionServiceImpl.class
)
@Component("club.p6e.coat.auth.web.reactive.service.ReactiveVerificationCodeAcquisitionServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveVerificationCodeAcquisitionServiceImpl implements ReactiveVerificationCodeAcquisitionService {

    /**
     * REGISTER TEMPLATE
     */
    private static final String REGISTER_TEMPLATE = "REGISTER_TEMPLATE";

    /**
     * Reactive User Repository Object
     */
    private final ReactiveUserRepository repository;

    /**
     * Reactive Verification Code Register Cache Object
     */
    private final ReactiveRegisterVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository Reactive User Repository Object
     * @param cache      Reactive Verification Code Login Cache Object
     */
    public ReactiveVerificationCodeAcquisitionServiceImpl(ReactiveUserRepository repository, ReactiveRegisterVerificationCodeCache cache) {
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
            exchange.getRequest().getAttributes().put(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            return cache
                    .set(account, code)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun execute(ServerWebExchange exchange, String account, String language)",
                            "register verification code acquisition cache write exception"
                    )))
                    .flatMap(s -> {
                        final ReactivePushVerificationCodeEvent event = new ReactivePushVerificationCodeEvent(this, List.of(account), REGISTER_TEMPLATE, language, new HashMap<>() {{
                            put("code", code);
                        }});
                        SpringUtil.getBean(ApplicationContext.class).publishEvent(event);
                        final ReactivePushVerificationCodeEvent.Callback callback = event.getCallback();
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
