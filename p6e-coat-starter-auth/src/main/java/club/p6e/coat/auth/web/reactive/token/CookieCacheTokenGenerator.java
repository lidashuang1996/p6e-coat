package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.cache.UserTokenCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Cookie Cache Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
public class CookieCacheTokenGenerator implements TokenGenerator {

    private final UserTokenCache cache;

    public CookieCacheTokenGenerator(UserTokenCache cache) {
        this.cache = cache;
    }

    public String token() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
    }

    @Override
    public Mono<Object> execute(ServerWebExchange context, User user) {
        final Properties.Token properties = Properties.getInstance().getToken();
        final String token = token();
        final String device = context.getAttribute(Properties.P6E_DEVICE_ATTRIBUTE_KEY);
        return cache
                .set(user.id(), device == null ? "PC" : device, token, user.serialize())
                .flatMap(m -> Mono.just(ResponseCookie
                        .from(Properties.P6E_USER_AUTH_COOKIE_NAME, token)
                        .maxAge(properties.getDuration().getSeconds()).secure(true).build()
                ));
    }

}
