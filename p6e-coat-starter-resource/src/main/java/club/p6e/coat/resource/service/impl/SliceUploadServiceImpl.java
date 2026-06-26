package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.common.utils.CopyUtil;
import club.p6e.coat.resource.*;
import club.p6e.coat.resource.context.SliceUploadContext;
import club.p6e.coat.resource.error.*;
import club.p6e.coat.resource.model.FileUploadChunkModel;
import club.p6e.coat.resource.model.FileUploadModel;
import club.p6e.coat.resource.repository.FileUploadChunkRepository;
import club.p6e.coat.resource.repository.FileUploadRepository;
import club.p6e.coat.resource.service.SliceUploadService;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Slice Upload Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(SliceUploadServiceImpl.class)
public class SliceUploadServiceImpl implements SliceUploadService {

    /**
     * Source
     */
    private static final String SOURCE = "SLICE_UPLOAD";

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Upload Repository Object
     */
    private final FileUploadRepository uploadRepository;

    /**
     * Upload Chunk Repository Object
     */
    private final FileUploadChunkRepository uploadChunkRepository;

    /**
     * File Auth Object
     */
    private final FileAuth fileAuth;

    /**
     * File Signature Object
     */
    private final FileSignature fileSignature;

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
     * @param uploadRepository          Upload Repository Object
     * @param uploadChunkRepository     File Permission Object
     * @param fileAuth                  File Auth Object
     * @param fileSignature             File Signature Object
     * @param filePermission            File Permission Object
     * @param fileWriterBuilder         File Writer Builder Object
     * @param folderStorageLocationPath Folder Storage Location Path Object
     */
    public SliceUploadServiceImpl(
            Properties properties,
            FileUploadRepository uploadRepository,
            FileUploadChunkRepository uploadChunkRepository,
            FileAuth fileAuth,
            FileSignature fileSignature,
            FilePermission filePermission,
            FileWriterBuilder fileWriterBuilder,
            FolderStorageLocationPath folderStorageLocationPath
    ) {
        this.properties = properties;
        this.uploadRepository = uploadRepository;
        this.uploadChunkRepository = uploadChunkRepository;
        this.fileAuth = fileAuth;
        this.fileSignature = fileSignature;
        this.filePermission = filePermission;
        this.fileWriterBuilder = fileWriterBuilder;
        this.folderStorageLocationPath = folderStorageLocationPath;
    }

    @Override
    public Mono<SliceUploadContext.Open.Dto> open(SliceUploadContext.Open.Request request) {
        final String name = request.getName();
        final String node = request.getNode();
        final String voucher = request.getVoucher();
        if (name == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun open(SliceUploadContext.Open.Request request)",
                    "request parameter <name> exception"
            ));
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun open(SliceUploadContext.Open.Request request)",
                    "request parameter <voucher> exception"
            ));
        }
        final Properties.Upload upload = properties.getUploads().get(node);
        if (upload == null) {
            return Mono.error(new ResourceNodeException(
                    this.getClass(),
                    "fun open(SliceUploadContext.Open.Request request)",
                    "request node mapper config does not exist exception"
            ));
        } else {
            return fileAuth
                    .execute(voucher)
                    .flatMap(fu -> {
                        if (fu.getId() == 0) {
                            return Mono.error(new ResourceAuthException(
                                    this.getClass(),
                                    "fun open(SliceUploadContext.Open.Request request)",
                                    "request auth exception"
                            ));
                        } else {
                            return filePermission
                                    .execute(FilePermissionType.UPLOAD, fu)
                                    .flatMap(b -> {
                                        if (b) {
                                            final FileUploadModel model = new FileUploadModel();
                                            model.setName(name);
                                            model.setSource(SOURCE);
                                            model.setOwner(String.valueOf(fu.getId()));
                                            model.setCreator(String.valueOf(fu.getId()));
                                            model.setModifier(String.valueOf(fu.getId()));
                                            return uploadRepository.create(model).map(m -> {
                                                FileUtil.createFolder(FileUtil.convertAbsolutePath(
                                                        FileUtil.composePath(upload.getSlice().getPath(), String.valueOf(m.getId()))));
                                                return CopyUtil.run(m, SliceUploadContext.Open.Dto.class);
                                            });
                                        } else {
                                            return Mono.error(new ResourcePermissionException(
                                                    this.getClass(),
                                                    "fun open(SliceUploadContext.Open.Request request)",
                                                    "request node upload permission exception"
                                            ));
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    public Mono<SliceUploadContext.Chunk.Dto> chunk(SliceUploadContext.Chunk.Request request) {
        final Integer id = request.getId();
        final String node = request.getNode();
        final Integer index = request.getIndex();
        final String voucher = request.getVoucher();
        final String signature = request.getSignature();
        final FilePart filePart = request.getFilePart();
        if (id == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun chunk(SliceUploadContext.Chunk.Request request)",
                    "request parameter <id> exception"
            ));
        }
        if (node == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun chunk(SliceUploadContext.Chunk.Request request)",
                    "request parameter <node> exception"
            ));
        }
        if (index == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun chunk(SliceUploadContext.Chunk.Request request)",
                    "request parameter <index> exception"
            ));
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun chunk(SliceUploadContext.Chunk.Request request)",
                    "request parameter <voucher> exception"
            ));
        }
        if (signature == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun chunk(SliceUploadContext.Chunk.Request request)",
                    "request parameter <signature> exception"
            ));
        }
        final Properties.Upload upload = properties.getUploads().get(node);
        if (upload == null) {
            return Mono.error(new ResourceNodeException(
                    this.getClass(),
                    "fun chunk(SliceUploadContext.Chunk.Request request)",
                    "request node mapper config does not exist exception"
            ));
        } else {
            request.setFilePart(null);
            return fileAuth
                    .execute(voucher)
                    .flatMap(fu -> {
                        if (fu.getId() == 0) {
                            return Mono.error(new ResourceAuthException(
                                    this.getClass(),
                                    "fun chunk(SliceUploadContext.Open.Request request)",
                                    "request auth exception"
                            ));
                        } else {
                            return filePermission
                                    .execute(FilePermissionType.UPLOAD, fu)
                                    .flatMap(b -> {
                                        if (b) {
                                            return chunk(id, index, signature, filePart, fu, upload).map(m -> CopyUtil.run(m, SliceUploadContext.Chunk.Dto.class));
                                        } else {
                                            return Mono.error(new ResourcePermissionException(
                                                    this.getClass(),
                                                    "fun chunk(SliceUploadContext.Chunk.Request request)",
                                                    "request node upload permission exception"
                                            ));
                                        }
                                    });
                        }
                    });

        }
    }

    @Override
    public Mono<SliceUploadContext.Close.Dto> close(SliceUploadContext.Close.Request request) {
        final Integer id = request.getId();
        final String node = request.getNode();
        final String voucher = request.getVoucher();
        if (id == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun close(SliceUploadContext.Close.Request request)",
                    "request parameter <id> exception")
            );
        }
        if (node == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun close(SliceUploadContext.Close.Request request)",
                    "request parameter <node> exception")
            );
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun close(SliceUploadContext.Close.Request request)",
                    "request parameter <voucher> exception")
            );
        }
        final Properties.Upload upload = properties.getUploads().get(node);
        final Map<String, Object> attributes = new HashMap<>(request.getOther());
        if (upload == null) {
            return Mono.error(new ResourceNodeException(
                    this.getClass(),
                    "fun close(SliceUploadContext.Close.Request request)",
                    "request node mapper config does not exist exception"
            ));
        } else {
            attributes.putAll(upload.getOther());
            return fileAuth
                    .execute(voucher)
                    .flatMap(fu -> {
                        if (fu.getId() == 0) {
                            return Mono.error(new ResourceAuthException(
                                    this.getClass(),
                                    "fun close(SliceUploadContext.Close.Request request)",
                                    "request auth exception"
                            ));
                        } else {
                            return filePermission
                                    .execute(FilePermissionType.UPLOAD, fu)
                                    .flatMap(b -> {
                                        if (b) {
                                            attributes.putAll(upload.getOther());
                                            return uploadRepository
                                                    .closeLock(id, String.valueOf(fu.getId()))
                                                    .flatMap(_ -> uploadRepository.select(id))
                                                    .flatMap(m -> {
                                                        final String fileName = FileUtil.getName(m.getName());
                                                        final String fileSuffix = FileUtil.getSuffix(m.getName());
                                                        final String fileNameSuffix = FileUtil.composeFile(fileName, fileSuffix);
                                                        final String fileRelativePath = FileUtil.composePath(folderStorageLocationPath.execute(), fileNameSuffix);
                                                        final String fileAbsolutePath = FileUtil.composePath(upload.getPath(), fileRelativePath);
                                                        final String sliceAbsolutePath = FileUtil.convertAbsolutePath(
                                                                FileUtil.composePath(upload.getSlice().getPath(), String.valueOf(m.getId())));
                                                        final File[] files = FileUtil.readFolder(sliceAbsolutePath);
                                                        for (int i = 0; i < files.length; i++) {
                                                            for (int j = i; j < files.length; j++) {
                                                                final String in = files[i].getName();
                                                                final String jn = files[j].getName();
                                                                final int iw = Integer.parseInt(in.substring(0, in.indexOf("_")));
                                                                final int jw = Integer.parseInt(jn.substring(0, jn.indexOf("_")));
                                                                if (iw > jw) {
                                                                    final File v = files[j];
                                                                    files[j] = files[i];
                                                                    files[i] = v;
                                                                }
                                                            }
                                                        }
                                                        final String outputPath = FileUtil.convertAbsolutePath(fileAbsolutePath);
                                                        if (outputPath == null) {
                                                            return Mono.error(new ResourcePathException(
                                                                    this.getClass(),
                                                                    "fun execute(SimpleUploadContext.Request request)",
                                                                    "request resource target path exception"
                                                            ));
                                                        } else {
                                                            final File outputFile = new File(outputPath);
                                                            final FileWriter fileWriter = fileWriterBuilder.build(outputFile, upload.getType(), attributes);
                                                            return Mono.fromRunnable(() -> {
                                                                        try {
                                                                            try (final FileChannel outChannel = FileChannel.open(outputFile.toPath(),
                                                                                    StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                                                                                for (final File file : files) {
                                                                                    try (final FileChannel inChannel = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
                                                                                        long position = 0;
                                                                                        long size = inChannel.size();
                                                                                        while (position < size) {
                                                                                            position += inChannel.transferTo(position, size - position, outChannel);
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        } catch (Exception e) {
                                                                            throw new ResourceStreamException(
                                                                                    this.getClass(),
                                                                                    "fun execute(SimpleUploadContext.Request request)",
                                                                                    "request resource stream exception"
                                                                            );
                                                                        }
                                                                    })
                                                                    .subscribeOn(Schedulers.boundedElastic())
                                                                    .then(fileWriter
                                                                            .execute()
                                                                            .flatMap(_ -> uploadRepository.update(new FileUploadModel()
                                                                                    .setId(m.getId())
                                                                                    .setSize(outputFile.length())
                                                                                    .setStorageType(upload.getType().name())
                                                                                    .setStorageLocation(fileRelativePath)
                                                                            ))
                                                                            .flatMap(_ -> uploadRepository.select(m.getId()))
                                                                            .map(fum -> CopyUtil.run(fum, SliceUploadContext.Close.Dto.class))
                                                                    );
                                                        }
                                                    });
                                        } else {
                                            return Mono.error(new ResourcePermissionException(
                                                    this.getClass(),
                                                    "fun close(SliceUploadContext.Close.Request request)",
                                                    "request node upload permission exception"
                                            ));
                                        }
                                    });
                        }
                    });
        }
    }

    public Mono<FileUploadChunkModel> chunk(int id, int index, String signature, FilePart filePart, FileUser fileUser, Properties.Upload upload) {
        return uploadRepository
                .select(id)
                .flatMap(m -> Mono.just(m)
                        .flatMap(um -> {
                            final String chunkFolderAbsolutePath = FileUtil.convertAbsolutePath(
                                    FileUtil.composePath(upload.getSlice().getPath(), String.valueOf(um.getId())));
                            FileUtil.createFolder(chunkFolderAbsolutePath);
                            final File absolutePathFile = new File(FileUtil.composePath(chunkFolderAbsolutePath, index + "_" + signature));
                            return uploadRepository.acquireLock(um.getId(), String.valueOf(fileUser.getId()))
                                    .flatMap(_ -> filePart.transferTo(absolutePathFile).then(Mono.just(absolutePathFile)))
                                    .flatMap(_ -> uploadRepository.releaseLock(um.getId(), String.valueOf(fileUser.getId())))
                                    .map(_ -> absolutePathFile);
                        })
                        .flatMap(f -> {
                            final long size = upload.getMax();
                            if (f.length() > size) {
                                FileUtil.deleteFile(f);
                                return Mono.error(new ResourceSizeException(
                                        this.getClass(),
                                        "fun execute(SliceUploadContext context)",
                                        "execute(...) file (" + f.getName() + ") upload exceeds the maximum length limit.")
                                );
                            }
                            return Mono.just(f);
                        })
                        .flatMap(f -> fileSignature.execute(f).flatMap(s -> {
                            if (!s.equals(signature)) {
                                FileUtil.deleteFile(f);
                                return Mono.error(new ResourceSignatureException(
                                        this.getClass(),
                                        "fun execute(SliceUploadContext context)",
                                        "execute(...) file (" + f.getName() + ") incorrect signature content")
                                );
                            }
                            return Mono.just(f);
                        }))
                        .flatMap(f -> {
                            final FileUploadChunkModel model = new FileUploadChunkModel();
                            model.setFid(m.getId());
                            model.setSize(f.length());
                            model.setName(f.getName());
                            model.setCreator(String.valueOf(fileUser.getId()));
                            model.setModifier(String.valueOf(fileUser.getId()));
                            return uploadChunkRepository.create(model);
                        })
                );
    }

}
