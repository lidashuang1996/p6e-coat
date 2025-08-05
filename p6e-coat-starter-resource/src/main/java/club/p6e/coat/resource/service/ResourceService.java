package club.p6e.coat.resource.service;

import club.p6e.coat.resource.FileReader;
import club.p6e.coat.resource.actuator.FileReadActuator;
import club.p6e.coat.resource.context.ResourceContext;
import reactor.core.publisher.Mono;

/**
 * 资源查看服务
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ResourceService {

    /**
     * 执行资源查看操作
     *
     * @param context 资源查看上下文对象
     * @return 结果对象
     */
    Mono<FileReader> execute(ResourceContext context);

}
