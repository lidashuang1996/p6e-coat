package club.p6e.coat.permission.matcher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Permission Path Matcher Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Slf4j
@ConditionalOnMissingBean(PermissionGroupMatcher.class)
public class PermissionGroupMatcherImpl implements PermissionGroupMatcher {

    /**
     * Cache Object
     * Key ( Permission Group ID ) / Value ( Parent Permission Group ID List )
     */
    private final ConcurrentHashMap<String, List<String>> cache = new ConcurrentHashMap<>();

    @Override
    public boolean match(Set<String> user, String target) {
        if (user == null || user.isEmpty() || target == null) {
            return false;
        }
        if (user.contains(target)) {
            return true;
        }
        int counter = 16;
        List<String> list = List.of(target);
        while (!list.isEmpty()) {
            final Set<String> temporary = new HashSet<>();
            for (final String i : list) {
                final List<String> parent = cache.get(i);
                if (parent != null && !parent.isEmpty()) {
                    for (final String j : parent) {
                        if (user.contains(j)) {
                            return true;
                        }
                    }
                    temporary.addAll(parent);
                }
            }
            list = List.copyOf(temporary);
            counter--;
            if (counter <= 0) {
                break;
            }
        }
        return false;
    }

    @Override
    public void refresh(Map<String, List<String>> data) {
        cache.clear();
        for (final String key : data.keySet()) {
            cache.put(key, data.get(key));
        }
    }

}
