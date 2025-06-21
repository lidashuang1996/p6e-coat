package club.p6e.coat.websocket;

import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Auth Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(AuthService.class)
public class AuthServiceImpl implements AuthService {

    @Override
    public User validate(String channel, String uri) {
        return GeneratorUtil::uuid;
    }

}
