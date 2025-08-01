package club.p6e.coat.resource;

import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.resource.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 上传文件的本地存储路径服务
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = FolderStorageLocationPathService.class,
        ignored = FolderStorageLocationPathServiceImpl.class
)
public class FolderStorageLocationPathServiceImpl implements FolderStorageLocationPathService {

    private final static Logger LOGGER = LoggerFactory.getLogger(FolderStorageLocationPathServiceImpl.class);

    /**
     * 时间格式化对象
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public String path() {
        LOGGER.info("path >>>>>>>>>>>>>");
        // 文件路径由时间 + UUID 生成
        return FileUtil.composePath(DATE_TIME_FORMATTER.format(LocalDateTime.now()),
                GeneratorUtil.uuid() + GeneratorUtil.random(6, true, false));
    }

}
