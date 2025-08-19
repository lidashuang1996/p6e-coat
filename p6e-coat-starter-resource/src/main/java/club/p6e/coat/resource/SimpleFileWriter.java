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

    private final Mono<File> mono;

    public SimpleFileWriter(Mono<File> mono) {
        this.mono = mono;
    }

    @Override
    public Mono<Void> execute() {
        return null;
    }

}
