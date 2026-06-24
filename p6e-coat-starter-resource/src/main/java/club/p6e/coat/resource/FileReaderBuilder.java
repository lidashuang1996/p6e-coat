package club.p6e.coat.resource;

import java.io.File;
import java.util.Map;

/**
 * File Reader Builder
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileReaderBuilder {

    /**
     * Build File Reader Object
     *
     * @param file               File Object
     * @param fileAttribute      File Attribute Object
     * @param resourceType       Resource Type
     * @param resourceAttributes Resource Attributes
     * @return File Reader Object
     */
    FileReader build(File file, FileAttribute fileAttribute, String resourceType, Map<String, Object> resourceAttributes);

}
