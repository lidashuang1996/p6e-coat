package club.p6e.coat.resource;

import club.p6e.coat.common.utils.FileUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.io.File;
import java.nio.file.OpenOption;

/**
 * Simple File Reader
 *
 * @author lidashuang
 * @version 1.0
 */
public class SimpleFileReader implements FileReader {

    /**
     * File Object
     */
    private final File file;

    /**
     * File Attribute Object
     */
    private final FileAttribute fileAttribute;

    private static final DefaultDataBufferFactory BUFFER_FACTORY = new DefaultDataBufferFactory();

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
    public MediaType getFileMediaType() {
        return fileAttribute.getMediaType();
    }

    @Override
    public FileAttribute getFileAttribute() {
        return fileAttribute;
    }

    @Override
    public Flux<DataBuffer> execute() {
        return FileUtil.read
    }

    @Override
    public Flux<DataBuffer> execute(long position, long size) {
        return null;
    }

}
