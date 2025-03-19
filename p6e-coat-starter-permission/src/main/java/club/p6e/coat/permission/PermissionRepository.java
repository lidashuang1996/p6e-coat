package club.p6e.coat.permission;

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
    Object getPermissionDetailsList(Integer page, Integer size);

}
