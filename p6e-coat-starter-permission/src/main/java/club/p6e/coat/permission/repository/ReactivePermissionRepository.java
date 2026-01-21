package club.p6e.coat.permission.repository;

import club.p6e.coat.permission.PermissionDetails;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Reactive Permission Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactivePermissionRepository {

    /**
     * Get Permission Details List
     *
     * @param page Page
     * @param size Size
     * @return Permission Details List Object
     */
    Mono<List<PermissionDetails>> getPermissionDetailsList(Integer page, Integer size);

}
