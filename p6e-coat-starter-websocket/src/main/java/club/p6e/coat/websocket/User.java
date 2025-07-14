package club.p6e.coat.websocket;

import java.io.Serializable;

/**
 * User
 *
 * @author lidashuang
 * @version 1.0
 */
public interface User extends Serializable {

    /**
     * User ID
     *
     * @return User ID
     */
    @SuppressWarnings("ALL")
    String id();

}
