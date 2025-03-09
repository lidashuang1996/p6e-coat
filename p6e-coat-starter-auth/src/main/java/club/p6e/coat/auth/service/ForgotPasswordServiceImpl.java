package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthPasswordEncryptor;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.ForgotPasswordCodeCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.repository.UserAuthRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 忘记密码服务的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 忘记密码的验证码缓存
     */
    private final ForgotPasswordCodeCache cache;

    /**
     * 用户密码存储库
     */
    private final UserAuthRepository repository;

    /**
     * 密码加密器
     */
    private final AuthPasswordEncryptor encryptor;

    /**
     * 构造方法初始化
     *
     * @param cache      忘记密码的验证码缓存
     * @param properties 配置文件对象
     * @param repository 用户存储库
     * @param encryptor  密码加密器
     */
    public ForgotPasswordServiceImpl(
            Properties properties,
            ForgotPasswordCodeCache cache,
            UserAuthRepository repository,
            AuthPasswordEncryptor encryptor
    ) {
        this.cache = cache;
        this.encryptor = encryptor;
        this.properties = properties;
        this.repository = repository;
    }

    @Override
    public Mono<ForgotPasswordContext.Dto> execute(ServerWebExchange exchange, ForgotPasswordContext.Request param) {
        final Properties.Mode mode = properties.getMode();
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> cache
                        .get(v.getAccount())
                        .filter(l -> l.contains(param.getCode()))
                        .flatMap(l -> cache.del(v.getAccount()).map(ll -> v))
                )
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionForgotPasswordCodeException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, ForgotPasswordContext.Request param).",
                        "forgot password submit verification code expired."
                ))).flatMap(v -> (switch (mode) {
                            case PHONE -> repository.findByPhone(v.getAccount());
                            case MAILBOX -> repository.findByMailbox(v.getAccount());
                            case ACCOUNT -> repository.findByAccount(v.getAccount());
                            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(v.getAccount());
                        }).flatMap(m -> repository.updatePassword(
                                m.getId(), encryptor.execute(param.getPassword())
                        )).map(l -> new ForgotPasswordContext.Dto().setAccount(v.getAccount()))
                );
    }

}
