package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.LoginContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Login Quick Response Code Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingLoginQuickResponseCodeService {

    /**
     * Execute Login Quick Response Code
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Login Context Quick Response Code Request Object
     * @return Login Context Quick Response Code Dto Object
     */
    LoginContext.QuickResponseCode.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCode.Request param
    );

}
