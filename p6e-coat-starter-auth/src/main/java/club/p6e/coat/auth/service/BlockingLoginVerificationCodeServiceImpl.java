package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingLoginVerificationCodeCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Blocking Login Verification Code Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingLoginVerificationCodeService.class,
        ignored = BlockingLoginVerificationCodeServiceImpl.class
)
@Component("club.p6e.coat.auth.service.BlockingLoginVerificationCodeServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginVerificationCodeServiceImpl implements BlockingLoginVerificationCodeService {

    /**
     * Blocking User Repository Object
     */
    private final BlockingUserRepository repository;

    /**
     * Blocking Login Verification Code Cache Object
     */
    private final BlockingLoginVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository Blocking User Repository Object
     * @param cache      Blocking Login Verification Code Cache Object
     */
    public BlockingLoginVerificationCodeServiceImpl(BlockingUserRepository repository, BlockingLoginVerificationCodeCache cache) {
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
