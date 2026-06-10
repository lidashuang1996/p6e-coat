package club.p6e.coat.permission.repository;

import club.p6e.coat.permission.PermissionDetails;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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

    /**
     * Get Permission Group List
     *
     * @param page Page
     * @param size Size
     * @return Permission Group Map Object
     */
    Mono<Map<String, List<String>>> getPermissionGroupList(Integer page, Integer size);

}
