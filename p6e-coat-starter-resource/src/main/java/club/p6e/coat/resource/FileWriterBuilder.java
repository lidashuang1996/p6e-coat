package club.p6e.coat.resource;

import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Map;

/**
 * File Writer Builder
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileWriterBuilder {

    /**
     * Build File Writer Object
     *
     * @param type       Type
     * @param attributes Attributes
     * @param mono       File Object
     * @return File Writer Object
     */
    FileWriter build(String type, Map<String, Object> attributes, Mono<File> mono);

}
