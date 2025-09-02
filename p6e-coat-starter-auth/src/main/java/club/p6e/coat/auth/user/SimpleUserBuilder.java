package club.p6e.coat.auth.user;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Simple User Builder
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(UserBuilder.class)
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
