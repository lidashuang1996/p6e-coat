package club.p6e.coat.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;

/**
 * Disk File Writer Builder
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(FileWriterBuilder.class)
public class SimpleFileWriterBuilder implements FileWriterBuilder {

    @Override
    public FileWriter build(String type, Map<String, Object> attributes, Mono<File> mono) {
        if ("DISK".equalsIgnoreCase(type)) {
            return new SimpleFileWriter(mono);
        }
        return null;
    }

}
