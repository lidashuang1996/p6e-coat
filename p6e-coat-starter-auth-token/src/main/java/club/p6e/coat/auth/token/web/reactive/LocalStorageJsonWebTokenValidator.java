package club.p6e.coat.auth.token.web.reactive;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.token.JsonWebTokenCodec;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private static final String BEARER_TYPE = "Bearer";

    /**
     * Request Parameter Name
     */
    private static final String REQUEST_PARAMETER_NAME = "token";

    /**
     * Authorization Prefix
     */
    private static final String AUTHORIZATION_PREFIX = BEARER_TYPE + " ";

    /**
     * Authorization Header Name
     */
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    /**
     * Json Web Token Codec Object
     */
    private final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public LocalStorageJsonWebTokenValidator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public Mono<User> execute(ServerWebExchange context) {
        final ServerHttpRequest request = context.getRequest();
        final List<String> hList = request.getHeaders().get(AUTHORIZATION_HEADER_NAME);
        final List<String> pList = request.getQueryParams().get(REQUEST_PARAMETER_NAME);
        final List<String> list = new CopyOnWriteArrayList<>();
        if (hList != null) {
            list.addAll(hList);
        }
        if (pList != null) {
            list.addAll(pList);
        }
        if (!list.isEmpty()) {
            for (final String item : list) {
                if (item.startsWith(AUTHORIZATION_PREFIX)) {
                    final String content = codec.decryption(item.substring(AUTHORIZATION_PREFIX.length()));
                    if (content != null) {
                        return Mono.just(SpringUtil.getBean(UserBuilder.class).create(content));
                    }
                }
            }
        }
        return Mono.empty();
    }

}
