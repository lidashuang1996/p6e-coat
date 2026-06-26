package club.p6e.coat.resource;

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
     * Build File Writer
     *
     * @param file                   File Object
     * @param fileResourceType       File Resource Type Object
     * @param fileResourceAttributes File Resource Attributes Object
     * @return File Writer Object
     */
    FileWriter build(File file, FileResourceType fileResourceType, Map<String, Object> fileResourceAttributes);

}
