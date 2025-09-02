package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.event.PushMessageEvent;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.cache.RegisterVerificationCodeCache;
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
 * Register Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(RegisterVerificationCodeAcquisitionService.class)
public class RegisterVerificationCodeAcquisitionServiceImpl implements RegisterVerificationCodeAcquisitionService {

    /**
     * REGISTER TEMPLATE
     */
    private static final String REGISTER_TEMPLATE = "REGISTER_TEMPLATE";

    /**
     * User Repository Object
     */
    private final UserRepository repository;

    /**
     * Verification Code Register Cache Object
     */
    private final RegisterVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository User Repository Object
     * @param cache      Verification Code Login Cache Object
     */
    public RegisterVerificationCodeAcquisitionServiceImpl(UserRepository repository, RegisterVerificationCodeCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public RegisterContext.VerificationCodeAcquisition.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RegisterContext.VerificationCodeAcquisition.Request param) {
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
            throw GlobalExceptionContext.exceptionAccountNotExistException(
                    this.getClass(),
                    "fun validate(String account)",
                    "register verification code acquisition account does not exist exception"
            );
        }
    }

    /**
     * Execute Register Verification Code Push
     *
     * @param request  Http Servlet Request Object
     * @param account  Account Content
     * @param language Language Content
     * @return Register Context Acquisition Dto Object
     */
    private RegisterContext.VerificationCodeAcquisition.Dto execute(HttpServletRequest request, String account, String language) {
        final boolean pb = VerificationUtil.validationPhone(account);
        final boolean mb = VerificationUtil.validationMailbox(account);
        if (pb || mb) {
            final String code = GeneratorUtil.random();
            cache.set(account, code);
            request.setAttribute(VoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            final PushMessageEvent event = new PushMessageEvent(this, List.of(account), REGISTER_TEMPLATE, language, new HashMap<>() {{
                put("code", code);
            }});
            SpringUtil.getBean(ApplicationContext.class).publishEvent(event);
            return new RegisterContext.VerificationCodeAcquisition.Dto().setAccount(account);
        } else {
            throw GlobalExceptionContext.exceptionAccountException(
                    this.getClass(),
                    "fun execute(HttpServletRequest request, String account, String language)",
                    "register verification code acquisition account format exception"
            );
        }
    }

}
