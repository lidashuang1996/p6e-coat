package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.web.reactive.cache.VerificationCodeForgotPasswordCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.generator.ForgotPasswordCodeGenerator;
import club.p6e.coat.auth.launcher.Launcher;
import club.p6e.coat.auth.launcher.LauncherType;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * 忘记密码发送验证码服务的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeForgotPasswordAcquisitionServiceImpl implements VerificationCodeForgotPasswordAcquisitionService {

    /**
     * FORGOT PASSWORD TEMPLATE
     */
    private static final String FORGOT_PASSWORD_TEMPLATE = "FORGOT_PASSWORD_TEMPLATE";

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Verification Code Forgot Password Cache Object
     */
    private final VerificationCodeForgotPasswordCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository User Repository Object
     * @param cache      Verification Code Forgot Password Cache Object
     */
    public VerificationCodeForgotPasswordAcquisitionServiceImpl(
            UserRepository repository, VerificationCodeForgotPasswordCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<ForgotPasswordContext.VerificationCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, ForgotPasswordContext.VerificationCodeAcquisition.Request param) {
        return validate(param.getAccount())
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountNoException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, ForgotPasswordContext.Obtain.Request param).",
                        "forgot password obtain code account not exist exception."
                )))
                .flatMap(m -> {
                    final String code;
                    final LauncherType type;
                    final String account = param.getAccount();
                    if (VerificationUtil.validationPhone(account)) {
                        type = LauncherType.SMS;
                        code = generator.execute(LauncherType.SMS.name());
                    } else if (VerificationUtil.validationMailbox(account)) {
                        type = LauncherType.EMAIL;
                        code = generator.execute(LauncherType.EMAIL.name());
                    } else {
                        return Mono.error(GlobalExceptionContext.exceptionLauncherTypeException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, ForgotPasswordContext.Obtain.Request param).",
                                "forgot password obtain code type (LauncherType) exception."
                        ));
                    }
                    return v.setAccount(account)
                            .flatMap(a -> cache.set(account, code))
                            .filter(b -> b)
                            .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionCacheWriteException(
                                    this.getClass(),
                                    "fun execute(ServerWebExchange exchange, ForgotPasswordContext.Obtain.Request param).",
                                    "forgot password obtain code cache write exception."
                            )))
                            .flatMap(b -> Launcher.push(
                                    type,
                                    List.of(account),
                                    FORGOT_PASSWORD_TEMPLATE,
                                    new HashMap<>(0) {{
                                        put("code", code);
                                    }},
                                    param.getAccount()
                            ));
                })
                ).
        map(l -> new ForgotPasswordContext.CodeObtain.Dto().setAccount(param.getAccount()).setMessage(String.join(",", l)));
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
        }).map(u -> false).defaultIfEmpty(true);
    }

}
