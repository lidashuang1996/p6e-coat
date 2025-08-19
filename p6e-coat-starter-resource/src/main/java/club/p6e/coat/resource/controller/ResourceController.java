package club.p6e.coat.resource.controller;

import club.p6e.coat.resource.context.ResourceContext;
import club.p6e.coat.resource.service.ResourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Resource Service Object
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/resource")
public class ResourceController extends BaseController {

    /**
     * Resource Service Object
     */
    private final ResourceService service;

    /**
     * Constructor Initializers
     *
     * @param service Resource Service Object
     */
    public ResourceController(ResourceService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<ServerResponse> def(ServerRequest request, ResourceContext.Request rcr) {
        return service.execute(rcr).flatMap(fr -> getResourceServerResponse(request, fr));
    }

}
