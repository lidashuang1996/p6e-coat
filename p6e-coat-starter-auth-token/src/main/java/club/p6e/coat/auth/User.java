package club.p6e.coat.auth;

import java.util.Map;

/**
 * User
 *
 * @author lidashuang
 * @version 1.0
 */
public interface User {

    /**
     * Get User ID
     *
     * @return User ID
     */
    String id();

    /**
     * Get User Password
     *
     * @return User Password
     */
    String password();

    /**
     * Serialize User Object
     *
     * @return Serialize String
     */
    String serialize();

    /**
     * User Object To Map Object
     *
     * @return Map Object
     */
    Map<String, Object> toMap();

    /**
     * Set User Password
     *
     * @param password Password
     * @return User Object
     */
    User password(String password);

}
