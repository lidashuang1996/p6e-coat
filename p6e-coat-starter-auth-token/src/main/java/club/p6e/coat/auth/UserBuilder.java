package club.p6e.coat.auth;

import java.util.Map;

/**
 * User Builder
 *
 * @author lidashuang
 * @version 1.0
 */
public interface UserBuilder {

    /**
     * Create User Object
     * String To User Object
     *
     * @param content User Content
     * @return User Object
     */
    User create(String content);

    /**
     * Create User Object
     * Map To User Object
     *
     * @param content User Content
     * @return User Object
     */
    User create(Map<String, Object> content);

}
