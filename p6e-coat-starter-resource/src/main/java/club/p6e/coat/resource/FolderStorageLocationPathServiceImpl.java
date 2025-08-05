package club.p6e.coat.resource;

import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.resource.utils.FileUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Folder Storage Location Path Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(FolderStorageLocationPathServiceImpl.class)
public class FolderStorageLocationPathServiceImpl implements FolderStorageLocationPathService {

    /**
     * Date Time Formatter Object
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public String execute() {
        return FileUtil.composePath(DATE_TIME_FORMATTER.format(LocalDateTime.now()), GeneratorUtil.uuid() + GeneratorUtil.random(6, true, false));
    }

}
