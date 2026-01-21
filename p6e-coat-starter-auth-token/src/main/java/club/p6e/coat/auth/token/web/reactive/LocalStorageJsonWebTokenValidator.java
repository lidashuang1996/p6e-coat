package club.p6e.coat.auth.token.web.reactive;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.token.JsonWebTokenCodec;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Local Storage Json Web Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class LocalStorageJsonWebTokenValidator implements TokenValidator {

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
    public LocalStorageJsonWebTokenValidator(UserBuilder builder, JsonWebTokenCodec codec) {
        this.builder = builder;
        this.codec = codec;
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
                    content = codec.decryption(item.substring(AUTHORIZATION_PREFIX.length()));
                } else {
                    content = codec.decryption(item);
                }
                if (content != null) {
                    content = content.substring(content.indexOf("@") + 1);
                    return Mono.just(builder.create(content));
                }
            }
        }
        return Mono.empty();
    }

}
