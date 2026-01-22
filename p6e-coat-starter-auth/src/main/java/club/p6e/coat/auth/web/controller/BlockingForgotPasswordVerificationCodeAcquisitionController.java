package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.ForgotPasswordVerificationCodeAcquisitionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Forgot Password Verification Code Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
<<<<<<< HEAD:p6e-coat-starter-auth/src/main/java/club/p6e/coat/auth/web/controller/BlockingForgotPasswordVerificationCodeAcquisitionController.java
@ConditionalOnMissingBean(BlockingForgotPasswordVerificationCodeAcquisitionController.class)
=======
>>>>>>> 5317f79cc52ef26a5b430eb2353a47e797b96d61:p6e-coat-starter-auth/src/main/java/club/p6e/coat/auth/web/controller/ForgotPasswordVerificationCodeAcquisitionController.java
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@ConditionalOnMissingBean(ForgotPasswordVerificationCodeAcquisitionController.class)
@RestController("club.p6e.coat.auth.web.controller.ForgotPasswordVerificationCodeAcquisitionController")
public class BlockingForgotPasswordVerificationCodeAcquisitionController {

    /**
     * Forgot Password Verification Code Acquisition Service Object
     */
    private final ForgotPasswordVerificationCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Forgot Password Verification Code Acquisition Service Object
     */
    public BlockingForgotPasswordVerificationCodeAcquisitionController(ForgotPasswordVerificationCodeAcquisitionService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Forgot Password Context Verification Code Acquisition Request Object
     * @return Forgot Password Context Verification Code Acquisition Request Object
     */
    private ForgotPasswordContext.VerificationCodeAcquisition.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ForgotPasswordContext.VerificationCodeAcquisition.Request request
    ) {
        final ForgotPasswordContext.VerificationCodeAcquisition.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun ForgotPasswordContext.VerificationCodeAcquisition.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ForgotPasswordContext.VerificationCodeAcquisition.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @GetMapping(value = "/forgot/password/code")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ForgotPasswordContext.VerificationCodeAcquisition.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getForgotPassword().isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ForgotPasswordContext.VerificationCodeAcquisition.Request request)",
                    "forgot password is not enabled"
            );
        }
    }

}
