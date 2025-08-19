package club.p6e.coat.resource;

import org.springframework.http.MediaType;

import java.io.File;

/**
 * Simple File Attribute
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
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
     * @param file File Object
     */
    public SimpleFileAttribute(File file) {
        this(file.getName(), file.length(), MediaType.APPLICATION_OCTET_STREAM);
    }

    /**
     * Constructor Initializers
     *
     * @param file      File Object
     * @param mediaType File Media Type
     */
    public SimpleFileAttribute(File file, MediaType mediaType) {
        this(file.getName(), file.length(), mediaType);
    }

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

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MediaType getMediaType() {
        return mediaType;
    }

}
