package club.p6e.coat.resource.task;

import club.p6e.coat.resource.Properties;
import club.p6e.coat.resource.model.UploadChunkModel;
import club.p6e.coat.resource.repository.UploadChunkRepository;
import club.p6e.coat.resource.repository.UploadRepository;
import club.p6e.coat.resource.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 实现文件分片清除策略服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = FileSliceCleanTaskStrategyService.class,
        ignored = FileSliceCleanTaskStrategyServiceImpl.class
)
public class FileSliceCleanTaskStrategyServiceImpl implements FileSliceCleanTaskStrategyService {

    /**
     * 日志系统
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileSliceCleanTaskStrategyServiceImpl.class);

    /**
     * 配置信息对象
     */
    private final Properties properties;

    /**
     * 分片上传的配置信息存储库
     */
    private final UploadRepository uploadRepository;

    /**
     * 分片上传的配置信息存储库
     */
    private final UploadChunkRepository uploadChunkRepository;

    /**
     * 构造方法初始化
     *
     * @param properties 配置信息对象
     */
    public FileSliceCleanTaskStrategyServiceImpl(
            Properties properties,
            UploadRepository uploadRepository,
            UploadChunkRepository uploadChunkRepository
    ) {
        this.properties = properties;
        this.uploadRepository = uploadRepository;
        this.uploadChunkRepository = uploadChunkRepository;
    }

    @Override
    public String cron() {
        final int s = ThreadLocalRandom.current().nextInt(2, 6);
        final int f = ThreadLocalRandom.current().nextInt(0, 60);
        final int m = ThreadLocalRandom.current().nextInt(0, 60);
        return m + " " + f + " " + s + " * * *";
    }

    @Override
    public void execute() {
        execute1();
    }

    private void execute1() {
        try {
            LOGGER.info("[ FILE TASK START ] >>> ");
            final AtomicInteger index = new AtomicInteger(0);
            final Properties.SliceUpload sliceUpload = properties.getSliceUpload();
            while (true) {
                final UploadChunkModel model = uploadChunkRepository
                        .selectExpireData(index.get(), LocalDateTime.now().minusDays(30)).block();
                if (model == null || model.getId() == null || model.getFid() == null) {
                    break;
                } else {
                    index.set(model.getId());
                    FileUtil.deleteFolder(FileUtil.composePath(sliceUpload.getPath(), String.valueOf(model.getFid())));
                    uploadRepository.delete(model.getFid()).block();
                    uploadChunkRepository.deleteByFid(model.getFid()).block();
                }
            }
            LOGGER.info("[ FILE TASK END ] !!");
        } catch (Exception e) {
            LOGGER.error("[ FILE TASK ERROR ] >>> ", e);
        }
    }

}
