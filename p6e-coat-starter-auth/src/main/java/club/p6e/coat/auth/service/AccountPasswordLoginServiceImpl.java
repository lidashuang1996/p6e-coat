package club.p6e.coat.auth.service;

import club.p6e.coat.auth.codec.PasswordTransmissionCodec;
import club.p6e.coat.auth.repository.UserAuthRepository;
import club.p6e.coat.auth.repository.UserRepository;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.auth.AuthPasswordEncryptor;
import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.PasswordSignatureCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 账号密码登录的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class AccountPasswordLoginServiceImpl implements AccountPasswordLoginService {

    /**
     * 用户对象
     */
    private final AuthUser<?> au;

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 密码加密器
     */
    private final AuthPasswordEncryptor encryptor;

    /**
     * 用户存储库
     */
    private final UserRepository userRepository;

    /**
     * 用户密码存储库
     */
    private final UserAuthRepository userAuthRepository;

    /**
     * 构造方法初始化
     *
     * @param encryptor          密码加密器
     * @param properties         配置文件对象
     * @param userRepository     用户存储库
     * @param userAuthRepository 用户密码存储库
     */
    public AccountPasswordLoginServiceImpl(
            AuthUser<?> au,
            Properties properties,
            AuthPasswordEncryptor encryptor,
            UserRepository userRepository,
            UserAuthRepository userAuthRepository
    ) {
        this.au = au;
        this.encryptor = encryptor;
        this.properties = properties;
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
    }

    protected Mono<String> executeTransmissionDecryption(AuthVoucher voucher, String content) {
        final String mark = voucher.getAccountPasswordCodecMark();
        return Mono
                .just(mark)
                .flatMap(m -> {
                    if (SpringUtil.exist(PasswordSignatureCache.class)) {
                        return SpringUtil.getBean(PasswordSignatureCache.class).get(m);
                    } else {
                        return Mono.error(GlobalExceptionContext.exceptionBeanException(
                                this.getClass(),
                                "fun executeTransmissionDecryption(AuthVoucher voucher, String content).",
                                "Account password login transmission decryption cache handle bean not exist exception."
                        ));
                    }
                })
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun executeTransmissionDecryption(AuthVoucher voucher, String content).",
                        "Account password login transmission decryption cache data does not exist or expire exception."
                )))
                .flatMap(s -> {
                    try {
                        final PasswordTransmissionCodec codec
                                = SpringUtil.getBean(PasswordTransmissionCodec.class);
                        final PasswordTransmissionCodec.Model model
                                = JsonUtil.fromJson(s, PasswordTransmissionCodec.Model.class);

                        return Mono.just(codec.decryption(model, content));
                    } catch (Exception e) {
                        return Mono.error(GlobalExceptionContext.executeCacheException(
                                this.getClass(),
                                "fun executeTransmissionDecryption(AuthVoucher voucher, String content).",
                                "Account password login transmission decryption cache data does not exist or expire exception."
                        ));
                    }
                })
                .publishOn(Schedulers.boundedElastic())
                .doFinally(t -> SpringUtil.getBean(PasswordSignatureCache.class).del(mark).subscribe());
    }

    @Override
    public Mono<AuthUser.Model> execute(ServerWebExchange exchange, LoginContext.AccountPassword.Request param) {
        final Properties.Mode mode = properties.getMode();
        final boolean ete = properties.getLogin().getAccountPassword().isEnableTransmissionEncryption();
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> ete ? executeTransmissionDecryption(v, param.getPassword()).map(param::setPassword) : Mono.just(param))
                .flatMap(p -> switch (mode) {
                    case PHONE -> executePhoneMode(p);
                    case MAILBOX -> executeMailboxMode(p);
                    case ACCOUNT -> executeAccountMode(p);
                    case PHONE_OR_MAILBOX -> executePhoneOrMailboxMode(p);
                })
                .filter(u -> ete ? encryptor.validate(param.getPassword(), u.password()) : u.password().equals(param.getPassword()))
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountPasswordLoginAccountOrPasswordException(
                        this.getClass(),
                        "fun execute(LoginContext.AccountPassword.Request param).",
                        "Account or password exception."
                )));
    }

    /**
     * 执行手机号码登录
     *
     * @param param 请求对象
     * @return 结果对象
     */
    private Mono<AuthUser.Model> executePhoneMode(LoginContext.AccountPassword.Request param) {
        return userRepository
                .findByPhone(param.getAccount())
                .flatMap(u -> userAuthRepository.findById(u.getId()).flatMap(a -> au.create(u, a)));
    }

    /**
     * 执行邮箱登录
     *
     * @param param 请求对象
     * @return 结果对象
     */
    private Mono<AuthUser.Model> executeMailboxMode(LoginContext.AccountPassword.Request param) {
        return userRepository
                .findByMailbox(param.getAccount())
                .flatMap(u -> userAuthRepository.findById(u.getId()).flatMap(a -> au.create(u, a)));
    }

    /**
     * 执行账号登录
     *
     * @param param 请求对象
     * @return 结果对象
     */
    protected Mono<AuthUser.Model> executeAccountMode(LoginContext.AccountPassword.Request param) {
        return userRepository
                .findByAccount(param.getAccount())
                .flatMap(u -> userAuthRepository.findById(u.getId()).flatMap(a -> au.create(u, a)));
    }

    /**
     * 执行手机号码/邮箱登录
     *
     * @param param 请求对象
     * @return 结果对象
     */
    protected Mono<AuthUser.Model> executePhoneOrMailboxMode(LoginContext.AccountPassword.Request param) {
        return userRepository
                .findByPhoneOrMailbox(param.getAccount())
                .flatMap(u -> userAuthRepository.findById(u.getId()).flatMap(a -> au.create(u, a)));
    }

}
