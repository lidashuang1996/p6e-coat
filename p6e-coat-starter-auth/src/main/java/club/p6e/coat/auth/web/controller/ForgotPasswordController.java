package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.context.ForgotPasswordContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.ForgotPasswordService;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Forgot Password Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = ForgotPasswordController.class,
        ignored = ForgotPasswordController.class
)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class ForgotPasswordController {

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request  Forgot Password Context Request Object
     * @return Forgot Password Context Request Object
     */
    private ForgotPasswordContext.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            ForgotPasswordContext.Request request
    ) {
        final ForgotPasswordContext.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request)
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun ForgotPasswordContext.Request validate(HttpServletRequest httpServletRequest, " +
                            "HttpServletResponse httpServletResponse, ForgotPasswordContext.Request request)",
                    "request parameter validation exception"
            );
        }
    }

    @PostMapping("/forgot/password")
    public Mono<Object> def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody ForgotPasswordContext.Request request
    ) {
        final ForgotPasswordContext.Request r = validate(httpServletRequest, httpServletResponse, request);
        return SpringUtil.getBean(ForgotPasswordService.class).execute(httpServletRequest, httpServletResponse, r);
    }

}
