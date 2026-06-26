package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.resource.*;
import club.p6e.coat.resource.context.ResourceContext;
import club.p6e.coat.resource.error.*;
import club.p6e.coat.resource.service.ResourceService;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(ResourceServiceImpl.class)
public class ResourceServiceImpl implements ResourceService {

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * File Auth Object
     */
    private final FileAuth fileAuth;

    /**
     * File Permission Object
     */
    private final FilePermission filePermission;

    /**
     * File Reader Builder Object
     */
    private final FileReaderBuilder fileReaderBuilder;

    /**
     * File Attribute Builder Object
     */
    private final FileAttributeBuilder fileAttributeBuilder;

    /**
     * Constructor Initializers
     *
     * @param properties           Properties Object
     * @param filePermission       File Permission Object
     * @param fileReaderBuilder    File Reader Builder Object
     * @param fileAttributeBuilder File Attribute Builder Object
     */
    public ResourceServiceImpl(
            Properties properties,
            FileAuth fileAuth,
            FilePermission filePermission,
            FileReaderBuilder fileReaderBuilder,
            FileAttributeBuilder fileAttributeBuilder
    ) {
        this.properties = properties;
        this.fileAuth = fileAuth;
        this.filePermission = filePermission;
        this.fileReaderBuilder = fileReaderBuilder;
        this.fileAttributeBuilder = fileAttributeBuilder;
    }

    @Override
    public Mono<FileReader<?>> execute(ResourceContext.Request request) {
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
            return fileAuth
                    .execute(voucher)
                    .flatMap(fu -> {
                        if (fu.getId() == 0) {
                            return Mono.error(new ResourceAuthException(
                                    this.getClass(),
                                    "fun execute(ResourceContext.Request request)",
                                    "request auth exception"
                            ));
                        } else {
                            return filePermission
                                    .execute(FilePermissionType.DOWNLOAD, fu)
                                    .flatMap(b -> {
                                        if (b) {
                                            attributes.putAll(resource.getOther());
                                            String fileName = FileUtil.getName(path);
                                            final String fileSuffix = FileUtil.getSuffix(path);
                                            if (fileName != null) {
                                                final int index = fileName.lastIndexOf("_");
                                                attributes.put("__rule__", index >= 0 ? fileName.substring(index + 1) : "original");
                                                fileName = fileName.substring(0, index);
                                            }
                                            final MediaType fileMediaType = resource.getSuffixes().get(fileSuffix);
                                            if (fileMediaType == null) {
                                                return Mono.error(new ResourceMediaTypeException(
                                                        this.getClass(),
                                                        "fun execute(ResourceContext.Request request)",
                                                        "request node mapper config media type not supported exception"
                                                ));
                                            } else {
                                                attributes.put("__media_type__", fileMediaType.getType());
                                                final String filePath = FileUtil.convertAbsolutePath(FileUtil.composePath(resource.getPath(), FileUtil.composeFile(fileName, fileSuffix)));
                                                if (filePath == null) {
                                                    return Mono.error(new ResourcePathException(
                                                            this.getClass(),
                                                            "fun execute(ResourceContext.Request request)",
                                                            "request node resource path exception"
                                                    ));
                                                }
                                                final File file = new File(filePath);
                                                final FileAttribute fileAttribute = fileAttributeBuilder.build(file, attributes);
                                                return Mono.just(fileReaderBuilder.build(file, fileAttribute, resource.getType(), attributes));
                                            }
                                        } else {
                                            return Mono.error(new ResourcePermissionException(
                                                    this.getClass(),
                                                    "fun execute(ResourceContext.Request request)",
                                                    "request node resource permission exception"
                                            ));
                                        }
                                    });
                        }
                    });
        }
    }

}
