package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.RegisterVerificationCodeAcquisitionService;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verification Code Register Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(RegisterVerificationCodeAcquisitionController.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class RegisterVerificationCodeAcquisitionController {

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Register Context Verification Code Acquisition Request Object
     * @return Register Context Verification Code Acquisition Request Object
     */
    private RegisterContext.VerificationCodeAcquisition.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            RegisterContext.VerificationCodeAcquisition.Request request
    ) {
        final RegisterContext.VerificationCodeAcquisition.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun RegisterContext.VerificationCodeAcquisition.Request validate(" +
                            "HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RegisterContext.VerificationCodeAcquisition.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @GetMapping(value = "/register/code")
    public Object def(HttpServletRequest httpServletRequest,
                      HttpServletResponse httpServletResponse,
                      RegisterContext.VerificationCodeAcquisition.Request request) {
        final RegisterContext.VerificationCodeAcquisition.Request r = validate(httpServletRequest, httpServletResponse, request);
        return SpringUtil.getBean(RegisterVerificationCodeAcquisitionService.class).execute(httpServletRequest, httpServletResponse, r);
    }

}
