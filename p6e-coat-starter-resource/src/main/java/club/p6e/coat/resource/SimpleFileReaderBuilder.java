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
@Component
@ConditionalOnMissingBean(FileReaderBuilder.class)
public class SimpleFileReaderBuilder implements FileReaderBuilder {

    @Override
    public FileReader build(String type, Map<String, Object> attributes, File file) {
        return build(type, attributes, file, new SimpleFileAttribute(file));
    }

    @Override
    public FileReader build(String type, Map<String, Object> attributes, File file, FileAttribute attribute) {
        if ("DISK".equalsIgnoreCase(type)) {
            return new SimpleFileReader(file, attribute);
        }
        return null;
    }

}
