package club.p6e.coat.resource.mapper;

import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.resource.context.DownloadContext;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 下载文件请求参数映射器
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = DownloadContextRequestParameterMapper.class,
        ignored = DownloadContextRequestParameterMapper.class
)
public class DownloadContextRequestParameterMapper extends RequestParameterMapper {

    /**
     * NODE 请求参数
     */
    private static final String NODE_PARAMETER_NAME = "node";

    /**
     * PATH 请求参数
     */
    private static final String PATH_PARAMETER_NAME = "path";

    @Override
    public Class<?> outputClass() {
        return DownloadContext.class;
    }

    @Override
    public Mono<Object> execute(ServerRequest request) {
        final ServerHttpRequest httpRequest = request.exchange().getRequest();
        final MultiValueMap<String, String> queryParams = httpRequest.getQueryParams();
        final DownloadContext context = new DownloadContext();
        context.putAll(queryParams);
        final List<String> nodes = queryParams.get(NODE_PARAMETER_NAME);
        if (nodes != null && !nodes.isEmpty() && nodes.get(0) != null) {
            context.setNode(nodes.get(0));
        } else {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(ServerRequest request). ==> URL PARAM <"
                            + NODE_PARAMETER_NAME + "> request parameter format error.",
                    "URL PARAM <" + NODE_PARAMETER_NAME + "> request parameter is null."
            ));
        }
        final List<String> paths = queryParams.get(PATH_PARAMETER_NAME);
        if (paths != null && !paths.isEmpty() && paths.get(0) != null) {
            final String pc = paths.get(0);
            final String name = FileUtil.name(pc);
            final String path = FileUtil.path(pc);
            if (name == null) {
                return Mono.error(new ParameterException(
                        this.getClass(),
                        "fun execute(ServerRequest request). ==> URL PARAM <"
                                + PATH_PARAMETER_NAME + "> request parameter format error.",
                        "URL PARAM <" + PATH_PARAMETER_NAME + "> request parameter format error."
                ));
            } else {
                context.setPath(FileUtil.composePath(path, name));
            }
        } else {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(ServerRequest request). ==> execute(...) request parameter is null.",
                    "execute(...) request parameter is null."
            ));
        }
        return Mono.just(context);
    }

}
