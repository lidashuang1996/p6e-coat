package club.p6e.coat.resource.service;

import club.p6e.coat.resource.FileReader;
import club.p6e.coat.resource.context.DownloadContext;
import reactor.core.publisher.Mono;

/**
 * Download Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface DownloadService {

    /**
     * Execute Download Service
     *
     * @param request Download Context Request Object
     * @return File Reader Object
     */
    Mono<FileReader> execute(DownloadContext.Request request);

}
