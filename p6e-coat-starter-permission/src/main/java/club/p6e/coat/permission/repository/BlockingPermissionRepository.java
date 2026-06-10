package club.p6e.coat.permission.repository;

import club.p6e.coat.permission.PermissionDetails;

import java.util.List;
import java.util.Map;

/**
 * Blocking Permission Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingPermissionRepository {

    /**
     * Get Permission Details List
     *
     * @param page Page
     * @param size Size
     * @return Permission Details List Object
     */
    List<PermissionDetails> getPermissionDetailsList(Integer page, Integer size);

    /**
     * Get Permission Group List
     *
     * @param page Page
     * @param size Size
     * @return Permission Group Map Object
     */
    Map<String, List<String>> getPermissionGroupList(Integer page, Integer size);

}
