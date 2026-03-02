package club.p6e.cloud.gateway.user.token;

import club.p6e.coat.auth.User;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * User Token Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface UserRepository {

    /**
     * Get User Token Model Object
     *
     * @param id ID
     * @return User Token Model Object
     */
    Mono<Map<String, Object>> get(Integer id);

}
