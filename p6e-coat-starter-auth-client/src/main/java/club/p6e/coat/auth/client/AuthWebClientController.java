package club.p6e.coat.auth.client;

import club.p6e.coat.auth.client.cache.AuthCache;
import club.p6e.coat.auth.client.cache.AuthStateCache;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.controller.BaseWebController;
import club.p6e.coat.common.error.AuthException;
import club.p6e.coat.common.error.CacheException;
import club.p6e.coat.common.error.ParameterException;
import club.p6e.coat.common.error.ResourceException;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.HttpUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@RestController
@RequestMapping("/sso/oauth2/auth")
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class AuthWebClientController extends BaseWebController {

    protected final AuthCache authCache;
    protected final Properties properties;
    protected final AuthStateCache authStateCache;

    @SuppressWarnings("ALL")
    protected final AuthJsonWebTokenCipher authJsonWebTokenCipher;

    public AuthWebClientController(
            Properties properties,
            AuthCache authCache,
            AuthStateCache authStateCache,
            AuthJsonWebTokenCipher authJsonWebTokenCipher
    ) {
        this.authCache = authCache;
        this.properties = properties;
        this.authStateCache = authStateCache;
        this.authJsonWebTokenCipher = authJsonWebTokenCipher;
    }

    @SuppressWarnings("ALL")
    @GetMapping("")
    public void def(HttpServletRequest request, HttpServletResponse response) {
        try {
            final String source = getParam("source");
            final String redirectUri = getParam("redirect_uri", "redirectUri");
            final String state = GeneratorUtil.random(8, true, false);
            final Boolean bool = authStateCache.set(state, source == null ? "" : source);
            if (bool == null || bool == false) {
                throw new CacheException(
                        this.getClass(),
                        "fun def(HttpServletRequest request, HttpServletResponse response).",
                        "Request state cache write exception."
                );
            } else {
                response.sendRedirect(properties.getAuthorizeUrl()
                        + "?response_type=code"
                        + "&client_id=" + properties.getAuthorizeAppId()
                        + "&redirect_uri=" + (redirectUri == null ? properties.getAuthorizeAppRedirectUri() : redirectUri)
                        + "&scope=open_id,user_info"
                        + "&state=" + state
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ALL")
    @RequestMapping("/callback")
    public ResultContext callback(HttpServletRequest request, HttpServletResponse response) {
        final String code = getParam("code");
        final String state = getParam("state");
        if (code == null || state == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun callback(HttpServletRequest request, HttpServletResponse response).",
                    "Request parameter code/state does not exist exception."
            );
        } else {
            final String cache = authStateCache.getAndDel(state);
            if (cache == null) {
                throw new CacheException(
                        this.getClass(),
                        "fun callback(HttpServletRequest request, HttpServletResponse response).",
                        "Request parameter cache data does no exist exception."
                );
            } else {
                final String result = HttpUtil.doPost(
                        properties.getAuthorizeTokenUrl(),
                        new HashMap<>(),
                        JsonUtil.toJson(new HashMap<>() {{
                            put("code", code);
                            put("grantType", "authorization_code");
                            put("clientId", properties.getAuthorizeAppId());
                            put("clientSecret", properties.getAuthorizeAppSecret());
                            put("redirectUri", properties.getAuthorizeAppRedirectUri());
                        }})
                );
                final AuthModel.BaseResultModel brm = JsonUtil.fromJson(result, AuthModel.BaseResultModel.class);
                if (brm == null || brm.getCode() != 0) {
                    throw new ResourceException(
                            this.getClass(),
                            "fun callback(HttpServletRequest request, HttpServletResponse response).",
                            "Get user information exception."
                    );
                } else {
                    final AuthModel.TokenResultModel trm = JsonUtil.fromJson(result, AuthModel.TokenResultModel.class);
                    if (trm == null || trm.getData() == null) {
                        throw new ResourceException(
                                this.getClass(),
                                "fun callback(HttpServletRequest request, HttpServletResponse response).",
                                "Get user information exception."
                        );
                    } else {
                        final String user = TransformationUtil.objectToString(trm.getData().getUser());
                        if (user == null) {
                            throw new ResourceException(
                                    this.getClass(),
                                    "fun callback(HttpServletRequest request, HttpServletResponse response).",
                                    "Get user information exception."
                            );
                        } else {
                            final AuthModel.UserModel um = JsonUtil.fromJson(user, AuthModel.UserModel.class);
                            if (um == null) {
                                throw new ResourceException(
                                        this.getClass(),
                                        "fun callback(HttpServletRequest request, HttpServletResponse response).",
                                        "Get user information exception."
                                );
                            } else {
                                final Map<String, Object> data = authorization(String.valueOf(um.getId()), user, response);
                                data.put("url", cache);
                                return ResultContext.build(data);
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("ALL")
    @PutMapping("/refresh")
    public ResultContext refresh(HttpServletRequest request, HttpServletResponse response) {
        final String accessToken = getAccessToken();
        final String refreshToken = getRefreshToken();
        if (accessToken == null || refreshToken == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun refresh(ServerHttpRequest request, ServerHttpResponse response).",
                    "Request not exist authentication information exception."
            );
        } else {
            final AuthCache.Token aToken = authCache.getAccessToken(accessToken);
            final AuthCache.Token rToken = authCache.getRefreshToken(refreshToken);
            if (aToken == null || rToken == null) {
                throw new AuthException(
                        this.getClass(),
                        "fun refresh(ServerHttpRequest request, ServerHttpResponse response).",
                        "Request authentication information has exception."
                );
            } else {
                if (aToken.getAccessToken().equals(rToken.getAccessToken())
                        && aToken.getRefreshToken().equals(rToken.getRefreshToken())) {
                    final String user = authCache.getUser(aToken.getUid());
                    if (user == null) {
                        throw new AuthException(
                                this.getClass(),
                                "fun refresh(ServerHttpRequest request, ServerHttpResponse response).",
                                "Request authentication information has exception."
                        );
                    } else {
                        if (authCache.cleanAccessToken(aToken.getAccessToken()) == null) {
                            throw new CacheException(this.getClass(),
                                    "fun refresh(ServerHttpRequest request, ServerHttpResponse response).",
                                    "Request clean token data exception."
                            );
                        }
                        return ResultContext.build(authorization(aToken.getUid(), user, response));
                    }
                } else {
                    throw new AuthException(
                            this.getClass(),
                            "fun refresh(ServerHttpRequest request, ServerHttpResponse response).",
                            "Request authentication information has exception."
                    );
                }
            }
        }
    }

    @SuppressWarnings("ALL")
    @DeleteMapping("/logout")
    public ResultContext logout(HttpServletRequest request, HttpServletResponse response) {
        final String accessToken = getAccessToken();
        if (accessToken == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun logout(ServerHttpRequest request, ServerHttpResponse response).",
                    "Request not exist authentication information exception."
            );
        } else {
            final AuthCache.Token token = authCache.getAccessToken(accessToken);
            if (token == null) {
                throw new AuthException(
                        this.getClass(),
                        "fun logout(ServerHttpRequest request, ServerHttpResponse response).",
                        "Request authentication information has exception."
                );
            } else {
                if (authCache.cleanAccessToken(token.getAccessToken()) == null) {
                    throw new CacheException(this.getClass(),
                            "fun logout(ServerHttpRequest request, ServerHttpResponse response).",
                            "Request clean token data exception."
                    );
                } else {
                    return ResultContext.build();
                }
            }
        }
    }

    @SuppressWarnings("ALL")
    protected Map<String, Object> authorization(String id, String info, HttpServletResponse response) {
        /*
        // JWT
        final Date date = Date.from(LocalDateTime.now().plusSeconds(3600L).atZone(ZoneId.systemDefault()).toInstant());
        final String accessToken = JWT.create().withAudience(String.valueOf(id)).withExpiresAt(date)
                .withSubject(info).sign(Algorithm.HMAC256(authJsonWebTokenCipher.getAccessTokenSecret()));
        final String refreshToken = JWT.create().withAudience(String.valueOf(id)).withExpiresAt(date)
                .withSubject(info).sign(Algorithm.HMAC256(authJsonWebTokenCipher.getRefreshTokenSecret()));
         */

        final String accessToken = GeneratorUtil.uuid() + GeneratorUtil.random();
        final String refreshToken = GeneratorUtil.uuid() + GeneratorUtil.random();
        authCache.set(id, "PC", accessToken, refreshToken, info);

        /*
        // COOKIES
        final Cookie accessTokenCookie = new Cookie("P6E_ACCESS_TOKEN", accessToken) {{
            setPath("/");
            setMaxAge(3600);
            setHttpOnly(true);
        }};
        final Cookie refreshTokenCookie = new Cookie("P6E_REFRESH_TOKEN", refreshToken) {{
            setPath("/");
            setMaxAge(3600);
            setHttpOnly(true);
        }};
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ResultContext.build();
         */
        return new HashMap<>() {{
            put("accessToken", accessToken);
            put("refreshToken", refreshToken);
            put("tokenType", AUTH_HEADER_TOKEN_TYPE);
            put("expire", AuthCache.EXPIRATION_TIME);
        }};
    }
}
