package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.cache.LoginQuickResponseCodeCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Login Quick Response Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = LoginQuickResponseCodeAcquisitionService.class,
        ignored = LoginQuickResponseCodeAcquisitionServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@Component("club.p6e.coat.auth.web.service.LoginQuickResponseCodeAcquisitionServiceImpl")
public class LoginQuickResponseCodeAcquisitionServiceImpl implements LoginQuickResponseCodeAcquisitionService {

    /**
     * Login Quick Response Code Cache Object
     */
    private final LoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Login Quick Response Code Cache Object
     */
    public LoginQuickResponseCodeAcquisitionServiceImpl(LoginQuickResponseCodeCache cache) {
        this.cache = cache;
    }

    @Override
    public LoginContext.QuickResponseCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCodeAcquisition.Request param
    ) {
        final String code = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        httpServletRequest.setAttribute(VoucherAspect.MyHttpServletRequestWrapper.QUICK_RESPONSE_CODE_LOGIN_MARK, code);
        cache.set(code, LoginQuickResponseCodeCache.EMPTY_CONTENT);
        return new LoginContext.QuickResponseCodeAcquisition.Dto().setContent(code);
    }

}
