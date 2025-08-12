package club.p6e.coat.resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
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
public class DiskFileWriterBuilder implements FileWriterBuilder {

    @Override
    public FileWriter execute(String type, Map<String, Object> attributes, MultipartFile source, String target) {
        if ("DISK".equalsIgnoreCase(type)) {
            return new DiskFileWriter(attributes, source, target);
        }
        return null;
    }

    /**
     * Disk File Writer
     */
    public static class DiskFileWriter implements FileWriter {

        /**
         * Target Object
         */
        private final String target;

        /**
         * Source Object
         */
        private final MultipartFile source;

        /**
         * Config Object
         */
        private final Map<String, Object> config;

        /**
         * Constructor Initializers
         *
         * @param config Config Object
         * @param source Multipart File Object
         */
        public DiskFileWriter(Map<String, Object> config, MultipartFile source, String target) {
            this.target = target;
            this.source = source;
            this.config = config;
        }

        @Override
        public Mono<Void> execute() {
            try {
                return source.transferTo(new File(target));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}
