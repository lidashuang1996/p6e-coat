package club.p6e.coat.resource;

import reactor.core.publisher.Mono;

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
     * @return Mono Void
     */
    Mono<Void> execute();

}
