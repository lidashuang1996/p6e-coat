package club.p6e.coat.resource;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.MediaType;

import java.io.File;

/**
 * Simple File Reader
 *
 * @author lidashuang
 * @version 1.0
 */
public class SimpleFileReader implements FileReader<ResourceRegion> {

    /**
     * File Object
     */
    private final File file;

    /**
     * File Attribute Object
     */
    private final FileAttribute fileAttribute;

    /**
     * Constructor Initialization
     *
     * @param file          File Object
     * @param fileAttribute File Attribute Object
     */
    public SimpleFileReader(File file, FileAttribute fileAttribute) {
        this.file = file;
        this.fileAttribute = fileAttribute;
    }

    @Override
    public FileAttribute getFileAttribute() {
        return fileAttribute;
    }

    @Override
    public ResourceRegion execute() {
        return new ResourceRegion(new FileSystemResource(file), 0, file.length());
    }

    @Override
    public ResourceRegion execute(long position, long size) {
        return new ResourceRegion(new FileSystemResource(file), position, size);
    }

}
