package club.p6e.coat.resource;

import org.springframework.http.MediaType;

/**
 * File Attribute
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileAttribute {

    /**
     * Get File Name
     *
     * @return File Name
     */
    String getName();

    /**
     * Get File Length
     *
     * @return File Length
     */
    long getLength();

    /**
     * Get File Media Type
     *
     * @return File Media Type
     */
    MediaType getMediaType();

}
