package club.p6e.coat.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;

/**
 * Simple File Reader Builder
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(FileReaderBuilder.class)
public class SimpleFileReaderBuilder implements FileReaderBuilder {

    @Override
    public FileReader build(File file, FileAttribute fileAttribute, String resourceType, Map<String, Object> resourceAttributes) {
        if ("DISK".equalsIgnoreCase(resourceType)) {
            return new SimpleFileReader(file, resourceAttributes);
        }
        return null;
    }

}
