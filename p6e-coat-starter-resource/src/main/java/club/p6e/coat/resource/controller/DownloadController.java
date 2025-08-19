package club.p6e.coat.resource.controller;

import club.p6e.coat.resource.context.DownloadContext;
import club.p6e.coat.resource.service.DownloadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Download Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/download")
public class DownloadController extends BaseController {

    /**
     * Download Service Object
     */
    private final DownloadService service;

    /**
     * Constructor Initializers
     *
     * @param service Download Service Object
     */
    public DownloadController(DownloadService service) {
        this.service = service;
    }

    @GetMapping
    public Mono<ServerResponse> def(ServerRequest request, DownloadContext.Request dcr) {
        return service.execute(dcr).flatMap(fr -> getDownloadServerResponse(request, fr));
    }

}
