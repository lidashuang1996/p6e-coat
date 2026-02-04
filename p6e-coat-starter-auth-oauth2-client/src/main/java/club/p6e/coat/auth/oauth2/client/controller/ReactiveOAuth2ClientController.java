package club.p6e.coat.auth.oauth2.client.controller;

import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.oauth2.client.Properties;
import club.p6e.coat.auth.oauth2.client.cache.ReactiveOAuth2StateCache;
import club.p6e.coat.auth.token.*;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.controller.ReactiveWebUtil;
import club.p6e.coat.common.error.*;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.reactor.HttpUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Reactive Token Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@ConditionalOnMissingBean(ReactiveOAuth2ClientController.class)
@RestController("club.p6e.coat.auth.oauth2.client.controller.ReactiveOAuth2ClientController")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveOAuth2ClientController {

    /**
     * User Builder Object
     */
    private final UserBuilder builder;

    /**
     * Reactive Token Cleaner Object
     */
    private final ReactiveTokenCleaner cleaner;

    /**
     * Reactive Token Generator Object
     */
    private final ReactiveTokenGenerator generator;

    /**
     * Reactive Token Validator Object
     */
    private final ReactiveTokenValidator validator;

    /**
     * Reactive OAuth2 State Cache Object
     */
    private final ReactiveOAuth2StateCache stateCache;

    /**
     * Reactive OAuth2 Client Controller
     *
     * @param builder    User Builder Object
     * @param cleaner    Reactive User Token Cache Object
     * @param generator  Reactive Token Generator Object
     * @param validator  Reactive Token Validator Object
     * @param stateCache Reactive OAuth2 State Cache Object
     */
    public ReactiveOAuth2ClientController(
            UserBuilder builder,
            ReactiveTokenCleaner cleaner,
            ReactiveTokenGenerator generator,
            ReactiveTokenValidator validator,
            ReactiveOAuth2StateCache stateCache
    ) {
        this.builder = builder;
        this.cleaner = cleaner;
        this.generator = generator;
        this.validator = validator;
        this.stateCache = stateCache;
    }

    @GetMapping("/sso/oauth2/auth")
    public Mono<Void> auth(ServerWebExchange exchange) {
        final Properties properties = Properties.getInstance();
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();
        final Map<String, String> params = ReactiveWebUtil.getParams(request);
        final String source = ReactiveWebUtil.getParam(request, "source");
        final String redirectUri = ReactiveWebUtil.getParam(request, "redirect_uri", "redirectUri");
        final String state = GeneratorUtil.random(8, true, false);
        return stateCache.set(state, source == null ? "" : source)
                .switchIfEmpty(Mono.error(new CacheException(
                        this.getClass(),
                        "fun Mono<Void> auth(ServerWebExchange exchange)",
                        "request oauth2 state cache write exception"
                )))
                .flatMap(b -> {
                    final StringBuilder extend = new StringBuilder();
                    params.forEach((key, value) -> {
                        if ("source".equalsIgnoreCase(key) || "redirect_uri".equalsIgnoreCase(key) || "redirectUri".equalsIgnoreCase(key)) {
                            extend.append("&").append(key).append("=").append(URLEncoder.encode(params.get(key), StandardCharsets.UTF_8));
                        }
                    });
                    final String url = TemplateParser.execute(properties.getAuthorizeTemplate(), Map.of(
                            "URL", properties.getAuthorizeUrl(),
                            "SCOPE", properties.getAuthorizeScope(),
                            "APP_ID", properties.getAuthorizeAppId(),
                            "EXTEND", extend.isEmpty() ? "" : extend.toString(),
                            "STATE", state,
                            "URI", redirectUri == null ? properties.getAuthorizeAppRedirectUri() : redirectUri
                    ));
                    response.setStatusCode(HttpStatus.FOUND);
                    response.getHeaders().setLocation(URI.create(url));
                    return response.setComplete();
                });
    }

    @RequestMapping("/sso/oauth2/auth/callback")
    public Mono<ResultContext> callback(ServerWebExchange exchange) {
        final Properties properties = Properties.getInstance();
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();
        final String code = ReactiveWebUtil.getParam(request, "code");
        final String state = ReactiveWebUtil.getParam(request, "state");
        final String redirectUri = ReactiveWebUtil.getParam(request, "redirect_uri", "redirectUri");
        if (code == null || state == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun Mono<ResultContext> callback(ServerHttpRequest request, ServerHttpResponse response)",
                    "request parameter<code/state> does not exist exception"
            ));
        } else {
            return stateCache.get(state)
                    .switchIfEmpty(Mono.error(new CacheException(
                            this.getClass(),
                            "fun Mono<ResultContext> callback(HttpServletResponse response)",
                            "request parameter cache data does no exist exception"
                    )))
                    .flatMap(cache -> {
                        return HttpUtil.doPost(TemplateParser.execute(properties.getAuthorizeTokenUrl(), Map.of(
                                "code", code,
                                "grant_type", "authorization_code",
                                "client_id", properties.getAuthorizeAppId(),
                                "client_secret", properties.getAuthorizeAppSecret(),
                                "redirect_uri", properties.getAuthorizeAppRedirectUri()
                        ))).flatMap(result -> {
                            return stateCache.del(state)
                                    .switchIfEmpty(Mono.error(new CacheException(
                                            this.getClass(),
                                            "fun Mono<ResultContext> callback(HttpServletResponse response)",
                                            "request parameter cache data does no exist exception"
                                    )))
                                    .flatMap(l -> {
                                        final AuthCallbackModel acm = JsonUtil.fromJson(result, AuthCallbackModel.class);
                                        if (acm == null || acm.getCode() != 0) {
                                            return Mono.error(new ResourceException(
                                                    this.getClass(),
                                                    "fun ResultContext callback(HttpServletResponse response)",
                                                    "oauth2 callback get user information exception"
                                            ));
                                        } else {
                                            return HttpUtil.doPost(TemplateParser.execute(
                                                    properties.getAuthorizeLogoutUrl(),
                                                    Map.of("token", acm.getData().getToken())
                                            )).flatMap(r -> authorization(acm, exchange)).map(ResultContext::build);
                                        }
                                    });
                        });
                    });
        }
    }

    @DeleteMapping("/logout")
    public Mono<ResultContext> logout(ServerWebExchange exchange) {
        return validator.execute(exchange).map(u -> ResultContext.build(cleaner.execute(exchange)));
    }

    /**
     * Authorization
     *
     * @param acm      Auth Callback Model Object
     * @param exchange Server Web Exchange Object
     * @return Result Object
     */
    protected Mono<Object> authorization(AuthCallbackModel acm, ServerWebExchange exchange) {
        return generator.execute(exchange, builder.create(acm.getData().getUser()));
    }

    /**
     * Auth Callback Model
     */
    @Data
    @Accessors(chain = true)
    public static class AuthCallbackModel implements Serializable {
        private Integer code;
        private String message;
        private BlockingOAuth2ClientController.AuthCallbackDataModel data;
    }

    /**
     * Auth Callback Data Model
     */
    @Data
    @Accessors(chain = true)
    public static class AuthCallbackDataModel implements Serializable {
        private String oid;
        private String user;
        private String token;
        private String type;
        private String scope;
        private Long expiration;
    }

}
