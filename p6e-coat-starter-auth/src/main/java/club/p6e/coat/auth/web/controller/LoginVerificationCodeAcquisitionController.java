package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.LoginVerificationCodeAcquisitionService;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verification Code Login Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(LoginVerificationCodeAcquisitionController.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class LoginVerificationCodeAcquisitionController {

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Login Context Verification Code Acquisition Request Object
     * @return Login Context Verification Code Acquisition Request Object
     */
    private LoginContext.VerificationCodeAcquisition.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.VerificationCodeAcquisition.Request request
    ) {
        final LoginContext.VerificationCodeAcquisition.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun LoginContext.VerificationCodeAcquisition.Request validate(" +
                            "HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.VerificationCodeAcquisition.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @GetMapping(value = "/login/verification/code")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.VerificationCodeAcquisition.Request request
    ) {
        final LoginContext.VerificationCodeAcquisition.Request r = validate(httpServletRequest, httpServletResponse, request);
        return SpringUtil.getBean(LoginVerificationCodeAcquisitionService.class).execute(httpServletRequest, httpServletResponse, r);
    }

}
