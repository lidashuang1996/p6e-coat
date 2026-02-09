package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.cache.ReactiveAuthClientCache;
import club.p6e.coat.auth.oauth2.cache.ReactiveAuthUserCache;
import club.p6e.coat.auth.oauth2.context.InfoContext;
import club.p6e.coat.common.exception.AuthException;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Reactive Info Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveInfoService.class,
        ignored = ReactiveInfoServiceImpl.class
)
@Component("club.p6e.coat.auth.oauth2.service.ReactiveInfoServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveInfoServiceImpl implements ReactiveInfoService {

    /**
     * User Info Scope
     */
    private static final String USER_INFO_SCOPE = "user_info";

    /**
     * Client Info Scope
     */
    private static final String CLIENT_INFO_SCOPE = "client_info";

    /**
     * Reactive Auth User Cache Object
     */
    private final ReactiveAuthUserCache authUserCache;

    /**
     * Reactive Auth Client Cache Object
     */
    private final ReactiveAuthClientCache authClientCache;

    /**
     * Constructor Initialization
     *
     * @param authUserCache   Reactive Auth User Cache Object
     * @param authClientCache Reactive Client User Cache Object
     */
    public ReactiveInfoServiceImpl(ReactiveAuthUserCache authUserCache, ReactiveAuthClientCache authClientCache) {
        this.authUserCache = authUserCache;
        this.authClientCache = authClientCache;
    }

    @Override
    public Mono<Map<String, Object>> getUserInfo(ServerWebExchange exchange, InfoContext.Request request) {
        final String token = request.getToken();
        return authUserCache.getToken(token)
                .filter(m -> VerificationUtil.validateOAuth2Scope(m.getScope(), USER_INFO_SCOPE))
                .switchIfEmpty(Mono.error(new AuthException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> getUserInfo(ServerWebExchange exchange, InfoContext.Request request)",
                        "token does not have the scope of " + USER_INFO_SCOPE
                )))
                .flatMap(m -> authUserCache.getUser(m.getUid()))
                .flatMap(u -> {
                    final Map<String, Object> result = JsonUtil.fromJsonToMap(u, String.class, Object.class);
                    if (result == null) {
                        return Mono.empty();
                    } else {
                        return Mono.just(result);
                    }
                })
                .switchIfEmpty(Mono.error(new AuthException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> getUserInfo(ServerWebExchange exchange, InfoContext.Request request)",
                        "token does not exist or has expired"
                )));
    }

    @Override
    public Mono<Map<String, Object>> getClientInfo(ServerWebExchange exchange, InfoContext.Request request) {
        final String token = request.getToken();
        return authClientCache.getToken(token)
                .filter(m -> VerificationUtil.validateOAuth2Scope(m.getScope(), CLIENT_INFO_SCOPE))
                .switchIfEmpty(Mono.error(new AuthException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> getClientInfo(ServerWebExchange exchange, InfoContext.Request request)",
                        "token does not have the scope of " + CLIENT_INFO_SCOPE
                )))
                .flatMap(m -> authClientCache.getClient(m.getCid()))
                .flatMap(u -> {
                    final Map<String, Object> result = JsonUtil.fromJsonToMap(u, String.class, Object.class);
                    if (result == null) {
                        return Mono.empty();
                    } else {
                        return Mono.just(result);
                    }
                })
                .switchIfEmpty(Mono.error(new AuthException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> getClientInfo(ServerWebExchange exchange, InfoContext.Request request)",
                        "token does not exist or has expired"
                )));
    }

}
