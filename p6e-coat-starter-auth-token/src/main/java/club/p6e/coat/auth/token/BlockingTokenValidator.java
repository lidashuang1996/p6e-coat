package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingTokenValidator {

    /**
     * Execute Token Validate
     *
     * @param request  Http Servlet Request Object
     * @param response Http Servlet Response Object
     * @return User Object
     */
    User execute(HttpServletRequest request, HttpServletResponse response);

}
