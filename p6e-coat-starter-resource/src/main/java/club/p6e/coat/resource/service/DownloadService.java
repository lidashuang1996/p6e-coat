package club.p6e.coat.resource.service;

import club.p6e.coat.resource.FileReader;
import club.p6e.coat.resource.actuator.FileReadActuator;
import club.p6e.coat.resource.context.DownloadContext;
import reactor.core.publisher.Mono;

/**
 * 下载文件服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface DownloadService {

    /**
     * 执行下载操作
     *
     * @param context 下载文件上下文对象
     * @return 结果对象
     */
    public Mono<FileReader> execute(DownloadContext context);

}
