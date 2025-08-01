package club.p6e.coat.resource.handler;

import club.p6e.coat.resource.aspect.ResourceAspect;
import club.p6e.coat.resource.context.ResourceContext;
import club.p6e.coat.resource.mapper.RequestParameterMapper;
import club.p6e.coat.resource.service.ResourceService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 资源查看-处理函数
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = ResourceHandlerFunction.class,
        ignored = ResourceHandlerFunction.class
)
public class ResourceHandlerFunction extends AspectHandlerFunction implements HandlerFunction<ServerResponse> {

    /**
     * 资源查看服务对象
     */
    private final ResourceService service;

    /**
     * 资源查看切面列表对象
     */
    private final List<ResourceAspect> aspects;

    /**
     * 构造函数初始化
     *
     * @param service 资源查看服务对象
     * @param aspects 资源查看切面列表对象
     */
    public ResourceHandlerFunction(ResourceService service, List<ResourceAspect> aspects) {
        this.service = service;
        this.aspects = aspects;
    }

    @NonNull
    @Override
    public Mono<ServerResponse> handle(@NonNull ServerRequest request) {
        return
                // 通过请求参数映射器获取上下文对象
                RequestParameterMapper.execute(request, ResourceContext.class)
                        // 执行资源查看之前的切点
                        .flatMap(c -> before(aspects, c))
                        // 执行资源查看
                        .flatMap(m -> service.execute(new ResourceContext(m)).flatMap(fra -> after(aspects, m, null).map(r -> fra)))
                        // 结果返回
                        .flatMap(fra -> {
                            final MediaType mediaType = fra.mediaType();
                            final long length = fra.model().getLength();
                            final List<HttpRange> ranges = request.headers().range();
                            if (!ranges.isEmpty()) {
                                final HttpRange range = ranges.get(0);
                                final long el = range.getRangeEnd(length);
                                final long sl = range.getRangeStart(length);
                                final long cl = el - sl + 1;
                                return ServerResponse
                                        .status(HttpStatus.PARTIAL_CONTENT)
                                        .contentLength(cl)
                                        .contentType(mediaType)
                                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                                        .header(HttpHeaders.CONTENT_RANGE, "bytes " + sl + "-" + el + "/" + length)
                                        .body((response, context) -> response.writeWith(fra.execute(sl, cl)));
                            } else {
                                return ServerResponse
                                        .ok()
                                        .contentType(mediaType)
                                        .body((response, context) -> response.writeWith(fra.execute()));
                            }
                        });
    }

}
