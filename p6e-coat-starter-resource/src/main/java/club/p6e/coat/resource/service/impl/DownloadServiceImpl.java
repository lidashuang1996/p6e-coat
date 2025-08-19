package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.resource.*;
import club.p6e.coat.resource.context.DownloadContext;
import club.p6e.coat.resource.error.NodeException;
import club.p6e.coat.resource.error.NodePermissionException;
import club.p6e.coat.resource.service.DownloadService;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Download Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(DownloadService.class)
public class DownloadServiceImpl implements DownloadService {

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
    public DownloadServiceImpl(
            Properties properties,
            FileReaderBuilder fileReaderBuilder,
            FileAttributeBuilder fileAttributeBuilder,
            FilePermissionService filePermissionService
    ) {
        this.properties = properties;
        this.fileReaderBuilder = fileReaderBuilder;
        this.fileAttributeBuilder = fileAttributeBuilder;
        this.filePermissionService = filePermissionService;
    }

    @Override
    public Mono<FileReader> execute(DownloadContext.Request request) {
        final String node = request.getNode();
        final String path = request.getPath();
        final String voucher = request.getVoucher();
        if (node == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(DownloadContext.Request request) => request parameter <node> exception",
                    "request parameter <node> exception"
            ));
        }
        if (path == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(DownloadContext.Request request) => request parameter <path> exception",
                    "request parameter <path> exception"
            ));
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(DownloadContext.Request request) => request parameter <voucher> exception",
                    "request parameter <voucher> exception"
            ));
        }
        final Properties.Download dc = properties.getDownloads().get(node);
        final Map<String, Object> attributes = new HashMap<>(request.getOther());
        if (dc == null) {
            return Mono.error(new NodeException(
                    this.getClass(),
                    "fun execute(DownloadContext.Request request) => request node mapper config does not exist exception",
                    "request node mapper config does not exist exception"
            ));
        } else {
            attributes.putAll(dc.getOther());
            final File file = new File(FileUtil.convertAbsolutePath(FileUtil.composePath(dc.getPath(), path)));
            return filePermissionService
                    .execute(FilePermissionType.DOWNLOAD, voucher)
                    .flatMap(b -> {
                        if (b) {
                            return Mono.just(fileReaderBuilder.build(dc.getType(), attributes,file, fileAttributeBuilder.build(file)));
                        } else {
                            return Mono.error(new NodePermissionException(
                                    this.getClass(),
                                    "fun execute(DownloadContext.Request request) => request node file operation permission exception",
                                    "request node file operation permission exception"
                            ));
                        }
                    });
        }
    }
}
