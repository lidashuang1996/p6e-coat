package club.p6e.cloud.gateway.user.token;

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
     * Get User Data Object
     *
     * @param id User ID
     * @return User Data Object
     */
    Mono<Map<String, Object>> get(Integer id);

}
