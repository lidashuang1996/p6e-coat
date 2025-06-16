package club.p6e.coat.permission;

/**
 * Permission Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionRepository {

    /**
     * Get Permission Details List
     *
     * @param page Page
     * @param size Size
     * @return Permission Details List Object
     */
    Object getPermissionDetailsList(Integer page, Integer size);

}
