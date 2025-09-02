package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.event.PushVerificationCodeEvent;
import club.p6e.coat.auth.web.reactive.cache.VerificationCodeLoginCache;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = VerificationCodeLoginAcquisitionService.class,
        ignored = VerificationCodeLoginAcquisitionServiceImpl.class
)
public class VerificationCodeLoginAcquisitionServiceImpl implements VerificationCodeLoginAcquisitionService {

    /**
     * VERIFICATION CODE LOGIN TEMPLATE
     */
    private static final String VERIFICATION_CODE_LOGIN_TEMPLATE = "VERIFICATION_CODE_LOGIN_TEMPLATE";

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationCodeLoginAcquisitionServiceImpl.class);

    /**
     * 用户存储库
     */
    private final UserRepository repository;

    /**
     * Verification Code Login Cache Object
     */
    private final VerificationCodeLoginCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository User Repository Object
     * @param cache      Verification Code Login Cache Object
     */
    public VerificationCodeLoginAcquisitionServiceImpl(
            UserRepository repository,
            VerificationCodeLoginCache cache
    ) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<LoginContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request param) {
        // before registration, it is necessary to verify whether the input account exists
        return validate(param.getAccount()).flatMap(b ->
                // register account does not exist
                b ? execute(exchange, param.getAccount(), param.getLanguage())
                        // register account exist
                        : Mono.error(GlobalExceptionContext.exceptionAccountNotExistException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, RegisterContext.Acquisition.Request param).",
                        "verification code login acquisition, login account not exist exception."
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

    private Mono<LoginContext.VerificationCodeAcquisition.Dto> execute(ServerWebExchange exchange, String account, String language) {
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
                        final PushVerificationCodeEvent event = new PushVerificationCodeEvent(this, List.of(account), VERIFICATION_CODE_LOGIN_TEMPLATE, language, new HashMap<>() {{
                            put("code", code);
                        }});
                        SpringUtil.getApplicationContext().publishEvent(event);
                        return new LoginContext.VerificationCodeAcquisition.Dto().setAccount(account);
                    });
        } else {
            return Mono.error(GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, String account, String language).",
                    "verification code login acquisition, login account format verification exception."
            ));
        }
    }

}

