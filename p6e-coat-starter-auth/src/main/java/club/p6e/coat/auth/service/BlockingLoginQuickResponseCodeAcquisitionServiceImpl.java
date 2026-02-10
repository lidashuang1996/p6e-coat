package club.p6e.coat.auth.service;

import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingLoginQuickResponseCodeCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.common.utils.GeneratorUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Blocking Login Quick Response Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.service.BlockingLoginQuickResponseCodeAcquisitionServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginQuickResponseCodeAcquisitionServiceImpl implements BlockingLoginQuickResponseCodeAcquisitionService {

    /**
     * Blocking Login Quick Response Code Cache Object
     */
    private final BlockingLoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Blocking Login Quick Response Code Cache Object
     */
    public BlockingLoginQuickResponseCodeAcquisitionServiceImpl(BlockingLoginQuickResponseCodeCache cache) {
        this.cache = cache;
    }

    @Override
    public LoginContext.QuickResponseCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCodeAcquisition.Request param
    ) {
        final String code = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        httpServletRequest.setAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.QUICK_RESPONSE_CODE_LOGIN_MARK, code);
        cache.set(code, BlockingLoginQuickResponseCodeCache.EMPTY_CONTENT);
        return new LoginContext.QuickResponseCodeAcquisition.Dto().setContent(code);
    }

}
