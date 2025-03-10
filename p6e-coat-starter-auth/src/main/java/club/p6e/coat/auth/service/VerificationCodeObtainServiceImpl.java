package club.p6e.coat.auth.service;

import club.p6e.coat.auth.cache.WebFluxVerificationCodeLoginCache;
import club.p6e.coat.auth.launcher.Launcher;
import club.p6e.coat.auth.launcher.LauncherType;
import club.p6e.coat.auth.repository.WebFluxUserRepository;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.generator.VerificationCodeLoginGenerator;
import club.p6e.coat.auth.model.UserModel;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;

/**
 * 验证码获取服务的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeObtainServiceImpl implements VerificationCodeObtainService {

    /**
     * CODE LOGIN 模板
     */
    private static final String CODE_LOGIN_TEMPLATE = "CODE_LOGIN_TEMPLATE";

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 用户存储库
     */
    private final WebFluxUserRepository repository;

    /**
     * 验证码缓存对象
     */
    private final WebFluxVerificationCodeLoginCache cache;

    /**
     * 验证码生成对象
     */
    private final VerificationCodeLoginGenerator generator;

    /**
     * 构造方法初始化
     *
     * @param properties 配置文件对象
     * @param repository 用户存储库对象
     * @param cache      验证码缓存对象
     * @param generator  验证码生成器对象
     */
    public VerificationCodeObtainServiceImpl(
            Properties properties,
            WebFluxUserRepository repository,
            WebFluxVerificationCodeLoginCache cache,
            VerificationCodeLoginGenerator generator
    ) {
        this.cache = cache;
        this.generator = generator;
        this.properties = properties;
        this.repository = repository;
    }

    @Override
    public Mono<LoginContext.VerificationCodeObtain.Dto> execute(
            ServerWebExchange exchange, LoginContext.VerificationCodeObtain.Request param) {
        Mono<UserModel> mono;
        LauncherType type = null;
        final String account = param.getAccount();
        switch (properties.getMode()) {
            case PHONE -> {
                type = LauncherType.SMS;
                mono = repository.findByPhone(account);
            }
            case MAILBOX -> {
                type = LauncherType.EMAIL;
                mono = repository.findByMailbox(account);
            }
            case PHONE_OR_MAILBOX -> {
                if (VerificationUtil.validationPhone(account)) {
                    type = LauncherType.SMS;
                } else if (VerificationUtil.validationMailbox(account)) {
                    type = LauncherType.EMAIL;
                }
                mono = repository.findByPhoneOrMailbox(account);
            }
            default -> {
                return Mono.error(GlobalExceptionContext.executeServiceNotSupportException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeObtain.Request param).",
                        "Verification code obtain service not supported. [" + account + "]."
                ));
            }
        }
        if (type == null) {
            return Mono.error(GlobalExceptionContext.executeServiceNotSupportException(
                    this.getClass(),
                    "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeObtain.Request param).",
                    "Verification code obtain <type> service not supported. [" + account + "]."
            ));
        } else {
            final LauncherType ft = type;
            final Mono<UserModel> fm = mono;
            final String code = generator.execute(type.name());
            return AuthVoucher
                    .init(exchange)
                    .flatMap(v -> fm
                            .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountException(
                                    this.getClass(),
                                    "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeObtain.Request param).",
                                    "Verification code login obtain code account not exist exception."
                            )))
                            .flatMap(u -> cache.set(account, code))
                            .flatMap(b -> {
                                if (b) {
                                    return Launcher.push(ft, List.of(account), CODE_LOGIN_TEMPLATE, new HashMap<>(1) {{
                                        put("code", code);
                                    }}, param.getLanguage());
                                } else {
                                    return Mono.error(GlobalExceptionContext.executeCacheException(
                                            this.getClass(),
                                            "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeObtain.Request param).",
                                            "Verification code obtain write cache error."
                                    ));
                                }
                            })
                            .flatMap(l -> v.setVerificationCodeData(account, ft.name(), String.valueOf(l)).map(vv -> l))
                    ).map(m -> new LoginContext.VerificationCodeObtain.Dto().setAccount(account).setMessage(JsonUtil.toJson(m)));
        }
    }

}

