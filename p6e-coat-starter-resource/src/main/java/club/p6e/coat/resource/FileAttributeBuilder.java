package club.p6e.coat.resource;

import java.io.File;
import java.util.Map;

/**
 * File Attribute Builder
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileAttributeBuilder {

    /**
     * Build File Attribute Object
     *
     * @param file                   File Object
     * @param fileResourceAttributes File Resource Attributes Object
     * @return File Attribute Object
     */
    FileAttribute build(File file, Map<String, Object> fileResourceAttributes);

}
