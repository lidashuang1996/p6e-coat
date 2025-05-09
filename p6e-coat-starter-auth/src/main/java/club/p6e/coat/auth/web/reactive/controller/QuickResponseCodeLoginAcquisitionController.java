package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.QuickResponseCodeLoginAcquisitionService;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Quick Response Code Login Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@ConditionalOnMissingBean(
        value = QuickResponseCodeLoginAcquisitionController.class,
        ignored = QuickResponseCodeLoginAcquisitionController.class
)
public class QuickResponseCodeLoginAcquisitionController {

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Quick Response Code Acquisition Request Object
     * @return Login Context Quick Response Code Acquisition Request Object
     */
    private Mono<LoginContext.QuickResponseCodeAcquisition.Request> validate(
            ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request request) {
        return ServerHttpRequestParameterValidator.execute(exchange, request)
                .flatMap(o -> {
                    if (o instanceof LoginContext.QuickResponseCodeAcquisition.Request oRequest) {
                        return Mono.just(oRequest);
                    } else {
                        return Mono.error(GlobalExceptionContext.executeParameterException(
                                this.getClass(),
                                "fun Mono<LoginContext.QuickResponseCodeAcquisition.Request> validate(" +
                                        "ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request request).",
                                "request parameter validation exception."
                        ));
                    }
                });
    }

    @GetMapping("/login/quick/response/code")
    public Mono<Object> def(ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request request) {
        return validate(exchange, request).flatMap(r -> SpringUtil.getBean(QuickResponseCodeLoginAcquisitionService.class).execute(exchange, r));
    }

}
