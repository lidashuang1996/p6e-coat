package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.RegisterContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Register Verification Code Acquisition Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface RegisterVerificationCodeAcquisitionService {

    /**
     * Execute Register Verification Code Acquisition
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param param               Register Context Verification Code Acquisition Request Object
     * @return Register Context Verification Code Acquisition Dto Object
     */
    RegisterContext.VerificationCodeAcquisition.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            RegisterContext.VerificationCodeAcquisition.Request param
    );

}
