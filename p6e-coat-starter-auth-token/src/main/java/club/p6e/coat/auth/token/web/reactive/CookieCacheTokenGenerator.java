package club.p6e.coat.auth.token.web.reactive;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Cookie Cache Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class CookieCacheTokenGenerator implements TokenGenerator {

    /**
     * Auth Cookie Name
     */
    private static final String AUTH_COOKIE_NAME = "P6E_AUTH";

    /**
     * Device Header Name
     */
    @SuppressWarnings("ALL")
    private static final String DEVICE_HEADER_NAME = "P6e-Device";

    /**
     * User Token Cache Object
     */
    private final UserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public CookieCacheTokenGenerator(UserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final String token = token();
        final String device = exchange.getRequest().getHeaders().getFirst(DEVICE_HEADER_NAME);
        return cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), duration())
                .flatMap(m -> Mono.just(ResponseCookie.from(AUTH_COOKIE_NAME, token).maxAge(duration()).secure(true).build()));
    }

    /**
     * Cache Duration
     *
     * @return Cache Duration Number
     */
    public long duration() {
        return 3600L;
    }

    /**
     * Token Content
     *
     * @return Token Content
     */
    public String token() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
    }

}
