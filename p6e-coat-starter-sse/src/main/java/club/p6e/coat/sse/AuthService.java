package club.p6e.coat.sse;

/**
 * Auth Service
 *
 * @author lidashuang
 * @version 1.0
 */
public interface AuthService {

    /**
     * Validate
     *
     * @param name Channel Name
     * @param uri  Request Uri
     * @return User Object
     */
    User validate(String name, String uri);

}
