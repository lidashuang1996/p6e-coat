package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.cache.UserTokenCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
public class LocalStorageCacheTokenGenerator implements TokenGenerator {

    private final UserTokenCache cache;

    public LocalStorageCacheTokenGenerator(UserTokenCache cache) {
        this.cache = cache;
    }

    public String token() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, false, false);
    }

    @Override
    public Mono<Object> execute(ServerWebExchange context, User user) {
        final String token = token();
        final String device = context.getAttribute("P6e-Device");
        return cache.set(user.id(), device, token, user.serialize()).map(m -> new HashMap<>() {{
            put("token", token);
            put("expiration", 3600);
        }});
    }

}
