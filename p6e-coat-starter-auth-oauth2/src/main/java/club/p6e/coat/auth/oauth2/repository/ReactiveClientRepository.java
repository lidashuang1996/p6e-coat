package club.p6e.coat.auth.oauth2.repository;

import club.p6e.coat.auth.oauth2.model.ClientModel;
import reactor.core.publisher.Mono;

/**
 * Reactive Client Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveClientRepository {

    /**
     * Query By App ID
     *
     * @param id App ID
     * @return Client Model Object
     */
    Mono<ClientModel> findByAppId(String id);

}
