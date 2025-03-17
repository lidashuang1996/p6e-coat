package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.user.User;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.auth.web.reactive.cache.UserTokenCache;
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
public class CookieCacheTokenGenerator implements TokenGenerator {

    /**
     * Auth Cookie Name
     */
    private static final String AUTH_COOKIE_NAME = "P6E_AUTH";

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

    /**
     * Token Content
     *
     * @return Token Content
     */
    public String token() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final Properties.Token properties = Properties.getInstance().getToken();
        final String token = token();
        final long expiration = properties.getDuration().getSeconds();
        final String device = ((ServerHttpRequest) exchange.getRequest()).getDevice();
        return cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), expiration)
                .flatMap(m -> Mono.just(ResponseCookie.from(AUTH_COOKIE_NAME, token).maxAge(expiration).secure(true).build()));
    }

}
