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
@ConditionalOnMissingBean(PermissionValidator.class)
public class PermissionValidatorImpl implements PermissionValidator {

    /**
     * USUAL CHAR
     */
    private static final String USUAL_CHAR = "*";

    /**
     * Permission Path Matcher Object
     */
    private final PermissionPathMatcher matcher;

    /**
     * Constructor Initialization
     *
     * @param matcher Permission Path Matcher Object
     */
    public PermissionValidatorImpl(PermissionPathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public PermissionDetails execute(String path, String method, List<String> groups) {
        PermissionDetails vague = null;
        if (groups != null) {
            final List<PermissionDetails> permissions = this.matcher.match(path);
            if (permissions != null && !permissions.isEmpty()) {
                for (final PermissionDetails permission : permissions) {
                    final String pp = permission.getPath();
                    final String pm = permission.getMethod();
                    final String pg = String.valueOf(permission.getGid());
                    final boolean gb = groups.contains(pg);
                    final boolean mb = USUAL_CHAR.equalsIgnoreCase(pm) || method.equalsIgnoreCase(pm);
                    if (gb && mb) {
                        if (pp.contains(USUAL_CHAR)) {
                            vague = permission;
                        } else {
                            return permission;
                        }
                    }
                }
            }
        }
        return vague;
    }

}
