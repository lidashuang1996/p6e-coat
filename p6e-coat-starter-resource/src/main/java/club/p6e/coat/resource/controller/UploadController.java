package club.p6e.coat.resource.controller;

import club.p6e.coat.common.utils.CopyUtil;
import club.p6e.coat.resource.context.SimpleUploadContext;
import club.p6e.coat.resource.context.SliceUploadContext;
import club.p6e.coat.resource.service.SimpleUploadService;
import club.p6e.coat.resource.service.SliceUploadService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.MultiValueMap;
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
     * Slice Upload Service Object
     */
    private final SliceUploadService sliceUploadService;

    /**
     * Simple Upload Service Object
     */
    private final SimpleUploadService simpleUploadService;

    /**
     * Constructor Initializers
     *
     * @param sliceUploadService  Slice Upload Service Object
     * @param simpleUploadService Simple Upload Service Object
     */
    public UploadController(SliceUploadService sliceUploadService, SimpleUploadService simpleUploadService) {
        this.sliceUploadService = sliceUploadService;
        this.simpleUploadService = simpleUploadService;
    }

    @PostMapping("/simple")
    public Mono<ServerResponse> simple(ServerRequest request, @RequestPart("file") FilePart filePart, @RequestBody SimpleUploadContext.Request scr) {
        final MultiValueMap<String, String> params = request.queryParams();
        params.forEach((key, value) -> {
            if ("node".equalsIgnoreCase(key)) {
                scr.setNode(value.get(0));
            }
            if ("voucher".equalsIgnoreCase(key)) {
                scr.setVoucher(value.get(0));
            }
            scr.getOther().put(key, String.join(",", value));
        });
        return simpleUploadService.execute(scr.setFile(filePart)).flatMap(r ->
                ServerResponse.ok().bodyValue(ResultContext.build(CopyUtil.run(r, SimpleUploadContext.Vo.class))));
    }

    @PostMapping("/slice/open")
    public Mono<ServerResponse> openSlice(ServerRequest request, @RequestBody SliceUploadContext.Open.Request sor) {
        final MultiValueMap<String, String> params = request.queryParams();
        params.forEach((key, value) -> {
            if ("name".equalsIgnoreCase(key)) {
                sor.setName(value.get(0));
            }
            if ("voucher".equalsIgnoreCase(key)) {
                sor.setVoucher(value.get(0));
            }
            sor.getOther().put(key, String.join(",", value));
        });
        return sliceUploadService.open(sor).flatMap(r ->
                ServerResponse.ok().bodyValue(ResultContext.build(CopyUtil.run(r, SliceUploadContext.Open.Vo.class))));
    }

    @RequestMapping(value = "/slice/close", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE})
    public Mono<ServerResponse> closeSlice(ServerRequest request, SliceUploadContext.Close.Request scr) {
        final MultiValueMap<String, String> params = request.queryParams();
        final SliceUploadContext.Close.Request fcr = scr == null ? new SliceUploadContext.Close.Request() : scr;
        params.forEach((key, value) -> {
            if ("id".equalsIgnoreCase(key)) {
                try {
                    fcr.setId(Integer.valueOf(value.get(0)));
                } catch (Exception e) {
                    // ignore exception
                }
            }
            if ("node".equalsIgnoreCase(key)) {
                fcr.setNode(value.get(0));
            }
            if ("voucher".equalsIgnoreCase(key)) {
                fcr.setVoucher(value.get(0));
            }
            fcr.getOther().put(key, String.join(",", value));
        });
        return sliceUploadService.close(fcr).flatMap(r ->
                ServerResponse.ok().bodyValue(ResultContext.build(CopyUtil.run(r, SliceUploadContext.Close.Vo.class))));
    }

    @RequestMapping(value = "/slice/close/{id}", method = {RequestMethod.POST, RequestMethod.GET, RequestMethod.DELETE})
    public Mono<ServerResponse> closeSlice(@PathVariable Integer id, ServerRequest request, SliceUploadContext.Close.Request scr) {
        return closeSlice(request, (scr == null ? new SliceUploadContext.Close.Request() : scr).setId(id));
    }

    @PostMapping("/slice/chunk")
    public Mono<ServerResponse> chunkSlice(ServerRequest request, @RequestBody SliceUploadContext.Chunk.Request scr) {
        final MultiValueMap<String, String> params = request.queryParams();
        final SliceUploadContext.Chunk.Request fcr = scr == null ? new SliceUploadContext.Chunk.Request() : scr;
        params.forEach((key, value) -> {
            if ("node".equalsIgnoreCase(key)) {
                fcr.setNode(value.get(0));
            }
            if ("voucher".equalsIgnoreCase(key)) {
                fcr.setVoucher(value.get(0));
            }
            if ("id".equalsIgnoreCase(key)) {
                try {
                    fcr.setId(Integer.valueOf(value.get(0)));
                } catch (Exception e) {
                    // ignore exception
                }
            }
            fcr.getOther().put(key, String.join(",", value));
        });
        return sliceUploadService.chunk(fcr).flatMap(r ->
                ServerResponse.ok().bodyValue(ResultContext.build(CopyUtil.run(r, SliceUploadContext.Chunk.Vo.class))));
    }

    @PostMapping("/slice/chunk/{id}")
    public Mono<ServerResponse> chunkSlice(@PathVariable Integer id, ServerRequest request, @RequestBody SliceUploadContext.Chunk.Request scr) {
        return chunkSlice(request, (scr == null ? new SliceUploadContext.Chunk.Request() : scr).setId(id));
    }

}
