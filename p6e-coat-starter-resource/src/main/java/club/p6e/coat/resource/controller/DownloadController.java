package club.p6e.coat.resource.controller;

import club.p6e.coat.resource.aspect.DownloadAspect;
import club.p6e.coat.resource.context.DownloadContext;
import club.p6e.coat.resource.context.ResourceContext;
import club.p6e.coat.resource.mapper.RequestParameterMapper;
import club.p6e.coat.resource.service.DownloadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/download")
public class DownloadController extends BaseController {

    /**
     * 下载文件服务对象
     */
    private final DownloadService service;

    /**
     * 下载文件切面列表对象
     */
    private final List<DownloadAspect> aspects;

    /**
     * 构造函数初始化
     *
     * @param service 下载文件服务对象
     * @param aspects 下载文件切面列表对象
     */
    public DownloadController(DownloadService service, List<DownloadAspect> aspects) {
        this.service = service;
        this.aspects = aspects;
    }

    @GetMapping
    public Mono<ServerResponse> def(ServerRequest request) {
        return RequestParameterMapper
                .execute(request, DownloadContext.class)
                .flatMap(service::execute)
                .flatMap(fr -> getHttpRangeDownloadServerResponse(request.headers().range(), fr));
    }

}
