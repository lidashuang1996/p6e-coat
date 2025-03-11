package club.p6e.coat.auth.token;

import club.p6e.coat.auth.JsonWebTokenCodec;
import club.p6e.coat.auth.User;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
public class WebFluxLocalStorageJsonWebTokenValidator implements TokenValidator {

    private final JsonWebTokenCodec codec;

    public WebFluxLocalStorageJsonWebTokenValidator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final List<String> list = request.getHeaders().get("Authorization");
        if (list != null && !list.isEmpty()) {
            for (final String item : list) {
                if (item.startsWith("Bearer ")) {
                    final String content = codec.decryption(item.substring("Bearer ".length()));
                    if (content != null) {
                        return Mono.just(User.create(content));
                    }
                }
            }
        }
        return Mono.empty();
    }


}
