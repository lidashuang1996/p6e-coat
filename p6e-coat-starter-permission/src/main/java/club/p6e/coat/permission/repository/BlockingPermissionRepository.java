package club.p6e.coat.permission.repository;

import club.p6e.coat.permission.PermissionDetails;

import java.util.List;

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
     * @return Permission Group List Object
     */
    List<Integer> getPermissionGroupList(Integer page, Integer size);

    /**
     * Get Permission Group Parent List
     *
     * @param id Permission Group ID Object
     * @return Permission Group Parent List Object
     */
    List<Integer> getPermissionGroupParentList(Integer id);

}
