package club.p6e.coat.resource;

import org.springframework.http.MediaType;

import java.io.File;

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
     * @param file File Object
     * @return File Attribute Object
     */
    FileAttribute build(File file);

    /**
     * Build File Attribute Object
     *
     * @param file      File Object
     * @param mediaType Media Type
     * @return File Attribute Object
     */
    FileAttribute build(File file, MediaType mediaType);

}
