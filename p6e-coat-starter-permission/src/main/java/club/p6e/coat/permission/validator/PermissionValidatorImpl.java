package club.p6e.coat.permission.validator;

import club.p6e.coat.permission.PermissionDetails;
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
@Component
@ConditionalOnMissingBean(
        value = PermissionValidator.class,
        ignored = PermissionValidatorImpl.class
)
public class PermissionValidatorImpl implements PermissionValidator {

    /**
     * COMMON CHAR
     */
    private static final String COMMON_CHAR = "*";

    /**
     * Permission Path Matcher Object
     */
    private final PermissionPathMatcher matcher;

    /**
     * Constructor Initializers
     *
     * @param matcher Permission Path Matcher Object
     */
    public PermissionValidatorImpl(PermissionPathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public PermissionDetails execute(String path, String method, List<String> groups) {
        if (groups != null) {
            final List<PermissionDetails> permissions = matcher.match(path, 0);
            if (permissions != null && !permissions.isEmpty()) {
                for (final PermissionDetails permission : permissions) {
                    final String pm = permission.getMethod();
                    final String pg = String.valueOf(permission.getGid());
                    if ((groups.contains(COMMON_CHAR) || groups.contains(pg))
                            && (COMMON_CHAR.equals(pm) || method.equalsIgnoreCase(pm))) {
                        return permission;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public PermissionDetails execute(String path, String method, String project, List<String> groups) {
        if (groups != null) {
            final List<PermissionDetails> permissions = matcher.match(path, 0);
            if (permissions != null && !permissions.isEmpty()) {
                for (final PermissionDetails permission : permissions) {
                    final String pm = permission.getMethod();
                    final String pg = String.valueOf(permission.getGid());
                    final String pp = String.valueOf(permission.getPid());
                    if (pp.equals(project)
                            && (groups.contains(COMMON_CHAR) || groups.contains(pg))
                            && (COMMON_CHAR.equals(pm) || method.equalsIgnoreCase(pm))) {
                        return permission;
                    }
                }
            }
        }
        return null;
    }

}
