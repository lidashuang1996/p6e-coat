package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.utils.CopyUtil;
import club.p6e.coat.resource.*;
import club.p6e.coat.resource.context.SimpleUploadContext;
import club.p6e.coat.resource.error.NodeException;
import club.p6e.coat.resource.error.NodePermissionException;
import club.p6e.coat.resource.model.UploadLogModel;
import club.p6e.coat.resource.repository.UploadRepository;
import club.p6e.coat.resource.service.SimpleUploadService;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Upload Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(SimpleUploadService.class)
public class SimpleUploadServiceImpl implements SimpleUploadService {

    /**
     * Source
     */
    private static final String SOURCE = "SIMPLE_UPLOAD";

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Upload Repository Object
     */
    private final UploadRepository repository;

    /**
     * File Writer Builder Object
     */
    private final FileWriterBuilder fileWriterBuilder;

    /**
     * File Permission Service Object
     */
    private final FilePermissionService filePermissionService;

    /**
     * Folder Storage Location Path Service Object
     */
    private final FolderStorageLocationPathService folderStorageLocationPathService;

    /**
     * Constructor Initializers
     *
     * @param properties                       Properties Object
     * @param repository                       Upload Repository Object
     * @param fileWriterBuilder                File Writer Builder Object
     * @param filePermissionService            File Permission Service Object
     * @param folderStorageLocationPathService Folder Storage Location Path Service Object
     */
    public SimpleUploadServiceImpl(
            Properties properties,
            UploadRepository repository,
            FileWriterBuilder fileWriterBuilder,
            FilePermissionService filePermissionService,
            FolderStorageLocationPathService folderStorageLocationPathService
    ) {
        this.properties = properties;
        this.repository = repository;
        this.fileWriterBuilder = fileWriterBuilder;
        this.filePermissionService = filePermissionService;
        this.folderStorageLocationPathService = folderStorageLocationPathService;
    }

    @Override
    public Mono<SimpleUploadContext.Dto> execute(SimpleUploadContext.Request request) {
        final String node = request.getNode();
        final String voucher = request.getVoucher();
        final MultipartFile multipartFile = request.getFile();
        if (node == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request) => request parameter <node> exception",
                    "request parameter <node> exception"
            ));
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request) => request parameter <voucher> exception",
                    "request parameter <voucher> exception"
            ));
        }
        if (multipartFile == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request) => request parameter <file> exception",
                    "request parameter <file> exception"
            ));
        }
        if (multipartFile.getOriginalFilename() == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request) => request parameter <file/name> exception",
                    "request parameter <file/name> exception"
            ));
        }
        final Properties.Upload uc = properties.getUploads().get(node);
        final Map<String, Object> attributes = new HashMap<>(request.getOther());
        if (uc == null) {
            return Mono.error(new NodeException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request) => request node mapper config does not exist exception",
                    "request node mapper config does not exist exception"
            ));
        } else {
            request.setFile(null);
            attributes.putAll(uc.getOther());
            final long fileLength = multipartFile.getSize();
            final String fileName = FileUtil.name(multipartFile.getOriginalFilename());
            final String fileSuffix = FileUtil.getSuffix(fileName);
            final String fileContent = FileUtil.composeFile(fileName, fileSuffix);
            if (fileLength > uc.getMax()) {
                return Mono.error(new ParameterException(
                        this.getClass(),
                        "fun execute(SimpleUploadContext.Request request) => request parameter <file/size> exception",
                        "request parameter <file/size> exception"
                ));
            } else {
                final String fileRelativePath = FileUtil.composePath(folderStorageLocationPathService.execute(), fileContent);
                final String fileAbsolutePath = FileUtil.composePath(uc.getPath(), fileRelativePath);
                if (fileRelativePath == null || fileAbsolutePath == null) {
                    return Mono.error(new ParameterException(
                            this.getClass(),
                            "fun execute(SimpleUploadContext.Request request) => request parameter <file/path> exception",
                            "request parameter <file/path> exception"
                    ));
                } else {
                    final UploadLogModel model = new UploadLogModel();
                    model.setName(fileName);
                    model.setSource(SOURCE);
                    final String target = FileUtil.convertAbsolutePath(fileAbsolutePath);
                    final FileWriter fileWriter = fileWriterBuilder.execute(uc.getType(), attributes, multipartFile, target);
                    return filePermissionService
                            .execute(FilePermissionType.UPLOAD, voucher)
                            .flatMap(b -> {
                                if (b) {
                                    return repository.create(model)
                                            .flatMap(m -> fileWriter.execute().then(Mono.just(m)))
                                            .flatMap(m -> repository.closeLock(m.getId()))
                                            .map(m -> CopyUtil.run(m, SimpleUploadContext.Dto.class));
                                } else {
                                    return Mono.error(new NodePermissionException(
                                            this.getClass(),
                                            "fun execute(SimpleUploadContext.Request request) => request node file operation permission exception",
                                            "request node file operation permission exception")
                                    );
                                }
                            });
                }
            }
        }
    }

}
