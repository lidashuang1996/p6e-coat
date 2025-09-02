package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.PasswordSignatureService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Password Signature Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(PasswordSignatureController.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class PasswordSignatureController {

    /**
     * Password Signature Service Object
     */
    private final PasswordSignatureService service;

    /**
     * Constructor Initialization
     *
     * @param service Password Signature Service Object
     */
    public PasswordSignatureController(PasswordSignatureService service) {
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
    private PasswordSignatureContext.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            PasswordSignatureContext.Request request
    ) {
        final PasswordSignatureContext.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun PasswordSignatureContext.Request validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, PasswordSignatureContext.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @GetMapping("/password/signature")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            PasswordSignatureContext.Request request
    ) {
        final Properties properties = Properties.getInstance();
        if (properties.isEnable()) {
            return service.execute(httpServletRequest, httpServletResponse, validate(httpServletRequest, httpServletResponse, request));
        } else {
            throw GlobalExceptionContext.executeNoEnableException(
                    this.getClass(),
                    "fun Object def(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCode.Request request)",
                    "password signature is not enabled"
            );
        }
    }

}
