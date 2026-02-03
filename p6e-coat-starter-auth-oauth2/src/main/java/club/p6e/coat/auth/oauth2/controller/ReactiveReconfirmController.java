package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.context.ReconfirmContext;
import club.p6e.coat.auth.oauth2.service.ReactiveReconfirmService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

/**
 * Reactive Reconfirm Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveReconfirmController.class)
@RestController("club.p6e.coat.auth.oauth2.controller.ReactiveReconfirmController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveReconfirmController {

    /**
     * Reactive Reconfirm Service Object
     */
    private final ReactiveReconfirmService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Reconfirm Service Object
     */
    public ReactiveReconfirmController(ReactiveReconfirmService service) {
        this.service = service;
    }

    @PostMapping("/oauth2/reconfirm")
    public Object def(ServerWebExchange exchange, ReconfirmContext.Request request) {
        return service.execute(exchange, request);
    }

}
