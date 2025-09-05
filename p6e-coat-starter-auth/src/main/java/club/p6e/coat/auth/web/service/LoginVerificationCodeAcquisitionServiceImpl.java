package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.cache.LoginVerificationCodeCache;
import club.p6e.coat.auth.web.event.PushVerificationCodeEvent;
import club.p6e.coat.auth.web.repository.UserRepository;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

/**
 * Login Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = LoginVerificationCodeAcquisitionService.class,
        ignored = LoginVerificationCodeAcquisitionServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class LoginVerificationCodeAcquisitionServiceImpl implements LoginVerificationCodeAcquisitionService {

    /**
     * VERIFICATION CODE LOGIN TEMPLATE
     */
    private static final String VERIFICATION_CODE_LOGIN_TEMPLATE = "VERIFICATION_CODE_LOGIN_TEMPLATE";

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Login Verification Code Cache Object
     */
    private final LoginVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository User Repository Object
     * @param cache      Login Verification Code Cache Object
     */
    public LoginVerificationCodeAcquisitionServiceImpl(
            UserRepository repository,
            LoginVerificationCodeCache cache
    ) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public LoginContext.VerificationCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.VerificationCodeAcquisition.Request param
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
        if (switch (Properties.getInstance().getMode()) {
            case PHONE -> repository.findByPhone(account);
            case MAILBOX -> repository.findByMailbox(account);
            case ACCOUNT -> repository.findByAccount(account);
            case PHONE_OR_MAILBOX -> repository.findByPhoneOrMailbox(account);
        } == null) {
            throw GlobalExceptionContext.exceptionAccountNoExistException(
                    this.getClass(),
                    "fun validate(String account)",
                    "login verification code acquisition account does not exist exception"
            );
        }
    }

    /**
     * Execute Login Verification Code Acquisition
     *
     * @param httpServletRequest Http Servlet Request Object
     * @param account            Account Content
     * @param language           Language Content
     * @return Login Context Verification Code Acquisition Dto Object
     */
    private LoginContext.VerificationCodeAcquisition.Dto execute(HttpServletRequest httpServletRequest, String account, String language) {
        final String code = GeneratorUtil.random();
        final boolean pb = VerificationUtil.validationPhone(account);
        final boolean mb = VerificationUtil.validationMailbox(account);
        if (pb || mb) {
            httpServletRequest.setAttribute(VoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            cache.set(account, code);
            final PushVerificationCodeEvent event = new PushVerificationCodeEvent(this, List.of(account), VERIFICATION_CODE_LOGIN_TEMPLATE, language, new HashMap<>() {{
                put("code", code);
            }});
            SpringUtil.getApplicationContext().publishEvent(event);
            return new LoginContext.VerificationCodeAcquisition.Dto().setAccount(account);
        } else {
            throw GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, String account, String language)",
                    "login verification code acquisition account format exception"
            );
        }
    }

}

