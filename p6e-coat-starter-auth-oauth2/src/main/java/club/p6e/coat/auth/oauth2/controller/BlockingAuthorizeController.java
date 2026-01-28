package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.Properties;
import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.service.BlockingAuthorizeService;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.error.ServiceNotEnableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Authorize Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingAuthorizeController.class)
@RestController("club.p6e.coat.auth.controller.BlockingForgotPasswordController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingAuthorizeController {

    /**
     * Blocking Authorize Service Object
     */
    private final BlockingAuthorizeService service;

    /**
     * Constructor Initialization
     *
     * @param service Blocking Authorize Service Object
     */
    public BlockingAuthorizeController(BlockingAuthorizeService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Authorize Context Request Object
     * @return Forgot Password Context Request Object
     */
    private AuthorizeContext.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            AuthorizeContext.Request request
    ) {
        final ForgotPasswordContext.Request result = BlockingRequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun ForgotPasswordContext.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ForgotPasswordContext.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }
    
    @PostMapping("/oauth2/authorize")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            AuthorizeContext.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getAuthorizationCode().isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw new ServiceNotEnableException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthorizeContext.Request request)",
                    "forgot password is not enabled"
            );
        }
    }

}
