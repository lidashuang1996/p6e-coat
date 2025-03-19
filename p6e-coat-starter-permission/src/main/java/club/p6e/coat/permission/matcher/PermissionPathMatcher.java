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
     * @param mode Mode
     *             Mode -1  Fuzzy Matching
     *             Mode  1  Perfect Matching
     *             Mode  0  Complete Matching Followed By Fuzzy Matching
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
