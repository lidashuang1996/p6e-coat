package club.p6e.coat.resource.service;

import club.p6e.coat.resource.context.SliceUploadContext;
import reactor.core.publisher.Mono;

/**
 * Slice Upload Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface SliceUploadService {

    /**
     * Open Slice Upload
     *
     * @param request Slice Upload Context Open Request Object
     * @return Slice Upload Context Open Dto Object
     */
    Mono<SliceUploadContext.Open.Dto> open(SliceUploadContext.Open.Request request);

    /**
     * Chunk Upload
     *
     * @param request Slice Upload Context Chunk Request Object
     * @return Slice Upload Context Chunk Dto Object
     */
    Mono<SliceUploadContext.Chunk.Dto> chunk(SliceUploadContext.Chunk.Request request);

    /**
     * Close Slice Upload
     *
     * @param request Slice Upload Context Close Request Object
     * @return Slice Upload Context Close Dto Object
     */
    Mono<SliceUploadContext.Close.Dto> close(SliceUploadContext.Close.Request request);

}
