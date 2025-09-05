package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.LoginAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = LoginAuthenticationLoginController.class,
        ignored = LoginAuthenticationLoginController.class
)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class LoginAuthenticationLoginController {

    /**
     * Login Authentication Service Object
     */
    private final LoginAuthenticationService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Authentication Service Object
     */
    public LoginAuthenticationLoginController(LoginAuthenticationService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Login Context Authentication Request Object
     * @return Login Context Authentication Request Object
     */
    private LoginContext.Authentication.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.Authentication.Request request
    ) {
        final LoginContext.Authentication.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun LoginContext.Authentication.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.Authentication.Request request)",
                    "request parameter validation exception"
            );
        }
        return request;
    }

    @PostMapping("/login/authentication")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody LoginContext.Authentication.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getLogin().isEnable()) {
            service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
            return "AUTHENTICATION";
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.Authentication.Request request)",
                    "login authentication is not enabled"
            );
        }
    }

}
