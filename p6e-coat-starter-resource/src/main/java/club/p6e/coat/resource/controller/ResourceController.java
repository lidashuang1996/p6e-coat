package club.p6e.coat.resource.controller;

import club.p6e.coat.resource.context.ResourceContext;
import club.p6e.coat.resource.mapper.RequestParameterMapper;
import club.p6e.coat.resource.service.ResourceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/resource")
public class ResourceController extends BaseController {

    /**
     * 资源查看服务对象
     */
    private final ResourceService service;

    /**
     * 构造函数初始化
     *
     * @param service 资源查看服务对象
     */
    public ResourceController(ResourceService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<ServerResponse> def(ServerRequest request) {
        return RequestParameterMapper
                .execute(request, ResourceContext.class)
                .flatMap(service::execute)
                .flatMap(fr -> getHttpRangeResourceServerResponse(request.headers().range(), fr));
    }

}
