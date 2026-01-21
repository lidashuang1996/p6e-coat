package club.p6e.coat.auth.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Reactive Local Storage Json Web Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveLocalStorageJsonWebTokenValidator implements ReactiveTokenValidator {

    /**
     * Bearer Type
     */
    protected static final String BEARER_TYPE = "Bearer";

    /**
     * Request Parameter Name
     */
    protected static final String REQUEST_PARAMETER_NAME = "token";

    /**
     * Authorization Prefix
     */
    protected static final String AUTHORIZATION_PREFIX = BEARER_TYPE + " ";

    /**
     * Authorization Header Name
     */
    protected static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    /**
     * User Builder Object
     */
    protected final UserBuilder builder;

    /**
     * Json Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param builder User Builder Object
     * @param codec   Json Web Token Codec Object
     */
    public ReactiveLocalStorageJsonWebTokenValidator(UserBuilder builder, JsonWebTokenCodec codec) {
        this.codec = codec;
        this.builder = builder;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final List<String> hList = request.getHeaders().get(AUTHORIZATION_HEADER_NAME);
        final List<String> pList = request.getQueryParams().get(REQUEST_PARAMETER_NAME);
        final List<String> list = new ArrayList<>();
        if (hList != null) {
            list.addAll(hList);
        }
        if (pList != null) {
            list.addAll(pList);
        }
        if (!list.isEmpty()) {
            String content;
            for (final String item : list) {
                if (item.startsWith(AUTHORIZATION_PREFIX)) {
                    content = this.codec.decryption(item.substring(AUTHORIZATION_PREFIX.length()));
                } else {
                    content = this.codec.decryption(item);
                }
                if (content != null) {
                    content = content.substring(content.indexOf("@") + 1);
                    return Mono.just(this.builder.create(content));
                }
            }
        }
        return Mono.empty();
    }

}
