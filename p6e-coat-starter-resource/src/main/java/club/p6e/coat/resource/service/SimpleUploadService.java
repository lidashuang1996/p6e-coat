package club.p6e.coat.resource.service;

import club.p6e.coat.resource.context.SimpleUploadContext;
import reactor.core.publisher.Mono;

/**
 * Simple Upload Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface SimpleUploadService {

    /**
     * Execute Simple Upload Service
     *
     * @param request Simple Upload Context Request Object
     * @return Simple Upload Context Dto Object
     */
    Mono<SimpleUploadContext.Dto> execute(SimpleUploadContext.Request request);

}
