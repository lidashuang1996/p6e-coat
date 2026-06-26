package club.p6e.coat.resource;

import reactor.core.publisher.Mono;

import java.io.File;

/**
 * Simple File Writer
 *
 * @author lidashuang
 * @version 1.0
 */
public class SimpleFileWriter implements FileWriter {

    /**
     * File Object
     */
    private final File file;

    /**
     * Constructor Initialization
     *
     * @param file File Object
     */
    public SimpleFileWriter(File file) {
        this.file = file;
    }

    @Override
    public Mono<File> execute() {
        return Mono.just(file);
    }

}
