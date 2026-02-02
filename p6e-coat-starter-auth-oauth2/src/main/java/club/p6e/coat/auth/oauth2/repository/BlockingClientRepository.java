package club.p6e.coat.auth.oauth2.repository;

import club.p6e.coat.auth.oauth2.model.ClientModel;

/**
 * Blocking Client Repository
 *
 * @author lidashuang
 * @version 1.0
 */
public interface BlockingClientRepository {

    /**
     * Query By App ID
     *
     * @param id App ID
     * @return Client Model Object
     */
    ClientModel findByAppId(String id);

}
