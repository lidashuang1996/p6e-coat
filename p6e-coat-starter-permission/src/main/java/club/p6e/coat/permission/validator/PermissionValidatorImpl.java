package club.p6e.coat.permission.validator;

import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.matcher.PermissionGroupMatcher;
import club.p6e.coat.permission.matcher.PermissionPathMatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Permission Validator Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(PermissionValidator.class)
public class PermissionValidatorImpl implements PermissionValidator {

    /**
     * USUAL CHAR
     */
    private static final String USUAL_CHAR = "*";

    /**
     * Permission Path Matcher Object
     */
    private final PermissionPathMatcher permissionPathMatcher;

    /**
     * Permission Group Matcher Object
     */
    private final PermissionGroupMatcher permissionGroupMatcher;

    /**
     * Constructor Initialization
     *
     * @param permissionPathMatcher  Permission Path Matcher Object
     * @param permissionGroupMatcher Permission Group Matcher Object
     */
    public PermissionValidatorImpl(PermissionPathMatcher permissionPathMatcher, PermissionGroupMatcher permissionGroupMatcher) {
        this.permissionPathMatcher = permissionPathMatcher;
        this.permissionGroupMatcher = permissionGroupMatcher;
    }

    @Override
    public PermissionDetails execute(String path, String method, List<String> groups) {
        if (groups != null && !groups.isEmpty()) {
            final List<PermissionDetails> permissions = this.permissionPathMatcher.match(path);
            if (permissions != null && !permissions.isEmpty()) {
                for (final PermissionDetails permission : permissions) {
                    final String pm = permission.getMethod();
                    if (USUAL_CHAR.equalsIgnoreCase(pm) || method.equalsIgnoreCase(pm)) {
                        if (this.permissionGroupMatcher.match(groups, String.valueOf(permission.getGid()))) {
                            continue;
                        }
                    }
                    return permission;
                }
            }
        }
        return null;
    }

}
