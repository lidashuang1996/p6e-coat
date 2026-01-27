package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Login Verification Code Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingLoginVerificationCodeService {

    /**
     * Execute Login Verification Code
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Login Context Verification Code Request Object
     * @return User Object
     */
    User execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.VerificationCode.Request param
    );

}
