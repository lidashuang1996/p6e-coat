package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.event.PushVerificationCodeEvent;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.cache.ForgotPasswordVerificationCodeCache;
import club.p6e.coat.auth.web.repository.UserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Forgot Password Verification Code Acquisition Service Implementation
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(ForgotPasswordVerificationCodeAcquisitionService.class)
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
        if (validate(param.getAccount())) {
            return execute(httpServletRequest, param.getAccount(), param.getLanguage());
        } else {
            throw GlobalExceptionContext.exceptionAccountNotExistException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RegisterContext.Acquisition.Request param)",
                    "forgot password verification code account does not exist exception"
            );
        }
    }

    /**
     * Validate Account Exist Status
     *
     * @param account Account Content
     * @return Account Verification Result
     */
    private boolean validate(String account) {
        return switch (Properties.getInstance().getMode()) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        } != null;
    }

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
