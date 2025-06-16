package club.p6e.coat.permission.validator;

import club.p6e.coat.permission.PermissionDetails;

import java.util.List;

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
     * @param mode   Validator Mode
     *               -1  Fuzzy Matching
     *               1   Perfect Matching
     *               0   Complete Matching Followed By Fuzzy Matching
     * @param path   Request Path
     * @param method Request Method
     * @param groups Permission Group
     * @return Permission Details Object
     */
    PermissionDetails execute(String mode, String path, String method, List<String> groups);

}
