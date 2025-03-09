package club.p6e.coat.auth.client;

import club.p6e.coat.auth.client.cache.AuthCache;
import club.p6e.coat.auth.client.cache.AuthCacheReactive;
import club.p6e.coat.auth.client.cache.AuthStateCacheReactive;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.controller.BaseWebFluxController;
import club.p6e.coat.common.error.*;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import club.p6e.coat.common.utils.reactor.HttpUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/sso/oauth2/auth")
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class AuthWebFluxClientController extends BaseWebFluxController {

    protected final Properties properties;
    protected final AuthCacheReactive authCache;
    protected final AuthStateCacheReactive authStateCache;

    @SuppressWarnings("ALL")
    protected final AuthJsonWebTokenCipher authJsonWebTokenCipher;

    public AuthWebFluxClientController(
            Properties properties,
            AuthCacheReactive authCache,
            AuthStateCacheReactive authStateCache,
            AuthJsonWebTokenCipher authJsonWebTokenCipher
    ) {
        this.authCache = authCache;
        this.properties = properties;
        this.authStateCache = authStateCache;
        this.authJsonWebTokenCipher = authJsonWebTokenCipher;
    }

    @SuppressWarnings("ALL")
    @GetMapping("")
    public Mono<Void> def(ServerHttpRequest request, ServerHttpResponse response) {
        final String source = getParam(request, "source");
        final String redirectUri = getParam(request, "redirect_uri", "redirectUri");
        final String state = GeneratorUtil.random(8, true, false);
        return authStateCache
                .set(state, source == null ? "" : source)
                .flatMap(b -> {
                    if (b) {
                        response.setStatusCode(HttpStatus.FOUND);
                        response.getHeaders().setLocation(URI.create(properties.getAuthorizeUrl()
                                + "?response_type=code"
                                + "&client_id=" + properties.getAuthorizeAppId()
                                + "&redirect_uri=" + (redirectUri == null ? properties.getAuthorizeAppRedirectUri() : redirectUri)
                                + "&scope=open_id,user_info"
                                + "&state=" + state));
                        return response.setComplete();
                    } else {
                        return Mono.error(new CacheException(
                                this.getClass(),
                                "fun def(ServerHttpRequest request, ServerHttpResponse response).",
                                "Request state cache write exception."
                        ));
                    }
                });
    }

    @SuppressWarnings("ALL")
    @RequestMapping("/callback")
    public Mono<ResultContext> callback(ServerHttpRequest request, ServerHttpResponse response) {
        final String code = getParam(request, "code");
        final String state = getParam(request, "state");
        final String redirectUri = getParam(request, "redirect_uri", "redirectUri");
        if (code == null || state == null) {
            return Mono.error(new ParameterException(
                    this.getClass(),
                    "fun callback(ServerHttpRequest request, ServerHttpResponse response).",
                    "Request parameter code/state does not exist exception."
            ));
        } else {
            return authStateCache.getAndDel(state)
                    .flatMap(cache -> {
                        if (cache == null) {
                            return Mono.error(new CacheException(
                                    this.getClass(),
                                    "fun callback(ServerHttpRequest request, ServerHttpResponse response).",
                                    "Request parameter cache data does no exist exception."
                            ));
                        } else {
                            return HttpUtil.doPost(
                                    properties.getAuthorizeTokenUrl(),
                                    new HashMap<>(),
                                    JsonUtil.toJson(new HashMap<>() {{
                                        put("code", code);
                                        put("grantType", "authorization_code");
                                        put("clientId", properties.getAuthorizeAppId());
                                        put("clientSecret", properties.getAuthorizeAppSecret());
                                        put("redirectUri", redirectUri == null ? properties.getAuthorizeAppRedirectUri() : redirectUri);
                                    }})
                            ).flatMap(result -> {
                                final AuthModel.BaseResultModel brm = JsonUtil.fromJson(result, AuthModel.BaseResultModel.class);
                                if (brm == null || brm.getCode() != 0) {
                                    return Mono.error(new ResourceException(
                                            this.getClass(),
                                            "fun callback(HttpServletRequest request, HttpServletResponse response).",
                                            "Get user information exception."
                                    ));
                                } else {
                                    final AuthModel.TokenResultModel trm = JsonUtil.fromJson(result, AuthModel.TokenResultModel.class);
                                    if (trm == null || trm.getData() == null) {
                                        return Mono.error(new ResourceException(
                                                this.getClass(),
                                                "fun callback(HttpServletRequest request, HttpServletResponse response).",
                                                "Get user information exception."
                                        ));
                                    } else {
                                        final String user = TransformationUtil.objectToString(trm.getData().getUser());
                                        if (user == null) {
                                            return Mono.error(new ResourceException(
                                                    this.getClass(),
                                                    "fun callback(HttpServletRequest request, HttpServletResponse response).",
                                                    "Get user information exception."
                                            ));
                                        } else {
                                            final AuthModel.UserModel um = JsonUtil.fromJson(user, AuthModel.UserModel.class);
                                            if (um == null) {
                                                return Mono.error(new ResourceException(
                                                        this.getClass(),
                                                        "fun callback(HttpServletRequest request, HttpServletResponse response).",
                                                        "Get user information exception."
                                                ));
                                            } else {
                                                return authorization(String.valueOf(um.getId()), user, response)
                                                        .map(data -> {
                                                            data.put("entranceUrl", cache);
                                                            return ResultContext.build(data);
                                                        });
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
        }
    }

    @SuppressWarnings("ALL")
    @PutMapping("/refresh")
    public Mono<ResultContext> refresh(ServerHttpRequest request, ServerHttpResponse response) {
        final String accessToken = getAccessToken(request);
        final String refreshToken = getRefreshToken(request);
        if (accessToken == null || refreshToken == null) {
            return Mono.error(new AuthException(
                    this.getClass(),
                    "fun refresh(ServerHttpRequest request, ServerHttpResponse response).",
                    "Request not exist authentication information exception."
            ));
        } else {
            return authCache.getAccessToken(accessToken)
                    .flatMap(at -> authCache.getRefreshToken(refreshToken)
                            .map(rt -> at.getAccessToken().equals(rt.getAccessToken())
                                    && at.getRefreshToken().equals(rt.getRefreshToken()))
                            .filter(b -> b)
                            .flatMap(b -> authCache
                                    .getUser(at.getUid())
                                    .flatMap(user -> authCache
                                            .cleanAccessToken(at.getAccessToken())
                                            .flatMap(l -> authorization(at.getUid(), user, response))
                                            .map(ResultContext::build)))
                    ).switchIfEmpty(Mono.error(new AuthException(
                            this.getClass(),
                            "fun refresh(ServerHttpRequest request, ServerHttpResponse response).",
                            "Request authentication information has exception."
                    )));
        }
    }

    @SuppressWarnings("ALL")
    @DeleteMapping("/logout")
    public Mono<ResultContext> logout(ServerHttpRequest request, ServerHttpResponse response) {
        final String accessToken = getAccessToken(request);
        if (accessToken == null) {
            return Mono.error(new AuthException(
                    this.getClass(),
                    "fun logout(ServerHttpRequest request, ServerHttpResponse response).",
                    "Request not exist authentication information exception."
            ));
        } else {
            return authCache
                    .getAccessToken(accessToken)
                    .switchIfEmpty(Mono.error(new AuthException(
                            this.getClass(),
                            "fun logout(ServerHttpRequest request, ServerHttpResponse response).",
                            "Request authentication information has exception."
                    )))
                    .flatMap(m -> authCache
                            .cleanAccessToken(m.getAccessToken())
                            .switchIfEmpty(Mono.error(new CacheException(
                                    this.getClass(),
                                    "fun logout(ServerHttpRequest request, ServerHttpResponse response).",
                                    "Request clean token data exception."
                            ))).map(l -> ResultContext.build()));
        }
    }

    @SuppressWarnings("ALL")
    protected Mono<Map<String, Object>> authorization(String id, String info, ServerHttpResponse response) {
        /*
        // JWT
        final Date date = Date.from(LocalDateTime.now().plusSeconds(3600L).atZone(ZoneId.systemDefault()).toInstant());
        final String accessToken = JWT.create().withAudience(String.valueOf(id)).withExpiresAt(date)
                .withSubject(info).sign(Algorithm.HMAC256(authJsonWebTokenCipher.getAccessTokenSecret()));
        final String refreshToken = JWT.create().withAudience(String.valueOf(id)).withExpiresAt(date)
                .withSubject(info).sign(Algorithm.HMAC256(authJsonWebTokenCipher.getRefreshTokenSecret()));
         */

        /*
        // COOKIES
        final ResponseCookie accessTokenCookie = ResponseCookie.from("P6E_ACCESS_TOKEN", accessToken)
                .maxAge(Duration.ofSeconds(3600L)).path("/").httpOnly(true).build();
        final ResponseCookie refreshTokenCookie = ResponseCookie.from("P6E_REFRESH_TOKEN", refreshToken)
                .maxAge(Duration.ofSeconds(3600L)).path("/").httpOnly(true).build();
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ResultContext.build();
        */

        final String accessToken = GeneratorUtil.uuid() + GeneratorUtil.random();
        final String refreshToken = GeneratorUtil.uuid() + GeneratorUtil.random();
        return authCache.set(id, "PC", accessToken, refreshToken, info)
                .map(t -> new HashMap<>() {{
                    put("accessToken", accessToken);
                    put("refreshToken", refreshToken);
                    put("tokenType", AUTH_HEADER_TOKEN_TYPE);
                    put("expire", AuthCache.EXPIRATION_TIME);
                }});
    }

}
