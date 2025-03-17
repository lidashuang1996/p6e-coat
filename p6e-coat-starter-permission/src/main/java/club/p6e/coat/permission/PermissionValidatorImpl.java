package club.p6e.coat.permission;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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
     * PermissionPathMatcher object
     */
    private final PermissionPathMatcher matcher;

    /**
     * Constructor initializers
     *
     * @param matcher PermissionPathMatcher object
     */
    public PermissionValidatorImpl(PermissionPathMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public Mono<PermissionDetails> execute(String path, String method, List<String> groups) {
        if (groups != null) {
            final List<PermissionDetails> permissions = matcher.match(path);
            if (permissions != null && !permissions.isEmpty()) {
                for (final PermissionDetails permission : permissions) {
                    final String pm = permission.getMethod();
                    final String pg = String.valueOf(permission.getGid());
                    if ((groups.contains("*") || groups.contains(pg))
                            && ("*".equals(pm) || method.equalsIgnoreCase(pm))) {
                        return Mono.just(permission);
                    }
                }
            }
        }
        return Mono.empty();
    }

    @Override
    public Mono<PermissionDetails> execute(String path, String method, String project, List<String> groups) {
        if (groups != null) {
            final List<PermissionDetails> permissions = matcher.match(path);
            if (permissions != null && !permissions.isEmpty()) {
                for (final PermissionDetails permission : permissions) {
                    final String pm = permission.getMethod();
                    final String pg = String.valueOf(permission.getGid());
                    final String pd = String.valueOf(permission.getPid());
                    if (pd.equals(project)
                            && (groups.contains("*") || groups.contains(pg))
                            && ("*".equals(pm) || method.equalsIgnoreCase(pm))) {
                        return Mono.just(permission);
                    }
                }
            }
        }
        return Mono.empty();
    }

}
