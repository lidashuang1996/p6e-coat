package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.cache.UserTokenCache;
import club.p6e.coat.common.utils.GeneratorUtil;
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

    public String token() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, false, false);
    }

    @Override
    public Mono<Object> execute(ServerWebExchange context, User user) {
        final String token = token();
        final String device = context.getAttribute("P6e-Device");
        final ServerHttpResponse response = context.getResponse();
        return cache.set(user.id(), device, token, user.serialize()).flatMap(m -> {
            response.addCookie(ResponseCookie.from("P6e-User-Auth", token).maxAge(3600).secure(true).build());
            return Mono.just("SUCCESS");
        });
    }


}
