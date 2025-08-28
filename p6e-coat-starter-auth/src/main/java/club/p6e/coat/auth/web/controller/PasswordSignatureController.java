package club.p6e.coat.auth.web.controller;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.PasswordSignatureService;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Password Signature Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = PasswordSignatureController.class,
        ignored = PasswordSignatureController.class
)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class PasswordSignatureController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Password Signature Context Request Object
     * @return Password Signature Context Request Object
     */
    private Mono<PasswordSignatureContext.Request> validate(
            ServerWebExchange exchange, PasswordSignatureContext.Request request) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<PasswordSignatureContext.Request> validate(" +
                                "ServerWebExchange exchange, PasswordSignatureContext.Request request).",
                        "request parameter validation exception."
                )));
    }

    @GetMapping("/password/signature")
    public Mono<Object> def(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            PasswordSignatureContext.Request request
    ) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(PasswordSignatureService.class).execute(exchange, r));
    }

}
