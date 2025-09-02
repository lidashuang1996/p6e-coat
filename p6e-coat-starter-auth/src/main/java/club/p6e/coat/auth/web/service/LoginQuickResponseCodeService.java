package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.LoginContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Quick Response Code Callback Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LoginQuickResponseCodeService {

    /**
     * Execute Quick Response Code Callback
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param Quick Response Code Callback Request Object
     * @return Login Context Quick Response Code Callback Dto Object
     */
    LoginContext.QuickResponseCodeCallback.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCode.Request param
    );

}
