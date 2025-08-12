package club.p6e.coat.resource.service;

import club.p6e.coat.resource.FileReader;
import club.p6e.coat.resource.context.ResourceContext;
import reactor.core.publisher.Mono;

/**
 * Resource Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ResourceService {

    /**
     * Execute Resource Service
     *
     * @param request Resource Context Request Object
     * @return File Reader Object
     */
    Mono<FileReader> execute(ResourceContext.Request request);

}
