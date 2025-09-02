package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.LoginQuickResponseCodeCallbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Quick Response Code Login Callback Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(LoginQuickResponseCodeCallbackController.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class LoginQuickResponseCodeCallbackController {

    /**
     * Login Quick Response Code Callback Service Object
     */
    private final LoginQuickResponseCodeCallbackService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Quick Response Code Callback Service Object
     */
    public LoginQuickResponseCodeCallbackController(LoginQuickResponseCodeCallbackService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Login Context Quick Response Code Callback Request Object
     * @return Login Context Quick Response Code Callback Request Object
     */
    private LoginContext.QuickResponseCodeCallback.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCodeCallback.Request request
    ) {
        final LoginContext.QuickResponseCodeCallback.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun LoginContext.QuickResponseCodeCallback.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @GetMapping("/login/quick/response/info")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCodeCallback.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getLogin().isEnable() && properties.getLogin().getQuickResponseCode().isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request request)",
                    "login quick response code is not enabled"
            );
        }
    }

}
