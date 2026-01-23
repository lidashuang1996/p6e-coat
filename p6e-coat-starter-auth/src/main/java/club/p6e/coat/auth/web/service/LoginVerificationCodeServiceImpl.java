package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingLoginVerificationCodeCache;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Login Verification Code Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = LoginVerificationCodeService.class,
        ignored = LoginVerificationCodeServiceImpl.class
)
@Component("club.p6e.coat.auth.web.service.LoginVerificationCodeServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class LoginVerificationCodeServiceImpl implements LoginVerificationCodeService {

    /**
     * User Repository Object
     */
    private final BlockingUserRepository repository;

    /**
     * Login Verification Code Cache Object
     */
    private final BlockingLoginVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache      Login Verification Code Cache Object
     * @param repository User Repository Object
     */
    public LoginVerificationCodeServiceImpl(BlockingUserRepository repository, BlockingLoginVerificationCodeCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    /**
     * Query User By Account
     *
     * @param account Account
     * @return User Object
     */
    private User getUser(String account) {
        return switch (Properties.getInstance().getMode()) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        };
    }

    @Override
    public User execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.VerificationCode.Request param
    ) {
        final String code = param.getCode();
        final String account = TransformationUtil.objectToString(httpServletRequest.getAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT));
        final List<String> codes = cache.get(account);
        if (codes == null || codes.isEmpty()) {
            throw GlobalExceptionContext.executeCacheException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.VerificationCode.Request param)",
                    "login verification code cache data does not exist or expire exception"
            );
        }
        if (codes.contains(code)) {
            cache.del(account);
            return getUser(account);
        } else {
            throw GlobalExceptionContext.executeCacheException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.VerificationCode.Request param)",
                    "login verification code cache data does not exist or expire exception"
            );
        }
    }

}
