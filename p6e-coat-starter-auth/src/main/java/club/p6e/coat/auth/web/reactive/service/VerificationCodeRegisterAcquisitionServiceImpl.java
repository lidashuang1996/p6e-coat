package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.auth.web.reactive.cache.VerificationCodeRegisterCache;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.launcher.Launcher;
import club.p6e.coat.auth.launcher.LauncherType;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.VerificationUtil;
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
    public Mono<RegisterContext.Acquisition.Dto> execute(ServerWebExchange exchange, RegisterContext.Acquisition.Request param) {
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
     * Launcher Push Message
     *
     * @param recipients       Recipient List
     * @param launcherType     Launcher Type
     * @param launcherCode     Launcher Code
     * @param launcherLanguage Launcher Language
     * @return Launcher Result List
     */
    private Mono<List<String>> push(List<String> recipients, LauncherType launcherType, String launcherCode, String launcherLanguage) {
        return Launcher.push(launcherType, recipients, REGISTER_TEMPLATE, new HashMap<>() {{
            put("code", launcherCode);
        }}, launcherLanguage);
    }

    /**
     * Execute Register Verification Code Push
     *
     * @param exchange Server Web Exchange Object
     * @param account  Account Content
     * @param language Language Content
     * @return Register Context Acquisition Dto Object
     */
    private Mono<RegisterContext.Acquisition.Dto> execute(ServerWebExchange exchange, String account, String language) {
        final String launcherCode = GeneratorUtil.random();
        final boolean pb = VerificationUtil.validationPhone(account);
        final boolean mb = VerificationUtil.validationMailbox(account);
        final LauncherType launcherType = pb ? LauncherType.SMS : mb ? LauncherType.EMAIL : null;
        final ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        if (launcherType == null) {
            return Mono.error(GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, String account, String language).",
                    "Verification code register acquisition, register account format verification exception."
            ));
        }
        request.setAccountContent(account);
        request.setAccountType(launcherType.name());
        return cache
                .set(account, launcherCode)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, String account, String language).",
                        "Verification code register acquisition, register cache exception."
                )))
                .flatMap(b -> push(List.of(account), launcherType, launcherCode, language))
                .map(l -> new RegisterContext.Acquisition.Dto().setAccount(account));
    }

}
