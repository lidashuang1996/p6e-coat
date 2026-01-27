package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingRegisterVerificationCodeCache;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.event.BlockingPushVerificationCodeEvent;
import club.p6e.coat.auth.repository.BlockingUserRepository;
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
 * Blocking Register Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingRegisterVerificationCodeAcquisitionService.class,
        ignored = BlockingRegisterVerificationCodeAcquisitionServiceImpl.class
)
@Component("club.p6e.coat.auth.service.BlockingRegisterVerificationCodeAcquisitionServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingRegisterVerificationCodeAcquisitionServiceImpl implements BlockingRegisterVerificationCodeAcquisitionService {

    /**
     * REGISTER TEMPLATE
     */
    private static final String REGISTER_TEMPLATE = "REGISTER_TEMPLATE";

    /**
     * Blocking User Repository Object
     */
    private final BlockingUserRepository repository;

    /**
     * Blocking Verification Code Register Cache Object
     */
    private final BlockingRegisterVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository Blocking User Repository Object
     * @param cache      Blocking Verification Code Login Cache Object
     */
    public BlockingRegisterVerificationCodeAcquisitionServiceImpl(BlockingUserRepository repository, BlockingRegisterVerificationCodeCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public RegisterContext.VerificationCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            RegisterContext.VerificationCodeAcquisition.Request param
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
            request.setAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            final BlockingPushVerificationCodeEvent event = new BlockingPushVerificationCodeEvent(this, List.of(account), REGISTER_TEMPLATE, language, new HashMap<>() {{
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
