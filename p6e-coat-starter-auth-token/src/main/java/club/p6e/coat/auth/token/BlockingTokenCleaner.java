package club.p6e.coat.auth.token;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingTokenCleaner {

    /**
     * Execute Token Cleaner
     *
     * @param request  Http Servlet Request Object
     * @param response Http Servlet Response Object
     * @return Result Object
     */
    Object execute(HttpServletRequest request, HttpServletResponse response);

}
