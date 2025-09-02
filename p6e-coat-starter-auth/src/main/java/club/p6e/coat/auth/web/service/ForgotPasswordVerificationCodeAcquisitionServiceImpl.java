package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.event.PushVerificationCodeEvent;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.cache.ForgotPasswordVerificationCodeCache;
import club.p6e.coat.auth.web.repository.UserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.SpringUtil;
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
 * Forgot Password Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(ForgotPasswordVerificationCodeAcquisitionService.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class ForgotPasswordVerificationCodeAcquisitionServiceImpl implements ForgotPasswordVerificationCodeAcquisitionService {

    /**
     * FORGOT PASSWORD TEMPLATE
     */
    private static final String FORGOT_PASSWORD_TEMPLATE = "FORGOT_PASSWORD_TEMPLATE";

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Forgot Password Verification Code Cache Object
     */
    private final ForgotPasswordVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository User Repository Object
     * @param cache      Forgot Password Verification Code Cache Object
     */
    public ForgotPasswordVerificationCodeAcquisitionServiceImpl(
            UserRepository repository,
            ForgotPasswordVerificationCodeCache cache
    ) {
        this.cache = cache;
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
            request.setAttribute(VoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            cache.set(account, code);
            final PushVerificationCodeEvent event = new PushVerificationCodeEvent(this, List.of(account), FORGOT_PASSWORD_TEMPLATE, language, new HashMap<>() {{
                put("code", code);
            }});
            SpringUtil.getBean(ApplicationContext.class).publishEvent(event);
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
