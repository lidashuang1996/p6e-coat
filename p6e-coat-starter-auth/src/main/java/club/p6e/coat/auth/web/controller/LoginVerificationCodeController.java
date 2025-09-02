package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.RequestParameterValidator;
import club.p6e.coat.auth.web.service.LoginVerificationCodeService;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Verification Code Login Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(LoginVerificationCodeController.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class LoginVerificationCodeController {

    /**
     * Request Parameter Validation
     *
     * @param httpServletRequest  Http Servlet Request Object
     * @param httpServletResponse Http Servlet Response Object
     * @param request             Login Context Verification Code Request Object
     * @return Login Context Verification Code Request Object
     */
    private LoginContext.VerificationCode.Request validate(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.VerificationCode.Request request
    ) {
        final LoginContext.VerificationCode.Request result = RequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        if (result == null) {
            throw GlobalExceptionContext.executeParameterException(
                    this.getClass(),
                    "fun LoginContext.VerificationCode.Request validate(" +
                            "HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.VerificationCode.Request request)",
                    "request parameter validation exception"
            );
        }
        return result;
    }

    @PostMapping(value = "/login/verification/code")
    public Object def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestBody LoginContext.VerificationCode.Request request
    ) {
        final LoginContext.VerificationCode.Request r = validate(httpServletRequest, httpServletResponse, request);
        return SpringUtil.getBean(LoginVerificationCodeService.class).execute(httpServletRequest, httpServletResponse, r);
    }

}
