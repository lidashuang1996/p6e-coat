package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.BlockingRequestParameterValidator;
import club.p6e.coat.auth.service.BlockingLoginVerificationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Login Verification Code Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingLoginVerificationCodeController.class)
@RestController("club.p6e.coat.auth.controller.BlockingLoginVerificationCodeController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginVerificationCodeController {

    /**
     * Blocking Login Verification Code Service Object
     */
    private final BlockingLoginVerificationCodeService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Login Verification Code Service Object
     */
    public BlockingLoginVerificationCodeController(BlockingLoginVerificationCodeService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Login Context Verification Code Request Object
     * @return Login Context Verification Code Request Object
     */
    private LoginContext.VerificationCode.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.VerificationCode.Request request
    ) {
        final LoginContext.VerificationCode.Request result = BlockingRequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun LoginContext.VerificationCode.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.VerificationCode.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @PostMapping(value = "/login/verification/code")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody LoginContext.VerificationCode.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getLogin().isEnable() && properties.getLogin().getQuickResponseCode().isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.VerificationCode.Request request)",
                    "login verification code is not enabled"
            );
        }
    }

}
