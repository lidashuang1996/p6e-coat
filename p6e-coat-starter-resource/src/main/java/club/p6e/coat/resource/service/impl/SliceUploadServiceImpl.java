package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.exception.FileException;
import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.common.exception.ResourceException;
import club.p6e.coat.common.utils.CopyUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.resource.*;
import club.p6e.coat.resource.context.SliceUploadContext;
import club.p6e.coat.resource.error.NodeException;
import club.p6e.coat.resource.model.UploadChunkLogModel;
import club.p6e.coat.resource.model.UploadLogModel;
import club.p6e.coat.resource.repository.UploadChunkRepository;
import club.p6e.coat.resource.repository.UploadRepository;
import club.p6e.coat.resource.service.SliceUploadService;
import club.p6e.coat.resource.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;

/**
 * 分片上传服务
 * 步骤2: 分片上传操作
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(SliceUploadService.class)
public class SliceUploadServiceImpl implements SliceUploadService {

    private static final String SOURCE = "SLICE_UPLOAD";

    private static final Logger LOGGER = LoggerFactory.getLogger(SliceUploadServiceImpl.class);
    /**
     * 配置文件对象
     */
    private final Properties properties;
    /**
     * 上传存储库对象
     */
    private final UploadRepository uploadRepository;
    /**
     * 上传块存储库对象
     */
    private final UploadChunkRepository uploadChunkRepository;
    /**
     * 文件签名服务
     */
    private final FileSignatureService fileSignatureService;

    private final FilePermissionService filePermissionService;

    /**
     * 构造方法初始化
     *
     * @param properties            配置文件对象
     * @param uploadRepository      上传存储库对象
     * @param uploadChunkRepository 上传块存储库对象
     * @param fileSignatureService  文件签名服务对象
     */
    public SliceUploadServiceImpl(
            Properties properties,
            UploadRepository uploadRepository,
            UploadChunkRepository uploadChunkRepository,
            FileSignatureService fileSignatureService
    ) {
        this.properties = properties;
        this.uploadRepository = uploadRepository;
        this.uploadChunkRepository = uploadChunkRepository;
        this.fileSignatureService = fileSignatureService;
    }

    @Override
    public Mono<SliceUploadContext.Open.Dto> open(SliceUploadContext.Open.Request request) {
        final UploadLogModel model = new UploadLogModel();
        final String operator = request.getOperator();
        if (operator != null) {
            model.setOwner(operator);
            model.setCreator(operator);
            model.setModifier(operator);
        }
        final String name = FileUtil.name(request.getName());
        if (name == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SliceUploadContext.Open.Request request) => request parameter <name> exception",
                    "request parameter <name> exception")
            );
        } else {
            model.setName(name);
            model.setSource(SOURCE);
            return uploadRepository.create(model).map(m -> CopyUtil.run(m, SliceUploadContext.Open.Dto.class));
        }
    }

    @Override
    public Mono<Map<String, Object>> chunk(SliceUploadContext context) {
        final Integer id = context.getId();
        final Integer index = context.getIndex();
        final String signature = context.getSignature();
        // 读取并清除文件对象
        final FilePart filePart = context.getFilePart();
        context.setFilePart(null);
        return uploadRepository
                .findById(id)
                .flatMap(m -> Mono.just(m)
                        .flatMap(um -> {
                            final UploadRepository repository = SpringUtil.getBean(UploadRepository.class);
                            // 文件夹绝对路径
                            final String absolutePath = FileUtil.convertAbsolutePath(
                                    FileUtil.composePath(properties.getSliceUpload().getPath(), String.valueOf(um.getId()))
                            );
                            // 如果不存在文件夹就创建文件夹
                            FileUtil.createFolder(absolutePath);
                            final File absolutePathFile = new File(FileUtil.composePath(absolutePath, index + "_" + FileUtil.generateName()));
                            LOGGER.info("FILE absolutePathFile >>> {}", absolutePathFile);
                            return repository
                                    // 获取锁
                                    .acquireLock(um.getId())
                                    // 写入文件数据
                                    .flatMap(file -> filePart.transferTo(absolutePathFile).then(Mono.just(absolutePathFile)))
                                    // 释放锁
                                    .flatMap(file -> repository.releaseLock(um.getId()))
                                    // 转换为文件对象输出
                                    .map(l -> absolutePathFile);
                        })
                        // 验证文件数据
                        .flatMap(f -> {
                            LOGGER.info("FFF >>> {}", f);
                            final long size = properties.getSliceUpload().getMaxSize();
                            if (f.length() > size) {
                                FileUtil.deleteFile(f);
                                return Mono.error(new FileException(this.getClass(),
                                        "fun execute(SliceUploadContext context). ==> " +
                                                "execute(...) file (" + f.getName() + ") upload exceeds the maximum length limit.",
                                        "execute(...) file (" + f.getName() + ") upload exceeds the maximum length limit.")
                                );
                            }
                            return Mono.just(f);
                        })
                        .flatMap(f -> fileSignatureService
                                .execute(f)
                                .flatMap(s -> {
                                    LOGGER.info("SIGNATURE >>> {}", s);
                                    if (!s.equals(signature)) {
                                        FileUtil.deleteFile(f);
                                        return Mono.error(new FileException(this.getClass(),
                                                "fun execute(SliceUploadContext context) => " +
                                                        "execute(...) file (" + f.getName() + ") incorrect signature content.",
                                                "execute(...) file (" + f.getName() + ") incorrect signature content.")
                                        );
                                    }
                                    return Mono.just(f);
                                }))
                        .flatMap(f -> {
                            LOGGER.info("FILE F SIZE >>> {}", f);
                            final UploadChunkLogModel model = new UploadChunkLogModel();
                            model.setFid(m.getId());
                            model.setName(f.getName());
                            model.setSize(f.length());
                            final Object operator = context.get("$operator");
                            if (operator instanceof final String content) {
                                model.setCreator(content);
                                model.setModifier(content);
                            } else if (m.getOwner() != null) {
                                model.setCreator(m.getOwner());
                                model.setModifier(m.getOwner());
                            }
                            return uploadChunkRepository.create(model);
                        })
                )
                .map(UploadChunkLogModel::toMap);
    }

    @Override
    public Mono<SliceUploadContext.Close.Dto> close(SliceUploadContext.Close.Request request) {
        final Integer id = request.getId();
        final String node = request.getNode();
        final String voucher = request.getVoucher();
        if (id == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SliceUploadContext.Close.Request request) => request parameter <id> exception",
                    "request parameter <id> exception")
            );
        }
        if (node == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SliceUploadContext.Close.Request request) => request parameter <node> exception",
                    "request parameter <id> exception")
            );
        }
        if (voucher == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun execute(SliceUploadContext.Close.Request request) => request parameter <voucher> exception",
                    "request parameter <voucher> exception")
            );
        }
        final Properties.Upload uc = properties.getUploads().get(node);
        if (uc == null) {
            return Mono.error(new NodeException(
                    this.getClass(),
                    "fun execute(SliceUploadContext.Close.Request request) => request node mapper config does not exist exception",
                    "request node mapper config does not exist exception"
            ));
        }
        return filePermissionService
                .execute(FilePermissionType.UPLOAD, voucher)
                .flatMap(b -> {
                    if (b) {
                        return uploadRepository
                                .closeLock(id)
                                .flatMap(l -> uploadRepository.findById(id))
                                .flatMap(m -> {
                                    final String fileName = m.getName();
                                    final String fileSuffix = FileUtil.getSuffix(fileName);
                                    final String absolutePath = FileUtil.convertAbsolutePath(
                                            FileUtil.composePath(uc.getPath(), String.valueOf(m.getId())));
                                    final File[] files = FileUtil.readFolder(absolutePath);
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
                                    return Mono.just(SimpleFileReaderBuilder.of(files).fileName(fileName).fileSuffix(fileSuffix).build())
                                            .flatMap(fr -> {
                                                uploadRepository.update(new UploadLogModel()
                                                        .setId(m.getId())
                                                        .setSize(fr.getFileAttribute().getLength())
                                                        .setStorageType(fr.getType())
                                                        .setStorageLocation(fm.getPath()))
                                            })
                                            .flatMap(rl -> uploadRepository.findById(m.getId()))
                                            .map(UploadLogModel::toMap);
                                });
                    } else {
                        return Mono.error(new ResourceException(
                                this.getClass(),
                                "fun execute(SliceUploadContext.Close.Request request). ==> " +
                                        "execute(...) exception without permission for this node.",
                                "execute(...) exception without permission for this node.")
                        );
                    }
                });
    }

}
