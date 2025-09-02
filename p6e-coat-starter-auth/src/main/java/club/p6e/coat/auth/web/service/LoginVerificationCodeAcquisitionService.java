package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.LoginContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Verification Code Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface LoginVerificationCodeAcquisitionService {

    /**
     * Execute Verification Code Acquisition Operation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param    Login Context Verification Code Acquisition Object
     * @return Login Context Verification Code Acquisition Dto Object
     */
    LoginContext.VerificationCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.VerificationCodeAcquisition.Request param
    );

}
