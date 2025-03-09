package club.p6e.coat.auth.validator;

import club.p6e.coat.auth.context.OAuth2Context;
import org.springframework.http.server.reactive.ServerHttpRequest;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * OAUTH2 AUTH 参数
 *
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2TokenParameterValidator implements ParameterValidatorInterface {

    /**
     * 执行顺序
     */
    private static final int ORDER = 0;

    /**
     * 授予类型参数
     */
    private static final String GRANT_TYPE_PARAM = "grant_type";

    /**
     * 重定向参数
     */
    private static final String REDIRECT_URI_PARAM = "redirect_uri";

    /**
     * 客户端编号参数
     */
    private static final String CLIENT_ID_PARAM = "client_id";

    /**
     * 客户端密钥参数
     */
    private static final String CLIENT_SECRET_PARAM = "client_secret";

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public Class<?> select() {
        return OAuth2Context.Token.Request.class;
    }

    @Override
    public Mono<Boolean> execute(ServerWebExchange exchange, Object data) {
        if (data instanceof final OAuth2Context.Token.Request param) {
            final ServerHttpRequest request = exchange.getRequest();
            if (param.getGrantType() == null) {
                param.setGrantType(request.getQueryParams().getFirst(GRANT_TYPE_PARAM));
            }
            if (param.getRedirectUri() == null) {
                param.setRedirectUri(request.getQueryParams().getFirst(REDIRECT_URI_PARAM));
            }
            if (param.getClientId() == null) {
                param.setClientId(request.getQueryParams().getFirst(CLIENT_ID_PARAM));
            }
            if (param.getClientId() == null) {
                param.setClientSecret(request.getQueryParams().getFirst(CLIENT_SECRET_PARAM));
            }
            return Mono.just((param.getGrantType() != null
                    && param.getClientId() != null
                    && param.getClientSecret() != null
                    && param.getRedirectUri() != null
                    && param.getCode() != null
            ) || (param.getGrantType() != null
                    && param.getClientId() != null
                    && param.getClientSecret() != null
                    && param.getUsername() != null
                    && param.getPassword() != null
            ));
        }
        return Mono.just(false);
    }

}
