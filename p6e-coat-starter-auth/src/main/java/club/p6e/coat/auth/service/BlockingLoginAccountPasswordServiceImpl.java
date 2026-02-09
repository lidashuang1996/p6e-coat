package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingPasswordSignatureCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.exception.AccountPasswordLoginAccountOrPasswordException;
import club.p6e.coat.common.exception.AccountPasswordLoginTransmissionException;
import club.p6e.coat.common.exception.BeanException;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.RsaUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Blocking Login Account Password Login Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingLoginAccountPasswordService.class,
        ignored = BlockingLoginAccountPasswordServiceImpl.class
)
@Component("club.p6e.coat.auth.service.BlockingLoginAccountPasswordServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginAccountPasswordServiceImpl implements BlockingLoginAccountPasswordService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Blocking User Repository Object
     */
    private final BlockingUserRepository repository;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param repository Blocking User Repository Object
     */
    public BlockingLoginAccountPasswordServiceImpl(PasswordEncryptor encryptor, BlockingUserRepository repository) {
        this.encryptor = encryptor;
        this.repository = repository;
    }

    /**
     * Query User By Account
     *
     * @param account Account
     * @return User Object
     */
    private User getUser(String account) {
        final User user = switch (Properties.getInstance().getMode()) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        };
        if (user == null) {
            throw new AccountPasswordLoginAccountOrPasswordException(
                    this.getClass(),
                    "fun getUser(String account)",
                    "login account password account or password exception"
            );
        } else {
            return user;
        }
    }

    /**
     * Execute Password Transmission Decryption
     *
     * @param request  Http Servlet Request Object
     * @param password Password Encryption Content Object
     * @return Password Decryption Content Object
     */
    private String executePasswordTransmissionDecryption(HttpServletRequest request, String password) {
        final boolean enableTransmissionEncryption = Properties.getInstance().getLogin().getAccountPassword().isEnableTransmissionEncryption();
        if (enableTransmissionEncryption) {
            final BlockingPasswordSignatureCache cache;
            if (SpringUtil.exist(BlockingPasswordSignatureCache.class)) {
                cache = SpringUtil.getBean(BlockingPasswordSignatureCache.class);
            } else {
                throw new BeanException(
                        this.getClass(),
                        "fun executePasswordTransmissionDecryption(HttpServletRequest request, String password)",
                        "login account password password transmission decryption cache handle bean[" + BlockingPasswordSignatureCache.class + "] not exist exception"
                );
            }
            final String mark = TransformationUtil.objectToString(request.getAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT_PASSWORD_SIGNATURE_MARK));
            try {
                final String content = cache.get(mark);
                if (content == null || content.isEmpty()) {
                    throw new CacheException(
                            this.getClass(),
                            "fun executePasswordTransmissionDecryption(HttpServletRequest request, String password)",
                            "login account password password transmission decryption cache data does not exist or expire exception"
                    );
                } else {
                    try {
                        final Map<String, String> data = JsonUtil.fromJsonToMap(content, String.class, String.class);
                        if (data != null && data.get("private") != null && !data.get("private").isEmpty()) {
                            return RsaUtil.privateKeyDecryption(data.get("private"), password);
                        }
                    } catch (Exception e) {
                        throw new AccountPasswordLoginTransmissionException(
                                this.getClass(),
                                "fun executePasswordTransmissionDecryption(HttpServletRequest request, String password) -> " + e.getMessage(),
                                "login account password password transmission exception"
                        );
                    }
                }
            } finally {
                cache.del(mark);
            }
        }
        return password;
    }

    @Override
    public User execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.AccountPassword.Request param
    ) {
        param.setPassword(executePasswordTransmissionDecryption(httpServletRequest, param.getPassword()));
        final User user = getUser(param.getAccount());
        if (encryptor.validate(param.getPassword(), user.password())) {
            return user;
        } else {
            throw new AccountPasswordLoginAccountOrPasswordException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.AccountPassword.Request param)",
                    "login account password account or password exception"
            );
        }
    }

}
