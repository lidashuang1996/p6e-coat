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

    /**
     * Get Permission Group List
     *
     * @param page Page
     * @param size Size
     * @return Permission Group List Object
     */
    Mono<List<Integer>> getPermissionGroupList(Integer page, Integer size);

    /**
     * Get Permission Group Parent List
     *
     * @param id Permission Group ID Object
     * @return Permission Group Parent List Object
     */
    Mono<List<Integer>> getPermissionGroupParentList(Integer id);

}
