package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.common.utils.CopyUtil;
import club.p6e.coat.resource.*;
import club.p6e.coat.resource.context.SimpleUploadContext;
import club.p6e.coat.resource.error.*;
import club.p6e.coat.resource.model.FileUploadModel;
import club.p6e.coat.resource.repository.FileUploadRepository;
import club.p6e.coat.resource.service.SimpleUploadService;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.codec.multipart.FilePart;
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
@ConditionalOnMissingBean(SimpleUploadServiceImpl.class)
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
    private final FileUploadRepository repository;

    /**
     * File Auth Object
     */
    private final FileAuth fileAuth;

    /**
     * File Permission Object
     */
    private final FilePermission filePermission;

    /**
     * File Writer Builder Object
     */
    private final FileWriterBuilder fileWriterBuilder;

    /**
     * Folder Storage Location Path Object
     */
    private final FolderStorageLocationPath folderStorageLocationPath;

    /**
     * Constructor Initializers
     *
     * @param properties                Properties Object
     * @param repository                Upload Repository Object
     * @param filePermission            File Permission Object
     * @param fileWriterBuilder         File Writer Builder Object
     * @param folderStorageLocationPath Folder Storage Location Path Object
     */
    public SimpleUploadServiceImpl(
            Properties properties,
            FileUploadRepository repository,
            FileAuth fileAuth,
            FilePermission filePermission,
            FileWriterBuilder fileWriterBuilder,
            FolderStorageLocationPath folderStorageLocationPath
    ) {
        this.properties = properties;
        this.repository = repository;
        this.fileAuth = fileAuth;
        this.filePermission = filePermission;
        this.fileWriterBuilder = fileWriterBuilder;
        this.folderStorageLocationPath = folderStorageLocationPath;
    }

    @Override
    public Mono<SimpleUploadContext.Dto> execute(SimpleUploadContext.Request request) {
        final String node = request.getNode();
        final String voucher = request.getVoucher();
        final FilePart filePart = request.getFile();
        if (node == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request)",
                    "request parameter <node> exception"
            ));
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request)",
                    "request parameter <voucher> exception"
            ));
        }
        if (filePart == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request)",
                    "request parameter <file> exception"
            ));
        }
        final Properties.Upload upload = properties.getUploads().get(node);
        final Map<String, Object> attributes = new HashMap<>(request.getOther());
        if (upload == null) {
            return Mono.error(new ResourceNodeException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext.Request request)",
                    "request node mapper config does not exist exception"
            ));
        } else {
            request.setFile(null);
            attributes.putAll(upload.getOther());
            final String fileName = FileUtil.getName(filePart.filename());
            final String fileSuffix = FileUtil.getSuffix(filePart.filename());
            final String fileNameSuffix = FileUtil.composeFile(fileName, fileSuffix);
            final String fileRelativePath = FileUtil.composePath(folderStorageLocationPath.execute(), fileNameSuffix);
            final String fileAbsolutePath = FileUtil.composePath(upload.getPath(), fileRelativePath);
            final String outputPath = FileUtil.convertAbsolutePath(fileAbsolutePath);
            if (outputPath == null) {
                return Mono.error(new ResourcePathException(
                        this.getClass(),
                        "fun execute(SimpleUploadContext.Request request)",
                        "request resource target path exception"
                ));
            } else {
                return fileAuth
                        .execute(voucher)
                        .flatMap(fu -> {
                            if (fu.getId() == 0) {
                                return Mono.error(new ResourceAuthException(
                                        this.getClass(),
                                        "fun execute(SimpleUploadContext.Request request)",
                                        "request auth exception"
                                ));
                            } else {
                                return filePermission
                                        .execute(FilePermissionType.DOWNLOAD, fu)
                                        .flatMap(b -> {
                                            if (b) {
                                                attributes.put("__name__", fileName);
                                                attributes.put("__suffix__", fileSuffix);
                                                attributes.put("__name_suffix__", fileNameSuffix);
                                                final File outputFile = new File(outputPath);
                                                if (!FileUtil.checkFolderExist(outputFile.getParent())) {
                                                    FileUtil.createFolder(outputFile.getParent());
                                                }
                                                final FileUploadModel model = new FileUploadModel();
                                                model.setSize(0L);
                                                model.setSource(SOURCE);
                                                model.setName(fileName);
                                                model.setStorageLocation(fileRelativePath);
                                                model.setStorageType(upload.getType().name());
                                                model.setOwner(String.valueOf(fu.getId()));
                                                final FileWriter fileWriter = fileWriterBuilder.build(outputFile, upload.getType(), attributes);
                                                return repository.create(model)
                                                        .flatMap(m -> filePart.transferTo(outputFile).then(Mono.just(m)))
                                                        .flatMap(m -> fileWriter.execute().map(_ -> m))
                                                        .flatMap(m -> repository.closeLock(m.getId(), String.valueOf(fu.getId())).map(_ -> m))
                                                        .flatMap(m -> {
                                                            if (outputFile.length() > upload.getMax()) {
                                                                FileUtil.deleteFile(outputFile);
                                                                return Mono.error(new ResourceSizeException(
                                                                        this.getClass(),
                                                                        "fun execute(SimpleUploadContext.Request request)",
                                                                        "request file size exceeds the maximum limit " +
                                                                                "(" + outputFile.length() + "/" + upload.getMax() + ") exception"
                                                                ));
                                                            } else {
                                                                return repository.update(new FileUploadModel().setId(m.getId()).setSize(outputFile.length()));
                                                            }
                                                        })
                                                        .map(m -> CopyUtil.run(m, SimpleUploadContext.Dto.class));

                                            } else {
                                                return Mono.error(new ResourcePermissionException(
                                                        this.getClass(),
                                                        "fun execute(SimpleUploadContext.Request request)",
                                                        "request node resource permission exception"
                                                ));
                                            }
                                        });
                            }
                        });
            }
        }
    }

}
