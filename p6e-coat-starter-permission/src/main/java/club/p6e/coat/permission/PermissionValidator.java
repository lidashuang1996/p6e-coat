package club.p6e.coat.permission;

import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Permission Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionValidator {

    /**
     * 验证是否具备权限
     *
     * @param path   Request path
     * @param method Request method
     * @param groups Permission group
     * @return Mono/PermissionDetails Permission objects for matching path
     */
    Mono<PermissionDetails> execute(String path, String method, List<String> groups);

    /**
     * 验证是否具备权限
     *
     * @param path    Request path
     * @param method  Request method
     * @param project Request project
     * @param groups  Permission group
     * @return Mono/PermissionDetails Permission objects for matching path
     */
    Mono<PermissionDetails> execute(String path, String method, String project, List<String> groups);

}
