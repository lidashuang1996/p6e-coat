package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingForgotPasswordVerificationCodeCache;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.event.BlockingPushVerificationCodeEvent;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Blocking Forgot Password Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingForgotPasswordVerificationCodeAcquisitionService.class,
        ignored = BlockingForgotPasswordVerificationCodeAcquisitionServiceImpl.class
)
@Component("club.p6e.coat.auth.service.BlockingForgotPasswordVerificationCodeAcquisitionServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingForgotPasswordVerificationCodeAcquisitionServiceImpl implements BlockingForgotPasswordVerificationCodeAcquisitionService {

    /**
     * FORGOT PASSWORD TEMPLATE
     */
    private static final String FORGOT_PASSWORD_TEMPLATE = "FORGOT_PASSWORD_TEMPLATE";

    /**
     * Application Context Object
     */
    private final ApplicationContext context;

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
     * @param context    Application Context Object
     * @param repository Blocking User Repository Object
     * @param cache      Blocking Forgot Password Verification Code Cache Object
     */
    public BlockingForgotPasswordVerificationCodeAcquisitionServiceImpl(
            ApplicationContext context,
            BlockingUserRepository repository,
            BlockingForgotPasswordVerificationCodeCache cache
    ) {
        this.cache = cache;
        this.context = context;
        this.repository = repository;
    }

    @Override
    public ForgotPasswordContext.VerificationCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ForgotPasswordContext.VerificationCodeAcquisition.Request param
    ) {
        validate(param.getAccount());
        return execute(httpServletRequest, param.getAccount(), param.getLanguage());
    }

    /**
     * Validate Account Exist Status
     *
     * @param account Account Content
     */
    private void validate(String account) {
        final User user = switch (Properties.getInstance().getMode()) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        };
        if (user == null) {
            throw GlobalExceptionContext.exceptionAccountNoExistException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RegisterContext.Acquisition.Request param)",
                    "forgot password verification code account does not exist exception"
            );
        }
    }

    /**
     * Execute Forgot Password Verification Code Acquisition
     *
     * @param request  Http Servlet Request Object
     * @param account  Account
     * @param language Language
     * @return Forgot Password Context Verification Code Acquisition Dto Object
     */
    private ForgotPasswordContext.VerificationCodeAcquisition.Dto execute(HttpServletRequest request, String account, String language) {
        final String code = GeneratorUtil.random();
        final boolean pb = VerificationUtil.validationPhone(account);
        final boolean mb = VerificationUtil.validationMailbox(account);
        if (pb || mb) {
            request.setAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            cache.set(account, code);
            context.publishEvent(new BlockingPushVerificationCodeEvent(this, List.of(account), FORGOT_PASSWORD_TEMPLATE, language, new HashMap<>() {{
                put("code", code);
            }}));
            return new ForgotPasswordContext.VerificationCodeAcquisition.Dto().setAccount(account);
        } else {
            throw GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(HttpServletRequest request, String account, String language)",
                    "forgot password verification code account format exception"
            );
        }
    }

}
