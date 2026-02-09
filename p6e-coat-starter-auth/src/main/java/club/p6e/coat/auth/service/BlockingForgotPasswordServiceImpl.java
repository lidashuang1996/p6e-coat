package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingForgotPasswordVerificationCodeCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.exception.AccountException;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Blocking Forgot Password Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingForgotPasswordService.class,
        ignored = BlockingForgotPasswordServiceImpl.class
)
@Component("club.p6e.coat.auth.service.BlockingForgotPasswordServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingForgotPasswordServiceImpl implements BlockingForgotPasswordService {

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Blocking User Repository Object
     */
    private final BlockingUserRepository repository;

    /**
     * Blocking Forgot Password Verification Code Cache Object
     */
    private final BlockingForgotPasswordVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param encryptor  Password Encryptor Object
     * @param repository Blocking User Repository Object
     * @param cache      Blocking Forgot Password Verification Code Cache Object
     */
    public BlockingForgotPasswordServiceImpl(
            PasswordEncryptor encryptor,
            BlockingUserRepository repository,
            BlockingForgotPasswordVerificationCodeCache cache
    ) {
        this.cache = cache;
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
            throw new AccountException(
                    this.getClass(),
                    "fun User getUser(String account)",
                    "forgot password account does not exist exception"
            );
        } else {
            return user;
        }
    }

    @Override
    public ForgotPasswordContext.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ForgotPasswordContext.Request param
    ) {
        final String code = param.getCode();
        final String account = TransformationUtil.objectToString(httpServletRequest.getAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT));
        final List<String> codes = cache.get(account);
        if (codes != null && !codes.isEmpty() && codes.contains(code)) {
            cache.del(account);
            repository.updatePassword(Integer.valueOf(getUser(account).id()), encryptor.execute(param.getPassword()));
            return new ForgotPasswordContext.Dto().setAccount(account);
        } else {
            throw new CacheException(
                    this.getClass(),
                    "fun ForgotPasswordContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ForgotPasswordContext.Request param)",
                    "forgot password verification code cache data does not exist or expire exception"
            );
        }
    }

}
