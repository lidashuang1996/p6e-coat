package club.p6e.coat.permission;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Permission Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionRepository {

    /**
     * Query Permission Details List
     *
     * @param page Page Data
     * @param size Size Data
     * @return Permission Details List Object
     */
    Mono<List<PermissionDetails>> getPermissionDetailsList(Integer page, Integer size);

}
