package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingRegisterVerificationCodeCache;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.event.BlockingPushVerificationCodeEvent;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.error.AccountException;
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
     * Application Context Object
     */
    private final ApplicationContext context;

    /**
     * Blocking User Repository Object
     */
    private final BlockingUserRepository repository;

    /**
     * Blocking Register Verification Code Cache Object
     */
    private final BlockingRegisterVerificationCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param context    Application Context Object
     * @param repository Blocking User Repository Object
     * @param cache      Blocking Register Verification Code Cache Object
     */
    public BlockingRegisterVerificationCodeAcquisitionServiceImpl(ApplicationContext context, BlockingUserRepository repository, BlockingRegisterVerificationCodeCache cache) {
        this.cache = cache;
        this.context = context;
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
            throw new AccountException(
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
        final boolean pb = VerificationUtil.validatePhone(account);
        final boolean mb = VerificationUtil.validateMailbox(account);
        if (pb || mb) {
            final String code = GeneratorUtil.random();
            cache.set(account, code);
            request.setAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            context.publishEvent(new BlockingPushVerificationCodeEvent(this, List.of(account), REGISTER_TEMPLATE, language, new HashMap<>() {{
                put("code", code);
            }}));
            return new RegisterContext.VerificationCodeAcquisition.Dto().setAccount(account);
        } else {
            throw new AccountException(
                    this.getClass(),
                    "fun execute(HttpServletRequest request, String account, String language)",
                    "register verification code acquisition account format exception"
            );
        }
    }

}
