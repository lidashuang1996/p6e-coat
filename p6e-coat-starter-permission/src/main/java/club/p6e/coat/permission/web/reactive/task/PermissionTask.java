package club.p6e.coat.permission.web.reactive.task;

import reactor.core.publisher.Mono;

/**
 * Permission Task
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionTask {

    /**
     * Execute Inject Permission Data
     */
    Mono<Long> execute() ;

}
