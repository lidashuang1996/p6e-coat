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
     * Build File Reader
     *
     * @param file                   File Object
     * @param fileAttribute          File Attribute Object
     * @param fileResourceType       File Resource Type Object
     * @param fileResourceAttributes File Resource Attributes Object
     * @return File Reader Object
     */
    FileReader<?> build(File file, FileAttribute fileAttribute, FileResourceType fileResourceType, Map<String, Object> fileResourceAttributes);

}
