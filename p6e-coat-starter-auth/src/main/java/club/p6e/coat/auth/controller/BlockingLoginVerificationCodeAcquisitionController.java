package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.BlockingRequestParameterValidator;
import club.p6e.coat.auth.service.BlockingLoginVerificationCodeAcquisitionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Login Verification Code Login Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingLoginVerificationCodeAcquisitionController.class)
@RestController("club.p6e.coat.auth.controller.BlockingLoginVerificationCodeAcquisitionController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginVerificationCodeAcquisitionController {

    /**
     * Blocking Login Verification Code Acquisition Service Object
     */
    private final BlockingLoginVerificationCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Login Verification Code Acquisition Service Object
     */
    public BlockingLoginVerificationCodeAcquisitionController(BlockingLoginVerificationCodeAcquisitionService service) {
        this.service = service;
    }

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
        final LoginContext.VerificationCodeAcquisition.Request result = BlockingRequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun LoginContext.VerificationCodeAcquisition.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.VerificationCodeAcquisition.Request request)",
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
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getLogin().isEnable() && properties.getLogin().getQuickResponseCode().isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.VerificationCodeAcquisition.Request request)",
                    "login verification code is not enabled"
            );
        }
    }

}
