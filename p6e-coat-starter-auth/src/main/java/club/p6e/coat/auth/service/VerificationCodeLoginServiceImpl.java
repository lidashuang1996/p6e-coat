package club.p6e.coat.auth.service;

import club.p6e.coat.auth.cache.VerificationCodeLoginCache;
import club.p6e.coat.auth.repository.UserRepository;
import club.p6e.coat.auth.AuthUser;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 验证码登录服务的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class VerificationCodeLoginServiceImpl implements VerificationCodeLoginService {

    /**
     * 用户认证对象
     */
    private final AuthUser<?> au;

    /**
     * 用户存储库
     */
    private final UserRepository repository;

    /**
     * 验证码缓存对象
     */
    private final VerificationCodeLoginCache cache;

    /**
     * 构造方法初始化
     *
     * @param cache      验证码缓存对象
     * @param repository 用户存储库
     */
    public VerificationCodeLoginServiceImpl(
            AuthUser<?> au,
            UserRepository repository,
            VerificationCodeLoginCache cache
    ) {
        this.au = au;
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<AuthUser.Model> execute(ServerWebExchange exchange, LoginContext.VerificationCode.Request param) {
        final String code = param.getCode();
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> {
                    final String account = v.getAccount();
                    final String accountType = v.getAccountType();
                    return cache
                            .get(account)
                            .switchIfEmpty(Mono.error(
                                    GlobalExceptionContext.executeCacheException(
                                            this.getClass(),
                                            "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeObtain.Request param)",
                                            "Verification code login cache data does not exist or expire exception."
                                    )))
                            .flatMap(list -> {
                                if (list != null && !list.isEmpty()) {
                                    final int index = list.indexOf(code);
                                    if (index >= 0) {
                                        return cache.del(account);
                                    }
                                }
                                return Mono.error(
                                        GlobalExceptionContext.executeCacheException(
                                                this.getClass(),
                                                "fun execute(ServerWebExchange exchange, LoginContext.VerificationCodeObtain.Request param)",
                                                "Verification code login cache data does not exist or expire exception."
                                        ));
                            })
                            .flatMap(r -> switch (Properties.Mode.create(accountType)) {
                                case PHONE -> repository.findByPhone(account);
                                case MAILBOX -> repository.findByMailbox(account);
                                case ACCOUNT -> repository.findByAccount(account);
                                case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
                            })
                            .flatMap(m -> au.create(m, null));
                });

    }
}
