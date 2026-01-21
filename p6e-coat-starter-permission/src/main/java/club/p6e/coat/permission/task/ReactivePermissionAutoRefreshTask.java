package club.p6e.coat.permission.task;

import reactor.core.publisher.Mono;

/**
 * Permission Auto Refresh Task
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactivePermissionAutoRefreshTask {

    /**
     * Execute Task
     *
     * @return Refresh Count
     */
    Mono<Long> execute();

}
