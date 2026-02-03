package club.p6e.coat.auth.oauth2.controller;

import club.p6e.coat.auth.oauth2.context.InfoContext;
import club.p6e.coat.auth.oauth2.service.ReactiveInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

/**
 * Reactive Info Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ReactiveInfoController.class)
@RestController("club.p6e.coat.auth.oauth2.controller.ReactiveInfoController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveInfoController {

    /**
     * Reactive Info Service Object
     */
    private final ReactiveInfoService service;

    /**
     * Constructor Initialization
     *
     * @param service Reactive Info Service Object
     */
    public ReactiveInfoController(ReactiveInfoService service) {
        this.service = service;
    }

    @RequestMapping(value = "/oauth2/user/info", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getUserInfo(ServerWebExchange exchange, InfoContext.Request request) {
        return service.getUserInfo(exchange, request);
    }

    @RequestMapping(value = "/oauth2/client/info", method = {RequestMethod.GET, RequestMethod.POST})
    public Object getClientInfo(ServerWebExchange exchange, InfoContext.Request request) {
        return service.getClientInfo(exchange, request);
    }

}
