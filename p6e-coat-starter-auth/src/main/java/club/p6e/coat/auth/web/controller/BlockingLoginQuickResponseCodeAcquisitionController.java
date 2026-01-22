package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.LoginQuickResponseCodeAcquisitionService;
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
@ConditionalOnMissingBean(BlockingLoginQuickResponseCodeAcquisitionController.class)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
@RestController("club.p6e.coat.auth.web.controller.LoginQuickResponseCodeAcquisitionController")
public class BlockingLoginQuickResponseCodeAcquisitionController {

    /**
     * Login Quick Response Code Acquisition Service Object
     */
    private final LoginQuickResponseCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Quick Response Code Acquisition Service Object
     */
    public BlockingLoginQuickResponseCodeAcquisitionController(LoginQuickResponseCodeAcquisitionService service) {
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
        final LoginContext.QuickResponseCodeAcquisition.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
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
