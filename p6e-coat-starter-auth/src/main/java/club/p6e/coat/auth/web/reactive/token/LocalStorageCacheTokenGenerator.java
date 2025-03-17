package club.p6e.coat.auth.web.reactive.token;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.user.User;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.auth.web.reactive.cache.UserTokenCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Local Storage Cache Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
public class LocalStorageCacheTokenGenerator implements TokenGenerator {

    /**
     * User Token Cache Object
     */
    private final UserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache User Token Cache Object
     */
    public LocalStorageCacheTokenGenerator(UserTokenCache cache) {
        this.cache = cache;
    }

    /**
     * Token Content
     *
     * @return Token Content
     */
    public String token() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, false, false);
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final Properties.Token properties = Properties.getInstance().getToken();
        final String token = token();
        final long expiration = properties.getDuration().getSeconds();
        final String device = ((ServerHttpRequest) exchange.getRequest()).getDevice();
        return cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), expiration).map(m -> new HashMap<>() {{
            put("token", token);
            put("type", "Bearer");
            put("expiration", expiration);
        }});
    }

}
