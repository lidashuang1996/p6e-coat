package club.p6e.coat.resource.service;

import club.p6e.coat.resource.context.SliceUploadContext;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Slice Upload Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface SliceUploadService {

    /**
     * 执行分片上传操作
     *
     * @param context 分片上传上下文对象
     * @return 结果对象
     */
    Mono<SliceUploadContext.Open.Dto> open(SliceUploadContext.Open.Request request);

    /**
     * 执行分片上传操作
     *
     * @param context 分片上传上下文对象
     * @return 结果对象
     */
    Mono<Map<String, Object>> chunk(SliceUploadContext.OpenRequest request);

    /**
     * 执行分片上传操作
     *
     * @param context 分片上传上下文对象
     * @return 结果对象
     */
    Mono<SliceUploadContext.Close.Dto> close(SliceUploadContext.Close.Request request);

}
