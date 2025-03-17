package club.p6e.coat.auth;

import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface UserBuilder {

    public User create(String content);

    public User create(Map<String, Object> content);

}
