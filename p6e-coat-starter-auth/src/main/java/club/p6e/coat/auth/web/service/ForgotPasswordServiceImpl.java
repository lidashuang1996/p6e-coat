package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.password.PasswordEncryptor;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.cache.ForgotPasswordVerificationCodeCache;
import club.p6e.coat.auth.web.repository.UserRepository;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Forgot Password Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(ForgotPasswordService.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Password Encryptor Object
     */
    private final PasswordEncryptor encryptor;

    /**
     * Forgot Password Verification Code Cache Object
     */
    private final ForgotPasswordVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param encryptor Password Encryptor Object
     * @param cache     Forgot Password Verification Code Cache Object
     */
    public ForgotPasswordServiceImpl(
            UserRepository repository,
            PasswordEncryptor encryptor,
            ForgotPasswordVerificationCodeCache cache
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
            throw GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun getUser(String account)",
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
        final String account = TransformationUtil.objectToString(httpServletRequest.getAttribute(VoucherAspect.MyHttpServletRequestWrapper.ACCOUNT));
        final List<String> codes = cache.get(account);
        if (codes != null && !codes.isEmpty() && codes.contains(code)) {
            cache.del(account);
            repository.updatePassword(Integer.valueOf(getUser(account).id()), encryptor.execute(param.getPassword()));
            return new ForgotPasswordContext.Dto().setAccount(account);
        } else {
            throw GlobalExceptionContext.executeCacheException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ForgotPasswordContext.Request param)",
                    "forgot password verification code cache data does not exist or expire exception"
            );
        }
    }

}
