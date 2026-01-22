package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.RegisterContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.RegisterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Blocking Register Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(BlockingRegisterController.class)
@RestController("club.p6e.coat.auth.web.controller.RegisterController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingRegisterController {

    /**
     * Register Service Object
     */
    private final RegisterService service;

    /**
     * Constructor Initialization
     *
     * @param service Register Service Object
     */
    public BlockingRegisterController(RegisterService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Password Signature Context Request Object
     * @return Password Signature Context Request Object
     */
    private RegisterContext.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            RegisterContext.Request request
    ) {
        final RegisterContext.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun RegisterContext.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RegisterContext.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @PostMapping("/register")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody RegisterContext.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable() && properties.getRegister().isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw GlobalExceptionContext.exceptionServiceNoEnabledException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RegisterContext.Request request)",
                    "register is not enabled"
            );
        }
    }

}
