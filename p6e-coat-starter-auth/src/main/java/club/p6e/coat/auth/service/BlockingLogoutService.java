package club.p6e.coat.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Logout Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingLogoutService {

    /**
     * Default Logout Data
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @return Result Object
     */
    Object execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);

}
