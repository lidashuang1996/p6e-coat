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
     * @param type       Type
     * @param attributes Attributes
     * @param file       File Object
     * @return File Reader Object
     */
    FileReader build(String type, Map<String, Object> attributes, File file);

    /**
     * Build File Reader Object
     *
     * @param type       Type
     * @param attributes Attributes
     * @param file       File Object
     * @param attribute  File Attribute Object
     * @return File Reader Object
     */
    FileReader build(String type, Map<String, Object> attributes, File file, FileAttribute attribute);

}
