package club.p6e.coat.resource.controller;

import club.p6e.coat.resource.context.ResourceContext;
import club.p6e.coat.resource.context.SliceUploadContext;
import club.p6e.coat.resource.mapper.RequestParameterMapper;
import club.p6e.coat.resource.service.SimpleUploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/upload")
public class UploadController extends BaseController {

    /**
     * Simple Upload Service Object
     */
    private final SimpleUploadService simpleUploadService;

    /**
     * Constructor Initializers
     *
     * @param simpleUploadService Simple Upload Service Object
     */
    public UploadController(SimpleUploadService simpleUploadService) {
        this.simpleUploadService = simpleUploadService;
    }

    @PostMapping("/simple")
    public Mono<ServerResponse> simple(ServerRequest request) {
        return RequestParameterMapper
                .execute(request, ResourceContext.class)
                .flatMap(simpleUploadService::execute)
                .flatMap(r -> ServerResponse.ok().bodyValue(ResultContext.build(r)));
    }

    @PostMapping("/slice/open")
    public Mono<ServerResponse> openSlice(ServerRequest request) {
        return RequestParameterMapper
                .execute(request, ResourceContext.class)
                .flatMap(simpleUploadService::execute)
                .flatMap(r -> ServerResponse.ok().bodyValue(ResultContext.build(r)));
    }

    @RequestMapping(value = "/slice/close", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE})
    public Mono<ServerResponse> closeSlice(ServerRequest request) {
        return RequestParameterMapper
                .execute(request, ResourceContext.class)
                .flatMap(simpleUploadService::execute)
                .flatMap(r -> ServerResponse.ok().bodyValue(ResultContext.build(r)));
    }

    @RequestMapping(value = "/slice/close/{id}", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE})
    public Mono<ServerResponse> closeSlice(@PathVariable String id, ServerRequest request) {
        return RequestParameterMapper
                .execute(request, ResourceContext.class)
                .flatMap(simpleUploadService::execute)
                .flatMap(r -> ServerResponse.ok().bodyValue(ResultContext.build(r)));
    }

    @PostMapping("/slice")
    public Mono<ServerResponse> slice(ServerRequest request) {
        return RequestParameterMapper
                .execute(request, ResourceContext.class)
                .flatMap(simpleUploadService::execute)
                .flatMap(r -> ServerResponse.ok().bodyValue(ResultContext.build(r)));
    }

    @PostMapping("/slice/{id}")
    public Mono<ServerResponse> slice(@PathVariable Integer id, ServerRequest request) {
        return RequestParameterMapper
                .execute(request, SliceUploadContext.class)
                .map(c -> c.setId(id))
                .flatMap(simpleUploadService::execute)
                .flatMap(r -> ServerResponse.ok().bodyValue(ResultContext.build(r)));
    }

}
