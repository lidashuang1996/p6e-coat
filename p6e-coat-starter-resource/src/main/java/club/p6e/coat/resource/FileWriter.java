package club.p6e.coat.resource;

import reactor.core.publisher.Mono;

import java.io.File;

/**
 * File Writer
 *
 * @author lidashuang
 * @version 1.0
 */
public interface FileWriter {

    /**
     * Execute File Writer
     *
     * @return File Object
     */
    Mono<File> execute();

}
