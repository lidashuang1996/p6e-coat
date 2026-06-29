package club.p6e.coat.permission.validator;

import club.p6e.coat.permission.PermissionDetails;

import java.util.List;
import java.util.Set;

/**
 * Permission Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionValidator {

    /**
     * Validate Request Permission
     *
     * @param path   Request Path
     * @param method Request Method
     * @param groups Permission Group
     * @return Permission Details Object
     */
    PermissionDetails execute(String path, String method, Set<String> groups);

}
