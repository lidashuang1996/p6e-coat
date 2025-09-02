package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.LoginContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Quick Response Code Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LoginQuickResponseCodeAcquisitionService {

    /**
     * Execute Quick Response Code Acquisition
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Login Context Quick Response Code Acquisition Request Object
     * @return Login Context Quick Response Code Acquisition Dto Object
     */
    LoginContext.QuickResponseCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCodeAcquisition.Request param
    );

}
