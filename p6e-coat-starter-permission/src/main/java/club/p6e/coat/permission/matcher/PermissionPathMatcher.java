package club.p6e.coat.permission.matcher;

import club.p6e.coat.permission.PermissionDetails;
import org.springframework.web.util.pattern.PathPattern;

import java.util.List;

/**
 * Permission Path Matcher
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionPathMatcher {

    /**
     * Matching Path
     *
     * @param path Path
     * @return Permission Details List Object
     */
    @SuppressWarnings("ALL")
    List<PermissionDetails> match(String path);

    /**
     * Matching Path
     *
     * @param path Path
     * @param mode Mode -1 模糊匹配 0 优选完全匹配其次模糊匹配 1 完全匹配
     * @return Permission Details List Object
     */
    @SuppressWarnings("ALL")
    List<PermissionDetails> match(String path, int mode);

    /**
     * Cache Register Path
     *
     * @param model Permission Details List Object
     */
    void register(PermissionDetails model);

    /**
     * Cache Unregister Path
     *
     * @param path Path Pattern Object
     */
    @SuppressWarnings("ALL")
    void unregister(PathPattern path);

    /**
     * Clean Expired Data
     */
    void cleanExpiredVersionData(long version);

}
