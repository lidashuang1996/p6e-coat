package club.p6e.coat.auth.token;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.cache.UserTokenCache;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class WebFluxCookieCacheTokenGenerator implements WebFluxTokenGenerator {

    private final UserTokenCache cache;

    public WebFluxCookieCacheTokenGenerator(UserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange context, User user) {
        final Properties.Token properties = Properties.getInstance().getToken();
        final String token = properties.getGenerator().execute();
        final String device = context.getAttribute(Properties.P6E_DEVICE_ATTRIBUTE_KEY);
        final ServerHttpResponse response = context.getResponse();
        return cache.set(user.id(), device, token, user.serialize()).flatMap(m -> {
            response.addCookie(ResponseCookie.from(Properties.P6E_USER_AUTH_COOKIE_NAME, token)
                    .maxAge(properties.getDuration().getSeconds()).secure(true).build());
            return Mono.just("SUCCESS");
        });
    }


}
