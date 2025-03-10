package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.cache.VerificationCodeLoginCache;
import club.p6e.coat.auth.launcher.Launcher;
import club.p6e.coat.auth.launcher.LauncherType;
import club.p6e.coat.auth.repository.UserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class VerificationCodeAcquisitionServiceImpl implements VerificationCodeAcquisitionService {

    /**
     * VERIFICATION CODE LOGIN TEMPLATE
     */
    private static final String VERIFICATION_CODE_LOGIN_TEMPLATE = "VERIFICATION_CODE_LOGIN_TEMPLATE";

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationCodeAcquisitionServiceImpl.class);

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
    public VerificationCodeAcquisitionServiceImpl(
            UserRepository repository,
            VerificationCodeLoginCache cache
    ) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<LoginContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request param) {
        Mono<User> mono;
        String launcherCode = null;
        LauncherType launcherType = null;
        final String account = param.getAccount();
        switch (Properties.getInstance().getMode()) {
            case PHONE -> {
                launcherType = LauncherType.SMS;
                launcherCode = GeneratorUtil.random();
                mono = repository.findByPhone(account);
            }
            case MAILBOX -> {
                launcherType = LauncherType.EMAIL;
                launcherCode = GeneratorUtil.random(8, true, false);
                mono = repository.findByMailbox(account);
            }
            case PHONE_OR_MAILBOX -> {
                if (VerificationUtil.validationPhone(account)) {
                    launcherType = LauncherType.SMS;
                    launcherCode = GeneratorUtil.random();
                } else if (VerificationUtil.validationMailbox(account)) {
                    launcherType = LauncherType.EMAIL;
                    launcherCode = GeneratorUtil.random(8, true, false);
                }
                mono = repository.findByPhoneOrMailbox(account);
            }
            default -> {
                return Mono.error(GlobalExceptionContext.executeServiceNotSupportException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request param).",
                        "Verification code obtain <type> service not supported. [" + account + "]."
                ));
            }
        }
        if (launcherType == null) {
            return Mono.error(GlobalExceptionContext.executeServiceNotSupportException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request param).",
                    "Verification code obtain <type> service not supported. [" + account + "]."
            ));
        } else {
            final String finalLauncherCode = launcherCode.toUpperCase();
            final LauncherType finalLauncherType = launcherType;
            return mono
                    .flatMap(u -> cache.set(account, finalLauncherCode))
                    .flatMap(b -> {
                        if (b) {
                            LOGGER.info("MESSAGE PUSH : [ {}/{} ] >>> {} ::: {}",
                                    account, finalLauncherType, finalLauncherCode, param.getLanguage());
                            return push(List.of(account), finalLauncherType, finalLauncherCode, param.getLanguage());
                        } else {
                            return Mono.error(GlobalExceptionContext.executeCacheException(
                                    this.getClass(),
                                    "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeAcquisition.Request param).",
                                    "Verification code obtain write cache error."
                            ));
                        }
                    }).map(l -> {
                        LOGGER.info("MESSAGE PUSH RESULT LIST : [ {}/{} ] >>> {}", account, finalLauncherType, l);
                        return new LoginContext.VerificationCodeAcquisition.Dto().setAccount(account);
                    });
        }
    }

    private Mono<List<String>> push(List<String> recipients, LauncherType launcherType, String launcherCode, String language) {
        return Launcher.push(launcherType, recipients, VERIFICATION_CODE_LOGIN_TEMPLATE, new HashMap<>(1) {{
            put("code", launcherCode);
        }}, language);
    }

}

