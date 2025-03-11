package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.repository.UserAuthRepository;
import club.p6e.coat.auth.repository.UserRepository;
import club.p6e.coat.common.utils.VerificationUtil;
import club.p6e.coat.auth.PasswordEncryptor;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.model.UserAuthModel;
import club.p6e.coat.auth.model.UserModel;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 注册服务的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class RegisterServiceImpl implements RegisterService {

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 密码加密器
     */
    private final PasswordEncryptor encryptor;

    /**
     * 模板对象
     */
    private final TransactionalOperator transactional;

    /**
     * 用户存储库
     */
    private final UserRepository userRepository;

    /**
     * 用户认证存储库
     */
    private final UserAuthRepository userAuthRepository;

    /**
     * 构造方法初始化
     *
     * @param properties         配置文件
     * @param encryptor          密码加密器
     * @param transactional      模板对象
     * @param userRepository     用户存储库
     * @param userAuthRepository 用户认证存储库
     */
    public RegisterServiceImpl(
            Properties properties,
            PasswordEncryptor encryptor,
            UserRepository userRepository,
            UserAuthRepository userAuthRepository,
            TransactionalOperator transactional
    ) {
        this.properties = properties;
        this.encryptor = encryptor;
        this.userRepository = userRepository;
        this.userAuthRepository = userAuthRepository;
        this.transactional = transactional;
    }

    @Override
    public Mono<RegisterContext.Dto> execute(ServerWebExchange exchange, RegisterContext.Request param) {
        final Properties.Mode mode = properties.getMode();
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> switch (mode) {
                    case PHONE -> executePhoneMode(v.getAccount(), param.getPassword()).flatMap(l -> v.del())
                            .flatMap(l -> v.del()).map(b -> new RegisterContext.Dto().setAccount(v.getAccount()));
                    case MAILBOX -> executeMailboxMode(v.getAccount(), param.getPassword()).flatMap(l -> v.del())
                            .flatMap(l -> v.del()).map(b -> new RegisterContext.Dto().setAccount(v.getAccount()));
                    case ACCOUNT -> executeAccountMode(v.getAccount(), param.getPassword()).flatMap(l -> v.del())
                            .flatMap(l -> v.del()).map(b -> new RegisterContext.Dto().setAccount(v.getAccount()));
                    case PHONE_OR_MAILBOX -> executePhoneOrMailboxMode(v.getAccount(), param.getPassword())
                            .flatMap(l -> v.del()).map(b -> new RegisterContext.Dto().setAccount(v.getAccount()));
                });
    }


    /**
     * 执行手机号码注册
     *
     * @return 结果对象
     */
    private Mono<String> executePhoneMode(String account, String password) {
        return userRepository
                .findByPhone(account)
                .switchIfEmpty(Mono.just(new UserModel().setId(-1)))
                .flatMap(m -> {
                    if (m != null && m.getId() != null && m.getId() > 0) {
                        return Mono.error(GlobalExceptionContext.exceptionAccountException(
                                this.getClass(),
                                "",
                                ""
                        ));
                    } else {
                        return transactional.transactional(
                                userRepository
                                        .create(new UserModel().setPhone(account))
                                        .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                                this.getClass(),
                                                "fun executePhoneMode(RegisterContext.Request param).",
                                                "[ TransactionCallback ] create user phone account exception."
                                        )))
                                        .flatMap(mm -> userAuthRepository.create(new UserAuthModel()
                                                .setId(mm.getId())
                                                .setPhone(account)
                                                .setPassword(encryptor.execute(password))
                                        ))
                        ).map(mm -> account);
                    }
                });
    }

    /**
     * 执行邮箱登录
     *
     * @return 结果对象
     */
    private Mono<String> executeMailboxMode(String account, String password) {
        return userRepository
                .findByMailbox(account)
                .switchIfEmpty(Mono.just(new UserModel().setId(-1)))
                .flatMap(m -> {
                    if (m != null && m.getId() != null && m.getId() > 0) {
                        return Mono.error(GlobalExceptionContext.exceptionAccountException(
                                this.getClass(),
                                "",
                                ""
                        ));
                    } else {
                        return transactional.transactional(
                                userRepository
                                        .create(new UserModel().setMailbox(account))
                                        .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                                this.getClass(),
                                                "fun executePhoneMode(RegisterContext.Request param).",
                                                "[ TransactionCallback ] create user phone account exception."
                                        )))
                                        .flatMap(mm -> userAuthRepository.create(new UserAuthModel()
                                                .setId(mm.getId())
                                                .setMailbox(account)
                                                .setPassword(encryptor.execute(password))
                                        ))
                        ).map(mm -> account);
                    }
                });
    }

    /**
     * 执行账号登录
     *
     * @return 结果对象
     */
    protected Mono<String> executeAccountMode(String account, String password) {
        return userRepository
                .findByAccount(account)
                .switchIfEmpty(Mono.just(new UserModel().setId(-1)))
                .flatMap(m -> {
                    if (m != null && m.getId() != null && m.getId() > 0) {
                        return Mono.error(GlobalExceptionContext.exceptionAccountException(
                                this.getClass(),
                                "",
                                ""
                        ));
                    } else {
                        return transactional.transactional(
                                userRepository
                                        .create(new UserModel().setAccount(account))
                                        .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                                this.getClass(),
                                                "fun executePhoneMode(RegisterContext.Request param).",
                                                "[ TransactionCallback ] create user phone account exception."
                                        )))
                                        .flatMap(mm -> userAuthRepository.create(new UserAuthModel()
                                                .setId(mm.getId())
                                                .setAccount(account)
                                                .setPassword(encryptor.execute(password))
                                        ))
                        ).map(mm -> account);
                    }
                });
    }

    /**
     * 执行手机号码/邮箱登录
     *
     * @return 结果对象
     */
    protected Mono<String> executePhoneOrMailboxMode(String account, String password) {
        return userRepository
                .findByPhoneOrMailbox(account)
                .switchIfEmpty(Mono.just(new UserModel().setId(-1)))
                .flatMap(m -> {
                    if (m != null && m.getId() != null && m.getId() > 0) {
                        return Mono.error(GlobalExceptionContext.exceptionAccountException(
                                this.getClass(),
                                "",
                                ""
                        ));
                    } else {
                        return transactional.transactional(
                                userRepository
                                        .create(new UserModel()
                                                .setPhone(VerificationUtil.validationPhone(account) ? account : null)
                                                .setMailbox(VerificationUtil.validationMailbox(account) ? account : null)
                                        )
                                        .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionDataBaseException(
                                                this.getClass(),
                                                "fun executePhoneMode(RegisterContext.Request param).",
                                                "[ TransactionCallback ] create user phone account exception."
                                        )))
                                        .flatMap(mm -> userAuthRepository.create(new UserAuthModel()
                                                .setId(mm.getId())
                                                .setPhone(VerificationUtil.validationPhone(account) ? account : null)
                                                .setMailbox(VerificationUtil.validationMailbox(account) ? account : null)
                                                .setPassword(encryptor.execute(password))
                                        ))
                        ).map(mm -> account);
                    }
                });
    }


}
