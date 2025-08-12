package club.p6e.coat.resource;

import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface FileWriter {

    Mono<Void> execute();

}
