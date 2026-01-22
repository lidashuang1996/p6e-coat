package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingTokenGenerator {

    /**
     * Execute Token Generate
     *
     * @param request  Http Servlet Request Object
     * @param response Http Servlet Response Object
     * @param user     User Object
     * @return Result Object
     */
    Object execute(HttpServletRequest request, HttpServletResponse response, User user);

}
