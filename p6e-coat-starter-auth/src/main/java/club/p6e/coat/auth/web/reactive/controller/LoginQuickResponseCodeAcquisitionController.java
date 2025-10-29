package club.p6e.coat.auth.web.reactive.controller;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.RequestParameterValidator;
import club.p6e.coat.auth.web.reactive.service.LoginQuickResponseCodeAcquisitionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Quick Response Code Acquisition Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = LoginQuickResponseCodeAcquisitionController.class,
        ignored = LoginQuickResponseCodeAcquisitionController.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
@RestController("club.p6e.coat.auth.web.reactive.controller.LoginQuickResponseCodeAcquisitionController")
public class LoginQuickResponseCodeAcquisitionController {

    /**
     * Login Quick Response Code Acquisition Service Object
     */
    private final LoginQuickResponseCodeAcquisitionService service;

    /**
     * Constructor Initialization
     *
     * @param service Login Quick Response Code Acquisition Service Object
     */
    public LoginQuickResponseCodeAcquisitionController(LoginQuickResponseCodeAcquisitionService service) {
        this.service = service;
    }

    /**
     * Request Parameter Validation
     *
     * @param exchange Server Web Exchange Object
     * @param request  Login Context Quick Response Code Acquisition Request Object
     * @return Login Context Quick Response Code Acquisition Request Object
     */
    private Mono<LoginContext.QuickResponseCodeAcquisition.Request> validate(
            ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request request) {
        return RequestParameterValidator.run(exchange, request)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                        this.getClass(),
                        "fun Mono<LoginContext.QuickResponseCodeAcquisition.Request> validate(ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request request)",
                        "request parameter validation exception"
                )));
    }

    @GetMapping("/login/quick/response/code")
    public Mono<Object> def(ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request request) {
        return validate(exchange, request).flatMap(r -> service.execute(exchange, r));
    }

}
