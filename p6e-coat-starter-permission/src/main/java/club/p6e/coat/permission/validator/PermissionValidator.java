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
     * @param path   Request Path
     * @param method Request Method
     * @param groups Permission Group
     * @return Permission Details Object
     */
    PermissionDetails execute(String path, String method, List<String> groups);

    /**
     * Validate Request Permission
     *
     * @param path    Request Path
     * @param method  Request Method
     * @param project Request Project
     * @param groups  Permission Group
     * @return Permission Details Object
     */
    PermissionDetails execute(String path, String method, String project, List<String> groups);

}
