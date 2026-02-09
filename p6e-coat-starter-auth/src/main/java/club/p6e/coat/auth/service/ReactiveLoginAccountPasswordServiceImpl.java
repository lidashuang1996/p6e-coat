package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.aspect.ReactiveVoucherAspect;
import club.p6e.coat.auth.cache.ReactivePasswordSignatureCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
import club.p6e.coat.common.exception.AccountPasswordLoginAccountOrPasswordException;
import club.p6e.coat.common.exception.BeanException;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.exception.PasswordTransmissionCodecException;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.RsaUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

/**
 * Reactive Login Account Password Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveLoginAccountPasswordService.class,
        ignored = ReactiveLoginAccountPasswordServiceImpl.class
)
@Component("club.p6e.coat.auth.service.ReactiveLoginAccountPasswordServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginAccountPasswordServiceImpl implements ReactiveLoginAccountPasswordService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Reactive Repository Object
     */
    private final ReactiveUserRepository repository;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param repository Reactive User Repository Object
     */
    public ReactiveLoginAccountPasswordServiceImpl(PasswordEncryptor encryptor, ReactiveUserRepository repository) {
        this.encryptor = encryptor;
        this.repository = repository;
    }

    /**
     * Query User By Account
     *
     * @param account Account
     * @return User Object
     */
    private Mono<User> getUser(String account) {
        return switch (Properties.getInstance().getMode()) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        };
    }

    /**
     * Execute Password Transmission Decryption
     *
     * @param exchange Server Web Exchange Object
     * @param password Password Encryption Content Object
     * @return Password Decryption Content Object
     */
    private Mono<String> executePasswordTransmissionDecryption(ServerWebExchange exchange, String password) {
        final boolean enableTransmissionEncryption = Properties.getInstance().getLogin().getAccountPassword().isEnableTransmissionEncryption();
        if (enableTransmissionEncryption) {
            final ReactivePasswordSignatureCache cache;
            if (SpringUtil.exist(ReactivePasswordSignatureCache.class)) {
                cache = SpringUtil.getBean(ReactivePasswordSignatureCache.class);
            } else {
                return Mono.error(new BeanException(
                        this.getClass(),
                        "fun executePasswordTransmissionDecryption(ServerWebExchange exchange, String password)",
                        "login account password password transmission decryption cache handle bean[" + ReactivePasswordSignatureCache.class + "] not exist exception"
                ));
            }
            final String mark = TransformationUtil.objectToString(exchange.getRequest().getAttributes().get(ReactiveVoucherAspect.MyServerHttpRequestDecorator.ACCOUNT_PASSWORD_SIGNATURE_MARK));
            return cache.get(mark)
                    .switchIfEmpty(Mono.error(new CacheException(
                            this.getClass(),
                            "fun executePasswordTransmissionDecryption(ServerWebExchange exchange, String password)",
                            "login account password password transmission decryption cache data does not exist or expire exception"
                    )))
                    .flatMap(s -> {
                        try {
                            final Map<String, String> data = JsonUtil.fromJsonToMap(s, String.class, String.class);
                            if (data != null && data.get("private") != null && !data.get("private").isEmpty()) {
                                return Mono.just(RsaUtil.privateKeyDecryption(data.get("private"), password));
                            }
                        } catch (Exception e) {
                            return Mono.error(new PasswordTransmissionCodecException(
                                    this.getClass(),
                                    "fun executePasswordTransmissionDecryption(ServerWebExchange exchange, String password)" + e.getMessage(),
                                    "login account password password transmission exception"
                            ));
                        }
                        return Mono.error(new CacheException(
                                this.getClass(),
                                "fun executePasswordTransmissionDecryption(ServerWebExchange exchange, String password)",
                                "login account password password transmission decryption cache data does not exist or expire exception"
                        ));
                    })
                    .publishOn(Schedulers.boundedElastic())
                    .doFinally(t -> cache.del(mark).subscribe());
        } else {
            return Mono.just(password);
        }
    }

    @Override
    public Mono<User> execute(ServerWebExchange exchange, LoginContext.AccountPassword.Request param) {
        return executePasswordTransmissionDecryption(exchange, param.getPassword()).map(param::setPassword)
                .flatMap(p -> getUser(p.getAccount()))
                .filter(u -> encryptor.validate(param.getPassword(), u.password()))
                .switchIfEmpty(Mono.error(new AccountPasswordLoginAccountOrPasswordException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.AccountPassword.Request param)",
                        "login account password account or password exception"
                )));
    }

}
