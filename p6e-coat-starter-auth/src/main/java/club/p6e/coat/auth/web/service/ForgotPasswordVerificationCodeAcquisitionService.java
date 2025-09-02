package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Forgot Password Verification Code Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ForgotPasswordVerificationCodeAcquisitionService {

    /**
     * Execute Forgot Password Verification Code Acquisition
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Forgot Password Context Verification Code Acquisition Request Object
     * @return Forgot Password Context Verification Code Acquisition Dto Object
     */
    ForgotPasswordContext.VerificationCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ForgotPasswordContext.VerificationCodeAcquisition.Request param
    );

}
