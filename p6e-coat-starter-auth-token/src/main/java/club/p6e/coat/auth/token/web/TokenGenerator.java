package club.p6e.coat.auth.token.web;

import club.p6e.coat.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface TokenGenerator {

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
