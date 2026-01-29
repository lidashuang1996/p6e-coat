package club.p6e.coat.auth.oauth2.repository;

import club.p6e.coat.auth.oauth2.model.ClientModel;
import reactor.core.publisher.Mono;

public interface ReactiveClientRepository {

    /**
     * Query By ID
     *
     * @param id ID
     * @return User Object
     */
    Mono<ClientModel> findByAppId(String id);
}
