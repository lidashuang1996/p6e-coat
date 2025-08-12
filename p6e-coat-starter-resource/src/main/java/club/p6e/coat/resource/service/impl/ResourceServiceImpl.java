package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.resource.*;
import club.p6e.coat.resource.context.ResourceContext;
import club.p6e.coat.resource.error.NodeException;
import club.p6e.coat.resource.error.NodePermissionException;
import club.p6e.coat.resource.service.ResourceService;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源查看服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = ResourceService.class,
        ignored = ResourceServiceImpl.class
)
public class ResourceServiceImpl implements ResourceService {

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * File Reader Builder Object
     */
    private final FileReaderBuilder fileReaderBuilder;

    /**
     * File Permission Service Object
     */
    private final FilePermissionService filePermissionService;

    /**
     * Constructor Initializers
     *
     * @param properties            Properties Object
     * @param fileReaderBuilder     File Reader Builder Object
     * @param filePermissionService File Permission Service Object
     */
    public ResourceServiceImpl(
            Properties properties,
            FileReaderBuilder fileReaderBuilder,
            FilePermissionService filePermissionService
    ) {
        this.properties = properties;
        this.fileReaderBuilder = fileReaderBuilder;
        this.filePermissionService = filePermissionService;
    }

    @Override
    public Mono<FileReader> execute(ResourceContext.Request request) {
        final String node = request.getNode();
        final String path = request.getPath();
        final String voucher = request.getVoucher();
        if (node == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(ResourceContext.Request request) => request parameter <node> exception",
                    "request parameter <node> exception"
            ));
        }
        if (path == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(ResourceContext.Request request) => request parameter <path> exception",
                    "request parameter <path> exception"
            ));
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(ResourceContext.Request request) => request parameter <voucher> exception",
                    "request parameter <voucher> exception"
            ));
        }
        final Properties.Resource rc = properties.getResources().get(node);
        final Map<String, Object> attributes = new HashMap<>(request.getOther());
        if (rc == null) {
            return Mono.error(new NodeException(
                    this.getClass(),
                    "fun execute(ResourceContext.Request request) => request node mapper config does not exist exception",
                    "request node mapper config does not exist exception"
            ));
        } else {
            attributes.putAll(rc.getOther());
            final String suffix = FileUtil.getSuffix(path);
            final MediaType mt = rc.getSuffixes().get(suffix);
            final File file = new File(FileUtil.convertAbsolutePath(FileUtil.composePath(rc.getPath(), path)));
            if (mt == null) {
                return Mono.error(new NodeException(
                        this.getClass(),
                        "fun execute(ResourceContext context) => request node mapper config media type does not exist exception",
                        "request node mapper config media type does not exist exception"
                ));
            } else {
                return filePermissionService
                        .execute(FilePermissionType.RESOURCE, voucher)
                        .flatMap(b -> {
                            if (b) {
                                return Mono.just(fileReaderBuilder.of(file).fileMediaType(mt).attributes(attributes).build());
                            } else {
                                return Mono.error(new NodePermissionException(
                                        this.getClass(),
                                        "fun execute(ResourceContext.Request request) => request node file operation permission exception",
                                        "request node file operation permission exception")
                                );
                            }
                        });
            }
        }
    }

}
