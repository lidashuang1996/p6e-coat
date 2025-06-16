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
     * COMMON MARK
     */
    private static final String COMMON_MARK = "*";

    /**
     * IGNORE MARK
     */
    private static final String IGNORE_MARK = "@IGNORE";

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
    public PermissionDetails execute(String mode, String path, String method, List<String> groups) {
        PermissionDetails vague = null;
        if (groups != null) {
            final List<PermissionDetails> permissions = matcher.match(path);
            if (permissions != null && !permissions.isEmpty()) {
                for (final PermissionDetails permission : permissions) {
                    final String pp = permission.getPath();
                    final String pk = permission.getMark();
                    final String pm = permission.getMethod();
                    final String pg = String.valueOf(permission.getGid());
                    final boolean gv = groups.contains(pg) || pk.toUpperCase().endsWith(IGNORE_MARK);
                    final boolean mv = COMMON_MARK.equalsIgnoreCase(pm) || method.equalsIgnoreCase(pm);
                    if (gv && mv) {
                        if (pp.contains("*")) {
                            switch (mode) {
                                case "0":
                                    vague = permission;
                                    break;
                                case "-1":
                                    return permission;
                            }
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
