package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Forgot Password Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingForgotPasswordService {

    /**
     * Execute Forgot Password
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Forgot Password Context Request Object
     * @return Forgot Password Context Dto Object
     */
    ForgotPasswordContext.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ForgotPasswordContext.Request param
    );

}
