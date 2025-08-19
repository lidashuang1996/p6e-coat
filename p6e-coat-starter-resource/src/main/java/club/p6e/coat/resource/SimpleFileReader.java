package club.p6e.coat.resource;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * Simple File Reader
 *
 * @author lidashuang
 * @version 1.0
 */
public class SimpleFileReader implements FileReader {

    private final File file;
    private final FileAttribute attribute;

    public SimpleFileReader(File file, FileAttribute attribute) {
        this.file = file;
        this.attribute = attribute;
    }

    @Override
    public MediaType getFileMediaType() {
        return attribute.getMediaType();
    }

    @Override
    public FileAttribute getFileAttribute() {
        return attribute;
    }

    @Override
    public Flux<DataBuffer> execute() {
        return null;
    }

    @Override
    public Flux<DataBuffer> execute(long position, long size) {
        return null;
    }

}
