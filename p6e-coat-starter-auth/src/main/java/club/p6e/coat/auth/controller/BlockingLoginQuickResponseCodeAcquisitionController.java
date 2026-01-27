package club.p6e.coat.auth.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.validator.BlockingRequestParameterValidator;
import club.p6e.coat.auth.service.BlockingLoginQuickResponseCodeAcquisitionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Quick Response Code Login Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
<<<<<<< HEAD:p6e-coat-starter-auth/src/main/java/club/p6e/coat/auth/web/controller/BlockingLoginQuickResponseCodeAcquisitionController.java
@ConditionalOnMissingBean(BlockingLoginQuickResponseCodeAcquisitionController.class)
=======
@ConditionalOnMissingBean(LoginQuickResponseCodeAcquisitionController.class)
>>>>>>> 5317f79cc52ef26a5b430eb2353a47e797b96d61:p6e-coat-starter-auth/src/main/java/club/p6e/coat/auth/web/controller/LoginQuickResponseCodeAcquisitionController.java
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@RestController("club.p6e.coat.auth.web.controller.LoginQuickResponseCodeAcquisitionController")
public class BlockingLoginQuickResponseCodeAcquisitionController {

    /**
     * Login Quick Response Code Acquisition Service Object
     */
    private final BlockingLoginQuickResponseCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Quick Response Code Acquisition Service Object
     */
    public BlockingLoginQuickResponseCodeAcquisitionController(BlockingLoginQuickResponseCodeAcquisitionService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Login Context Quick Response Code Acquisition Request Object
     * @return Login Context Quick Response Code Acquisition Request Object
     */
    private LoginContext.QuickResponseCodeAcquisition.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCodeAcquisition.Request request
    ) {
        final LoginContext.QuickResponseCodeAcquisition.Request result = BlockingRequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun LoginContext.QuickResponseCodeAcquisition.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeAcquisition.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @GetMapping("/login/quick/response/code")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCodeAcquisition.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getLogin().isEnable() && properties.getLogin().getQuickResponseCode().isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeAcquisition.Request request)",
                    "login quick response code is not enabled"
            );
        }
    }

}
