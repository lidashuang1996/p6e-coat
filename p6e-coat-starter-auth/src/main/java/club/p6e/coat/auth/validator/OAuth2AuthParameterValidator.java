package club.p6e.coat.auth.validator;

import club.p6e.coat.auth.context.OAuth2Context;
import club.p6e.coat.common.utils.JsonUtil;
import io.r2dbc.postgresql.codec.Json;
import org.springframework.http.server.reactive.ServerHttpRequest;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * OAUTH2 AUTH 参数
 *
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2AuthParameterValidator implements ParameterValidatorInterface {

    /**
     * 执行顺序
     */
    private static final int ORDER = 0;

    /**
     * 客户端 ID 参数
     */
    private static final String CLIENT_ID_PARAM = "client_id";

    /**
     * 重定向参数
     */
    private static final String REDIRECT_URI_PARAM = "redirect_uri";

    /**
     * 反应类型参数
     */
    private static final String RESPONSE_TYPE_PARAM = "response_type";

    @Override
    public int order() {
        return ORDER;
    }

    @Override
    public Class<?> select() {
        return OAuth2Context.Auth.Request.class;
    }

    @Override
    public Mono<Boolean> execute(ServerWebExchange exchange, Object data) {
        if (data instanceof final OAuth2Context.Auth.Request param) {
            final ServerHttpRequest request = exchange.getRequest();
            if (param.getClientId() == null) {
                param.setClientId(request.getQueryParams().getFirst(CLIENT_ID_PARAM));
            }
            if (param.getRedirectUri() == null) {
                param.setRedirectUri(request.getQueryParams().getFirst(REDIRECT_URI_PARAM));
            }
            if (param.getResponseType() == null) {
                param.setResponseType(request.getQueryParams().getFirst(RESPONSE_TYPE_PARAM));
            }
            return Mono.just(param.getScope() != null
                    && param.getClientId() != null
                    && param.getRedirectUri() != null
                    && param.getResponseType() != null);
        }
        return Mono.just(false);
    }

}
