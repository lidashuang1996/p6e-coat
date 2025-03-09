package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthPage;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.cache.OAuth2CodeCache;
import club.p6e.coat.auth.context.OAuth2Context;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.generator.OAuth2CodeGenerator;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TemplateParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * OAUTH2 二次认证确认的服务的默认实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2ReconfirmServiceImpl implements OAuth2ReconfirmService {

    /**
     * OAuth2 Code 缓存对象
     */
    private final OAuth2CodeCache cache;

    /**
     * OAuth2 Code 生成器对象
     */
    private final OAuth2CodeGenerator generator;

    /**
     * 构造方法初始化
     *
     * @param cache     OAuth2 Code 缓存对象
     * @param generator OAuth2 Code 生成器对象
     */
    public OAuth2ReconfirmServiceImpl(OAuth2CodeCache cache, OAuth2CodeGenerator generator) {
        this.cache = cache;
        this.generator = generator;
    }

    /**
     * 写入返回数据
     *
     * @param exchange ServerWebExchange 对象
     * @return Mono/Void 对象
     */
    protected Mono<Void> write(ServerWebExchange exchange, String voucher, String content) {
        final ServerHttpResponse response = exchange.getResponse();
        final AuthPage.Model oAuth2Confirm = AuthPage.oAuth2Confirm();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(oAuth2Confirm.getType());
        return response.writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(
                TemplateParser.execute(
                        oAuth2Confirm.getContent(),
                        "page", "oauth2-reconfirm", "voucher", voucher, "data", content
                ).getBytes(StandardCharsets.UTF_8)
        )));
    }

    @Override
    public Mono<Void> def(ServerWebExchange exchange) {
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> {
                    if (v.isOAuth2() && v.isOAuth2Complete()) {
                        return write(exchange, v.getMark(), JsonUtil.toJson(v.getOAuth2()));
                    } else {
                        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                        try {
                            exchange.getResponse().getHeaders().setLocation(new URL("").toURI());
                        } catch (Exception e) {
                            return Mono.error(new RuntimeException(e));
                        }
                        return exchange.getResponse().setComplete();
                    }
                });
    }

    @Override
    public Mono<OAuth2Context.Confirm.Dto> execute(ServerWebExchange exchange, OAuth2Context.Confirm.Request param) {
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> {
                    final String oauth2 = v.get(AuthVoucher.OAUTH2);
                    final String state = v.get(AuthVoucher.OAUTH2_STATE);
                    final String scope = v.get(AuthVoucher.OAUTH2_SCOPE);
                    final String clientId = v.get(AuthVoucher.OAUTH2_CLIENT_ID);
                    final String redirectUri = v.get(AuthVoucher.OAUTH2_REDIRECT_URI);
                    final String userId = v.get(AuthVoucher.OAUTH2_USER_ID);
                    final String userInfo = v.get(AuthVoucher.OAUTH2_USER_INFO);
                    if (oauth2 == null) {
                        return Mono.error(GlobalExceptionContext.executeOauth2ParameterException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, Oauth2Context.Confirm.Request param)",
                                "Oauth2 cache data oauth2 exception."
                        ));
                    }
                    if (scope == null) {
                        return Mono.error(GlobalExceptionContext.executeOauth2ParameterException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, Oauth2Context.Confirm.Request param)",
                                "Oauth2 cache data scope exception."
                        ));
                    }
                    if (clientId == null) {
                        return Mono.error(GlobalExceptionContext.executeOauth2ParameterException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, Oauth2Context.Confirm.Request param)",
                                "Oauth2 cache data client id exception."
                        ));
                    }
                    if (redirectUri == null) {
                        return Mono.error(GlobalExceptionContext.executeOauth2ParameterException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, Oauth2Context.Confirm.Request param)",
                                "Oauth2 cache data redirect uri exception."
                        ));
                    }
                    if (userId == null) {
                        return Mono.error(GlobalExceptionContext.executeOauth2ParameterException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, Oauth2Context.Confirm.Request param)",
                                "Oauth2 cache data user id exception."
                        ));
                    }
                    if (userInfo == null) {
                        return Mono.error(GlobalExceptionContext.executeOauth2ParameterException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, Oauth2Context.Confirm.Request param)",
                                "Oauth2 cache data user info exception."
                        ));
                    }
                    final String code = generator.execute();
                    final Map<String, String> map = new HashMap<>(5);
                    map.put(OAuth2CodeCache.OAUTH2_SCOPE, scope);
                    map.put(OAuth2CodeCache.OAUTH2_CLIENT_ID, clientId);
                    map.put(OAuth2CodeCache.OAUTH2_USER_ID, userId);
                    map.put(OAuth2CodeCache.OAUTH2_USER_INFO, userInfo);
                    map.put(OAuth2CodeCache.OAUTH2_REDIRECT_URI, redirectUri);
                    return cache
                            .set(code, map)
                            .flatMap(b -> b ?
                                    Mono.just(new OAuth2Context.Confirm.Dto()
                                            .setCode(code)
                                            .setState(state)
                                            .setRedirectUri(redirectUri)) :
                                    Mono.error(GlobalExceptionContext.executeCacheException(
                                            this.getClass(),
                                            "fun execute(ServerWebExchange exchange, Oauth2Context.Confirm.Request param).",
                                            "OAuth2 cache data write exception."
                                    ))
                            );
                });
    }

}
