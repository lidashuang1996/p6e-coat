package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.event.PushMessageEvent;
import club.p6e.coat.auth.web.reactive.cache.VerificationCodeRegisterCache;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * Register Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = VerificationCodeRegisterAcquisitionService.class,
        ignored = VerificationCodeRegisterAcquisitionServiceImpl.class
)
public class VerificationCodeRegisterAcquisitionServiceImpl implements VerificationCodeRegisterAcquisitionService {

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
    private final VerificationCodeRegisterCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository User Repository Object
     * @param cache      Verification Code Login Cache Object
     */
    public VerificationCodeRegisterAcquisitionServiceImpl(UserRepository repository, VerificationCodeRegisterCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<RegisterContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, RegisterContext.VerificationCodeAcquisition.Request param) {
        // before registration, it is necessary to verify whether the input account exists
        return validate(param.getAccount()).flatMap(b ->
                // register account does not exist
                b ? execute(exchange, param.getAccount(), param.getLanguage())
                        // register account exist
                        : Mono.error(GlobalExceptionContext.exceptionAccountExistException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, RegisterContext.Acquisition.Request param).",
                        "Verification code register acquisition, register account exist exception."
                )));
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
        }).map(u -> true).defaultIfEmpty(false);
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
//        final RequestVoucher request = (RequestVoucher) exchange.getRequest();
        if (pb || mb) {
//            request.setAccount(account);
            return cache
                    .set(account, code)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun execute(ServerWebExchange exchange, String account, String language).",
                            "Verification code register acquisition, register cache exception."
                    )))
                    .map(s -> {
                        final PushMessageEvent event = new PushMessageEvent(this, List.of(account), REGISTER_TEMPLATE, language, new HashMap<>() {{
                            put("code", code);
                        }});
                        SpringUtil.getBean(ApplicationContext.class).publishEvent(event);
                        return new RegisterContext.VerificationCodeAcquisition.Dto().setAccount(account);
                    });
        } else {
            return Mono.error(GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, String account, String language).",
                    "Verification code register acquisition, register account format verification exception."
            ));
        }
    }

}
