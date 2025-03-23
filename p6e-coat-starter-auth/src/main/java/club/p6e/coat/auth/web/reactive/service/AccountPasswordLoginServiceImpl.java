package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.*;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.repository.UserRepository;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.RsaUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.auth.web.reactive.cache.PasswordSignatureCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

/**
 * Account Password Login Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class AccountPasswordLoginServiceImpl implements AccountPasswordLoginService {

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param repository User Repository Object
     */
    public AccountPasswordLoginServiceImpl(PasswordEncryptor encryptor, UserRepository repository) {
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
        final boolean enableTransmissionEncryption = Properties.getInstance()
                .getLogin().getAccountPassword().isEnableTransmissionEncryption();
        if (enableTransmissionEncryption) {
            final PasswordSignatureCache cache;
            if (SpringUtil.exist(PasswordSignatureCache.class)) {
                cache = SpringUtil.getBean(PasswordSignatureCache.class);
            } else {
                return Mono.error(GlobalExceptionContext.exceptionBeanException(
                        this.getClass(),
                        "fun executePasswordTransmissionDecryption(ServerWebExchange exchange, String password).",
                        "account password login password transmission decryption cache " +
                                "handle bean[" + PasswordSignatureCache.class + "] not exist exception."
                ));
            }
            final ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
            final String mark = request.getAccountPasswordSignatureMark();
            return cache.get(mark)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun executePasswordTransmissionDecryption(ServerWebExchange exchange, String password).",
                            "account password login password transmission decryption cache data does not exist or expire exception."
                    )))
                    .flatMap(s -> {
                        try {
                            final Map<String, String> data = JsonUtil.fromJsonToMap(s, String.class, String.class);
                            if (data != null && data.get("private") != null && !data.get("private").isEmpty()) {
                                return Mono.just(RsaUtil.privateKeyDecryption(data.get("private"), password));
                            }
                        } catch (Exception e) {
                            // ignore exception
                        }
                        return Mono.error(GlobalExceptionContext.executeCacheException(
                                this.getClass(),
                                "fun executePasswordTransmissionDecryption(ServerWebExchange exchange, String password).",
                                "account password login password transmission decryption cache data does not exist or expire exception."
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
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionAccountPasswordLoginAccountOrPasswordException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.AccountPassword.Request param).",
                        "account password login account or password exception."
                )));
    }

}
