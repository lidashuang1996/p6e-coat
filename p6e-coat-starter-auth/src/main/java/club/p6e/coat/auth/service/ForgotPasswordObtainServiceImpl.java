package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.ForgotPasswordCodeCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.generator.ForgotPasswordCodeGenerator;
import club.p6e.coat.auth.launcher.Launcher;
import club.p6e.coat.auth.launcher.LauncherType;
import club.p6e.coat.auth.model.UserModel;
import club.p6e.coat.auth.repository.UserRepository;
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
public class ForgotPasswordObtainServiceImpl implements ForgotPasswordObtainService {

    /**
     * 忘记密码的模板名称
     */
    private static final String FORGOT_PASSWORD_TEMPLATE = "FORGOT_PASSWORD_TEMPLATE";

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 用户存储库
     */
    private final UserRepository repository;

    /**
     * 忘记密码验证码缓存
     */
    private final ForgotPasswordCodeCache cache;

    /**
     * 忘记密码验证码生成器
     */
    private final ForgotPasswordCodeGenerator generator;

    /**
     * 构造方法初始化
     *
     * @param properties 配置文件
     * @param repository 用户存储库
     * @param cache      验证码缓存
     * @param generator  验证码生成器
     */
    public ForgotPasswordObtainServiceImpl(
            Properties properties,
            UserRepository repository,
            ForgotPasswordCodeCache cache,
            ForgotPasswordCodeGenerator generator) {
        this.cache = cache;
        this.generator = generator;
        this.properties = properties;
        this.repository = repository;
    }

    @Override
    public Mono<ForgotPasswordContext.CodeObtain.Dto> execute(
            ServerWebExchange exchange,
            ForgotPasswordContext.CodeObtain.Request param
    ) {
        final Properties.Mode mode = properties.getMode();
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> getUserModel(mode, param.getAccount())
                        .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountException(
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
                ).map(l -> new ForgotPasswordContext.CodeObtain.Dto().setAccount(param.getAccount()).setMessage(String.join(",", l)));
    }

    /**
     * 获取用户模型
     *
     * @param mode    模式
     * @param account 账号
     * @return 用户模型对象
     */
    private Mono<UserModel> getUserModel(Properties.Mode mode, String account) {
        return (switch (mode) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        });
    }
}
