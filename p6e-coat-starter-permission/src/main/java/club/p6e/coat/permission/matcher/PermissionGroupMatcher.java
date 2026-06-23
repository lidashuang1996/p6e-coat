package club.p6e.coat.permission.matcher;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Permission Group Matcher
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionGroupMatcher {

    /**
     * Refresh Permission Group
     *
     * @param data Permission Group ID List Object
     */
    void refresh(Map<String, List<String>> data);

    /**
     * Permission Group ID Match
     *
     * @param user   User Permission Group ID Set Object
     * @param target Target Permission Group ID Object
     * @return Permission Group Match Result
     */
    boolean match(Set<String> user, String target);

}
