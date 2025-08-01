package club.p6e.coat.resource.service.impl;

import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.error.ResourceException;
import club.p6e.coat.common.error.ResourceNodeException;
import club.p6e.coat.resource.FilePermissionService;
import club.p6e.coat.resource.FileReadWriteService;
import club.p6e.coat.resource.Properties;
import club.p6e.coat.resource.actuator.FileWriteActuator;
import club.p6e.coat.resource.context.SimpleUploadContext;
import club.p6e.coat.resource.model.UploadModel;
import club.p6e.coat.resource.repository.UploadRepository;
import club.p6e.coat.resource.service.SimpleUploadService;
import club.p6e.coat.resource.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 简单（小文件）上传服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = SimpleUploadService.class,
        ignored = SimpleUploadServiceImpl.class
)
public class SimpleUploadServiceImpl implements SimpleUploadService {

    /**
     * 源
     */
    private static final String SOURCE = "SIMPLE_UPLOAD";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUploadServiceImpl.class);
    /**
     * 配置文件对象
     */
    private final Properties properties;
    /**
     * 上传存储库对象
     */
    private final UploadRepository repository;
    /**
     * 文件读取写入服务对象
     */
    private final FileReadWriteService fileReadWriteService;
    /**
     * 文件权限服务对象
     */
    private final FilePermissionService filePermissionService;

    /**
     * 构造方法初始化
     *
     * @param properties            配置文件对象
     * @param repository            上传存储库对象
     * @param fileReadWriteService  文件读取写入服务对象
     * @param filePermissionService 文件权限服务对象
     */
    public SimpleUploadServiceImpl(
            Properties properties,
            UploadRepository repository,
            FileReadWriteService fileReadWriteService,
            FilePermissionService filePermissionService
    ) {
        this.properties = properties;
        this.repository = repository;
        this.fileReadWriteService = fileReadWriteService;
        this.filePermissionService = filePermissionService;
    }

    @Override
    public Mono<Map<String, Object>> execute(SimpleUploadContext context) {
        final Properties.Upload upload = properties.getUploads().get(context.getNode());
        if (upload == null) {
            return Mono.error(new ResourceNodeException(
                    this.getClass(),
                    "fun execute(SimpleUploadContext context). ==> " +
                            "execute(...) unable to find corresponding resource context node.",
                    "execute(...) unable to find corresponding resource context node.")
            );
        }
        return filePermissionService
                .execute("U", context)
                .flatMap(b -> {
                    LOGGER.info("permission >>> {}", b);
                    if (b) {
                        return Mono.defer(() -> {
                            final FilePart filePart = context.getFilePart();
                            context.setFilePart(null);
                            final String name = FileUtil.name(filePart.filename());
                            if (name == null) {
                                return Mono.error(new ParameterException(
                                        this.getClass(),
                                        "fun execute(SimpleUploadContext context). ==> " +
                                                "execute(...) request parameter <name> exception.",
                                        "execute(...) request parameter <name> exception.")
                                );
                            }
                            LOGGER.info("name >>> {}", name);
                            final UploadModel pum = new UploadModel();
                            final Object operator = context.get("$operator");
                            if (operator instanceof final String content) {
                                pum.setOwner(content);
                                pum.setCreator(content);
                                pum.setModifier(content);
                            }
                            pum.setName(name);
                            pum.setSource(SOURCE);
                            LOGGER.info("SAVE DATA >>> {}", pum);
                            return repository
                                    .create(pum)
                                    .flatMap(m -> fileReadWriteService.write(name, new HashMap<>() {{
                                        putAll(context);
                                        putAll(upload.getExtend());
                                    }}, new CustomFileWriteActuator(filePart, upload)).map(fam -> {
                                        final UploadModel rum = new UploadModel();
                                        rum.setId(m.getId());
                                        rum.setSize(fam.getLength());
                                        rum.setStorageType(fam.getType());
                                        rum.setStorageLocation(fam.getPath());
                                        return rum;
                                    })).flatMap(m -> Mono.just(m)
                                            .flatMap(l -> repository.closeLock(m.getId()))
                                            .flatMap(l -> repository.update(m))
                                            .flatMap(l -> repository.findById(m.getId()))
                                    ).map(UploadModel::toMap);
                        });
                    } else {
                        return Mono.error(new ResourceException(
                                this.getClass(),
                                "fun execute(SimpleUploadContext context). ==> " +
                                        "execute(...) exception without permission for this node.",
                                "execute(...) exception without permission for this node.")
                        );
                    }
                });
    }

    /**
     * 自定义的文件写入执行器
     *
     * @param filePart   文件写入对象
     * @param properties 上传配置对象
     */
    private record CustomFileWriteActuator(
            FilePart filePart,
            Properties.Upload properties
    ) implements FileWriteActuator {

        @Override
        public String type() {
            return properties.getType();
        }

        @Override
        public String path() {
            return properties.getPath();
        }

        @Override
        public Mono<File> execute(File file) {
            return filePart.transferTo(file).then(Mono.just(file));
        }

    }

}
