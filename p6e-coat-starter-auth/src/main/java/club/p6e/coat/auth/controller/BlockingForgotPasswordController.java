package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.BlockingRequestParameterValidator;
import club.p6e.coat.auth.service.BlockingForgotPasswordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Forgot Password Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingForgotPasswordController.class)
@RestController("club.p6e.coat.auth.controller.BlockingForgotPasswordController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingForgotPasswordController {

    /**
     * Blocking Forgot Password Service Object
     */
    private final BlockingForgotPasswordService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Forgot Password Service Object
     */
    public BlockingForgotPasswordController(BlockingForgotPasswordService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Forgot Password Context Request Object
     * @return Forgot Password Context Request Object
     */
    private ForgotPasswordContext.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ForgotPasswordContext.Request request
    ) {
        final ForgotPasswordContext.Request result = BlockingRequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun ForgotPasswordContext.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ForgotPasswordContext.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @PostMapping("/forgot/password")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody ForgotPasswordContext.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getForgotPassword().isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ForgotPasswordContext.Request request)",
                    "forgot password is not enabled"
            );
        }
    }

}
