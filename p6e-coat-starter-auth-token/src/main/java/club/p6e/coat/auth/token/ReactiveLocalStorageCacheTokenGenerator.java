package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Reactive Local Storage Cache Token Generator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveLocalStorageCacheTokenGenerator implements ReactiveTokenGenerator {

    /**
     * Reactive User Token Cache Object
     */
    protected final ReactiveUserTokenCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Reactive User Token Cache Object
     */
    public ReactiveLocalStorageCacheTokenGenerator(ReactiveUserTokenCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange, User user) {
        final String token = token();
        final int duration = duration();
        final String device = device(exchange);
        return cache.set(user.id(), device == null ? "PC" : device, token, user.serialize(), duration).map(m -> new HashMap<>() {{
            put("token", token);
            put("type", "Bearer");
            put("expiration", duration);
        }});
    }

    /**
     * Get Device Content
     *
     * @param exchange Server Web Exchange Object
     * @return Device Content
     */
    public String device(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("P6e-Device");
    }

    /**
     * Get Cookie Name
     *
     * @return Cookie Name
     */
    public String name() {
        return "P6E_AUTH";
    }

    /**
     * Get Cookie Duration
     *
     * @return Cookie Duration
     */
    public int duration() {
        return 3600;
    }

    /**
     * Get Token Content
     *
     * @return Token Content
     */
    public String token() {
        return GeneratorUtil.uuid() + GeneratorUtil.random(8, false, false);
    }

}
