package club.p6e.coat.auth;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Component
public class SimpleUserBuilder implements UserBuilder {

    @Override
    public User create(String content) {
        return new SimpleUserModel(content);
    }

    @Override
    public User create(Map<String, Object> content) {
        return new SimpleUserModel(content);
    }
}
