package club.p6e.coat.resource;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

/**
 * File Reader
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileReader {

    /**
     * Get File Media Type Object
     *
     * @return Media Type Object
     */
    MediaType getFileMediaType();

    /**
     * Get File Attribute Object
     *
     * @return File Attribute Object
     */
    FileAttribute getFileAttribute();

    /**
     * Execute File Read Operation
     *
     * @return Data Buffer Object
     */
    Flux<DataBuffer> execute();

    /**
     * Execute File Read Operation
     *
     * @param position File Read Position
     * @param size     File Read Size
     * @return Data Buffer Object
     */
    Flux<DataBuffer> execute(long position, long size);

}
