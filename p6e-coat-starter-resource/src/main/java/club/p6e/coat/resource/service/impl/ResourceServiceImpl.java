package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.resource.*;
import club.p6e.coat.resource.context.ResourceContext;
import club.p6e.coat.resource.error.ResourceMediaTypeException;
import club.p6e.coat.resource.error.ResourceNodeException;
import club.p6e.coat.resource.error.ResourcePathException;
import club.p6e.coat.resource.error.ResourcePermissionException;
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
     * File Attribute Builder Object
     */
    private final FileAttributeBuilder fileAttributeBuilder;

    /**
     * File Permission Service Object
     */
    private final FilePermissionService filePermissionService;

    /**
     * Constructor Initializers
     *
     * @param properties            Properties Object
     * @param fileReaderBuilder     File Reader Builder Object
     * @param fileAttributeBuilder  File Attribute Builder Object
     * @param filePermissionService File Permission Service Object
     */
    public ResourceServiceImpl(
            Properties properties,
            FileReaderBuilder fileReaderBuilder,
            FileAttributeBuilder fileAttributeBuilder,
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
                    "fun execute(ResourceContext.Request request)",
                    "request parameter <node> exception"
            ));
        }
        if (path == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(ResourceContext.Request request)",
                    "request parameter <path> exception"
            ));
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(ResourceContext.Request request)",
                    "request parameter <voucher> exception"
            ));
        }
        final Properties.Resource resource = properties.getResources().get(node);
        final Map<String, Object> attributes = new HashMap<>(request.getOther());
        if (resource == null) {
            return Mono.error(new ResourceNodeException(
                    this.getClass(),
                    "fun execute(ResourceContext.Request request)",
                    "request node mapper config does not exist exception"
            ));
        } else {
            attributes.put("fileRule", "source");
            attributes.putAll(resource.getOther());
            String fileName = FileUtil.getName(path);
            final String fileSuffix = FileUtil.getSuffix(path);
            if (fileName != null) {
                final int index = fileName.lastIndexOf("_");
                attributes.put("fileRule", index == -1 ? "source" : fileName.substring(index + 1));
                if (index >= 0) {
                    fileName = fileName.substring(0, index);
                }
            }
            final MediaType fileMediaType = resource.getSuffixes().get(fileSuffix);
            if (fileMediaType == null) {
                return Mono.error(new ResourceMediaTypeException(
                        this.getClass(),
                        "fun execute(ResourceContext.Request request)",
                        "request node mapper config media type not supported exception"
                ));
            } else {
                attributes.put("fileMediaType", fileMediaType.toString());
                final String filePath = FileUtil.convertAbsolutePath(FileUtil.composePath(resource.getPath(), FileUtil.composeFile(fileName, fileSuffix)));
                if (filePath == null) {
                    return Mono.error(new ResourcePathException(
                            this.getClass(),
                            "fun execute(ResourceContext.Request request)",
                            "request node resource path exception"
                    ));
                }
                final File file = new File(filePath);
                return filePermissionService
                        .execute(FilePermissionType.RESOURCE, voucher)
                        .flatMap(b -> {
                            if (b) {
                                return Mono.just(fileReaderBuilder.build(file,
                                        fileAttributeBuilder.build(file, attributes), resource.getType(), attributes));
                            } else {
                                return Mono.error(new ResourcePermissionException(
                                        this.getClass(),
                                        "fun execute(ResourceContext.Request request)",
                                        "request node resource permission exception"
                                ));
                            }
                        });
            }
        }
    }

}
