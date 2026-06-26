package club.p6e.coat.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.io.File;
import java.util.Map;

/**
 * Simple File Writer Builder
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(SimpleFileWriterBuilder.class)
public class SimpleFileWriterBuilder implements FileWriterBuilder {

    @Override
    public FileWriter build(File file, FileResourceType fileResourceType, Map<String, Object> fileResourceAttributes) {
        if (FileResourceType.DISK == fileResourceType) {
            return new SimpleFileWriter(file);
        }
        return null;
    }

}
