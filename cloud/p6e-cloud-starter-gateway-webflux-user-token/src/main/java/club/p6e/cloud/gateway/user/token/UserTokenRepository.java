package club.p6e.cloud.gateway.user.token;

import reactor.core.publisher.Mono;

/**
 * User Token Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface UserTokenRepository {

    /**
     * Get User Token Model Object
     *
     * @param token Token
     * @return User Token Model Object
     */
    Mono<UserTokenModel> get(String token);

}
