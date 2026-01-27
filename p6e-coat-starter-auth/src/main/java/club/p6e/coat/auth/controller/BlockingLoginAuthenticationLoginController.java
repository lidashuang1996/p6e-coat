package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.BlockingRequestParameterValidator;
import club.p6e.coat.auth.service.BlockingLoginAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Authentication Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
<<<<<<< HEAD:p6e-coat-starter-auth/src/main/java/club/p6e/coat/auth/web/controller/BlockingLoginAuthenticationLoginController.java
@ConditionalOnMissingBean(BlockingLoginAuthenticationLoginController.class)
=======
@ConditionalOnMissingBean(LoginAuthenticationLoginController.class)
>>>>>>> 5317f79cc52ef26a5b430eb2353a47e797b96d61:p6e-coat-starter-auth/src/main/java/club/p6e/coat/auth/web/controller/LoginAuthenticationLoginController.java
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@RestController("club.p6e.coat.auth.web.controller.LoginAuthenticationLoginController")
public class BlockingLoginAuthenticationLoginController {

    /**
     * Login Authentication Service Object
     */
    private final BlockingLoginAuthenticationService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Authentication Service Object
     */
    public BlockingLoginAuthenticationLoginController(BlockingLoginAuthenticationService service) {
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
        final LoginContext.Authentication.Request result = BlockingRequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
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
            return "AUTHENTICATION_SUCCESS";
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.Authentication.Request request)",
                    "login authentication is not enabled"
            );
        }
    }

}
