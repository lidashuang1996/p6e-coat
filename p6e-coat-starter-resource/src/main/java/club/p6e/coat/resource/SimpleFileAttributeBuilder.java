package club.p6e.coat.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Simple File Attribute Builder
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(FileAttributeBuilder.class)
public class SimpleFileAttributeBuilder implements FileAttributeBuilder {

    @Override
    public FileAttribute build(File file) {
        return build(file, MediaType.APPLICATION_OCTET_STREAM);
    }

    @Override
    public FileAttribute build(File file, MediaType mediaType) {
        return new SimpleFileAttribute(file, mediaType);
    }

}
