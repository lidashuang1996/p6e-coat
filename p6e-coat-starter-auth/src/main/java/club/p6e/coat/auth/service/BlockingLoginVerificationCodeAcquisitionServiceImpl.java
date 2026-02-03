package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingLoginVerificationCodeCache;
import club.p6e.coat.auth.context.LoginContext;
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
 * Blocking Login Verification Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingLoginVerificationCodeAcquisitionService.class,
        ignored = BlockingLoginVerificationCodeAcquisitionServiceImpl.class
)
@Component("club.p6e.coat.auth.service.BlockingLoginVerificationCodeAcquisitionServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginVerificationCodeAcquisitionServiceImpl implements BlockingLoginVerificationCodeAcquisitionService {

    /**
     * VERIFICATION CODE LOGIN TEMPLATE
     */
    private static final String VERIFICATION_CODE_LOGIN_TEMPLATE = "VERIFICATION_CODE_LOGIN_TEMPLATE";

    /**
     * Application Context Object
     */
    private final ApplicationContext context;

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
     * @param context    Application Context Object
     * @param repository Blocking User Repository Object
     * @param cache      Blocking Login Verification Code Cache Object
     */
    public BlockingLoginVerificationCodeAcquisitionServiceImpl(
            ApplicationContext context,
            BlockingUserRepository repository,
            BlockingLoginVerificationCodeCache cache
    ) {
        this.cache = cache;
        this.context = context;
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
            throw new AccountException(
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
        final boolean pb = VerificationUtil.validatePhone(account);
        final boolean mb = VerificationUtil.validateMailbox(account);
        if (pb || mb) {
            httpServletRequest.setAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.ACCOUNT, account);
            cache.set(account, code);
            context.publishEvent(new BlockingPushVerificationCodeEvent(this, List.of(account), VERIFICATION_CODE_LOGIN_TEMPLATE, language, new HashMap<>() {{
                put("code", code);
            }}));
            return new LoginContext.VerificationCodeAcquisition.Dto().setAccount(account);
        } else {
            throw new AccountException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, String account, String language)",
                    "login verification code acquisition account format exception"
            );
        }
    }

}

