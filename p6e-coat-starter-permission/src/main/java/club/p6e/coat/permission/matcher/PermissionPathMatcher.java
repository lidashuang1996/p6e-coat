package club.p6e.coat.permission.matcher;

import club.p6e.coat.permission.PermissionDetails;

import java.util.List;

/**
 * Permission Path Matcher
 *
 * @author lidashuang
 * @version 1.0
 */
public interface PermissionPathMatcher {

    /**
     * Matching Path To Permission Details List Object
     *
     * @param path Path
     * @return Permission Details List Object
     */
    List<PermissionDetails> match(String path);

    /**
     * Path Permission Details Register To Cache
     *
     * @param model Permission Details List Object
     */
    void register(PermissionDetails model);

    /**
     * Clean Expired Version Data
     *
     * @param version Version ( Current Version )
     */
    void cleanExpiredVersionData(long version);

}
