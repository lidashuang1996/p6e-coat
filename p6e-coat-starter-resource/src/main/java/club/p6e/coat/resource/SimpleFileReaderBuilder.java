package club.p6e.coat.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.io.File;
import java.util.Map;

/**
 * Simple File Reader Builder
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(SimpleFileReaderBuilder.class)
public class SimpleFileReaderBuilder implements FileReaderBuilder {

    @Override
    public FileReader<?> build(File file, FileAttribute fileAttribute, FileResourceType fileResourceType, Map<String, Object> fileResourceAttributes) {
        if (FileResourceType.DISK == fileResourceType) {
            return new SimpleFileReader(file, fileAttribute);
        }
        return null;
    }

}
