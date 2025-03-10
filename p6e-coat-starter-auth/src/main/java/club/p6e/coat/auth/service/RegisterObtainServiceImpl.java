package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.RegisterCodeCache;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.generator.RegisterCodeGenerator;
import club.p6e.coat.auth.launcher.Launcher;
import club.p6e.coat.auth.launcher.LauncherType;
import club.p6e.coat.auth.model.UserModel;
import club.p6e.coat.auth.repository.UserRepository;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册验证码发送实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class RegisterObtainServiceImpl implements RegisterObtainService {

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * 注册验证码模板
     */
    private static final String REGISTER_TEMPLATE = "REGISTER_TEMPLATE";

    /**
     * 注册验证码缓存
     */
    private final RegisterCodeCache cache;

    /**
     * 注册验证码生成器
     */
    private final RegisterCodeGenerator generator;

    /**
     * 用户存储库
     */
    private final UserRepository userRepository;

    /**
     * 构造方法初始化
     *
     * @param properties     配置文件对象
     * @param cache          注册验证码缓存对象
     * @param generator      注册验证码生成器
     * @param userRepository 用户存储库
     */
    public RegisterObtainServiceImpl(
            Properties properties,
            RegisterCodeCache cache,
            UserRepository userRepository,
            RegisterCodeGenerator generator
    ) {
        this.cache = cache;
        this.generator = generator;
        this.properties = properties;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<RegisterContext.Obtain.Dto> execute(ServerWebExchange exchange, RegisterContext.Obtain.Request param) {
        final String account = param.getAccount();
        final Properties.Mode mode = properties.getMode();
        final boolean pb = VerificationUtil.validationPhone(param.getAccount());
        final boolean mb = VerificationUtil.validationMailbox(param.getAccount());
        final LauncherType type = pb ? LauncherType.SMS : mb ? LauncherType.EMAIL : null;
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> (switch (mode) {
                    case PHONE -> userRepository.findByPhone(param.getAccount());
                    case MAILBOX -> userRepository.findByMailbox(param.getAccount());
                    case ACCOUNT -> userRepository.findByAccount(param.getAccount());
                    case PHONE_OR_MAILBOX -> userRepository.findByPhoneOrMailbox(param.getAccount());
                })
                        .switchIfEmpty(Mono.just(new UserModel().setId(-1)))
                        .flatMap(m -> {
                            if (m != null && m.getId() != null && m.getId() > 0) {
                                return Mono.error(GlobalExceptionContext.exceptionAccountException(
                                        this.getClass(),
                                        "",
                                        ""
                                ));
                            } else {
                                return Mono.just(v);
                            }
                        }))
                .flatMap(v -> v.setAccount(account))
                .flatMap(v -> {
                    final String code = generator.execute(type.name());
                    return cache
                            .set(account, code)
                            .flatMap(b -> {
                                final Map<String, String> map = new HashMap<>();
                                map.put("code", code);
                                return Launcher.push(type, List.of(account), REGISTER_TEMPLATE, map, param.getLanguage());
                            });
                })
                .map(l -> new RegisterContext.Obtain.Dto().setAccount(account).setMessage(String.join(",", l)));
    }

}
