package club.p6e.coat.resource;

import lombok.Getter;
import org.springframework.http.MediaType;

/**
 * Simple File Attribute
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public class SimpleFileAttribute implements FileAttribute {

    /**
     * File Name
     */
    private final String name;

    /**
     * File Length
     */
    private final long length;

    /**
     * File Media Type
     */
    private final MediaType mediaType;

    /**
     * Constructor Initializers
     *
     * @param name      File Name
     * @param length    File Length
     * @param mediaType File Media Type
     */
    public SimpleFileAttribute(String name, long length, MediaType mediaType) {
        this.name = name;
        this.length = length;
        this.mediaType = mediaType;
    }

}
