package club.p6e.coat.auth.certificate;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpCertificate
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class HttpCertificate {

    /**
     * 时间格式化对象
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 默认的实现
     */
    private static Specification SPECIFICATION = new Achieve();

    /**
     * 重写默认的实现
     */
    public static void set(Specification specification) {
        SPECIFICATION = specification;
    }

    /**
     * 获取实现对象
     *
     * @return 实现对象
     */
    public static Specification get() {
        return SPECIFICATION;
    }

    /**
     * 设置认证头名称
     *
     * @param header 认证头名称
     */
    public static void setAuthHeader(String header) {
        SPECIFICATION.setAuthHeader(header);
    }

    /**
     * 读取认证头名称
     *
     * @return 认证头名称
     */
    public static String getAuthHeader() {
        return SPECIFICATION.getAuthHeader();
    }

    /**
     * 设置用户信息的头部名称
     *
     * @param name 用户信息的头部名称
     */
    public static void setUserInfoHeaderName(String name) {
        SPECIFICATION.setUserInfoHeaderName(name);
    }

    /**
     * 获取用户信息的头名称
     *
     * @return 用户信息的头名称
     */
    public static String getUserInfoHeaderName() {
        return SPECIFICATION.getUserInfoHeaderName();
    }

    /**
     * 设置认证头类型
     *
     * @param type 认证头类型
     */
    public static void setAuthHeaderTokenType(String type) {
        SPECIFICATION.setAuthHeaderTokenType(type);
    }

    /**
     * 获取认证头类型
     *
     * @return 认证头类型
     */
    public static String getAuthHeaderTokenType() {
        return SPECIFICATION.getAuthHeaderTokenType();
    }

    /**
     * 设置认证头必须的前缀
     *
     * @param prefix 认证头必须的前缀
     */
    public static void setAuthHeaderTokenPrefix(String prefix) {
        SPECIFICATION.setAuthHeaderTokenPrefix(prefix);
    }

    /**
     * 设置 ACCESS TOKEN 请求参数名称
     *
     * @param params 请求参数名称
     */
    public static void setTokenParams(List<String> params) {
        SPECIFICATION.setTokenParams(params);
    }

    /**
     * 设置 AUTH COOKIE ACCESS TOKEN 名称
     *
     * @param name AUTH COOKIE ACCESS TOKEN 名称
     */
    public static void setAuthCookieAccessTokenName(String name) {
        SPECIFICATION.setAuthCookieAccessTokenName(name);
    }

    /**
     * 设置  AUTH COOKIE REFRESH TOKEN 名称
     *
     * @param name AUTH COOKIE REFRESH TOKEN 名称
     */
    public static void setAuthCookieRefreshTokenName(String name) {
        SPECIFICATION.setAuthCookieRefreshTokenName(name);
    }

    /**
     * 从请求头中获取令牌
     *
     * @param request 请求对象
     * @return 结果值
     */
    public static String getHeaderToken(ServerHttpRequest request) {
        return SPECIFICATION.getHeaderToken(request);
    }

    /**
     * 从请求参数中获取令牌
     *
     * @param request 请求对象
     * @return 结果值
     */
    public static String getQueryParamToken(ServerHttpRequest request) {
        return SPECIFICATION.getQueryParamToken(request);
    }

    /**
     * 从请求 cookies 中获取令牌
     *
     * @param request 请求对象
     * @return 结果值
     */
    public static Mono<String> getHttpCookieToken(ServerHttpRequest request) {
        return SPECIFICATION.getHttpCookieToken(request);
    }

    /**
     * 从请求头或者参数中获取令牌
     *
     * @param request 请求对象
     * @return 结果值
     */
    public static Mono<String> getHttpLocalStorageToken(ServerHttpRequest request) {
        return SPECIFICATION.getHttpLocalStorageToken(request);
    }

    /**
     * 写入 cookie 令牌
     *
     * @param response     返回对象
     * @param accessToken  认证令牌
     * @param refreshToken 刷新令牌
     * @return 结果值
     */
    public static Mono<Object> setHttpCookieToken(
            ServerHttpResponse response, String accessToken, String refreshToken) {
        return setHttpCookieToken(response, accessToken, refreshToken, LocalDateTime.now().format(DATE_TIME_FORMATTER));
    }

    /**
     * 写入 cookie 令牌
     *
     * @param response     返回对象
     * @param accessToken  认证令牌
     * @param refreshToken 刷新令牌
     * @param result       结果对象
     * @return 结果值
     */
    public static Mono<Object> setHttpCookieToken(
            ServerHttpResponse response, String accessToken, String refreshToken, Object result) {
        return SPECIFICATION.setHttpCookieToken(response, accessToken, refreshToken, null, result);
    }

    /**
     * 写入 cookie 令牌
     *
     * @param response     返回对象
     * @param accessToken  认证令牌
     * @param refreshToken 刷新令牌
     * @param result       结果对象
     * @return 结果值
     */
    public static Mono<Object> setHttpCookieToken(
            ServerHttpResponse response, String accessToken, String refreshToken, long age, Object result) {
        return SPECIFICATION.setHttpCookieToken(response, accessToken, refreshToken, age, result);
    }

    /**
     * 写入本地缓存令牌
     *
     * @param accessToken  认证令牌
     * @param refreshToken 刷新令牌
     * @return 结果值
     */
    public static Mono<Map<String, Object>> setHttpLocalStorageToken(String accessToken, String refreshToken) {
        return SPECIFICATION.setHttpLocalStorageToken(accessToken, refreshToken, null, null);
    }

    /**
     * 写入本地缓存令牌
     *
     * @param accessToken  认证令牌
     * @param refreshToken 刷新令牌
     * @param data         数据对象
     * @return 结果值
     */
    public static Mono<Map<String, Object>> setHttpLocalStorageToken(
            String accessToken, String refreshToken, Map<String, Object> data) {
        return SPECIFICATION.setHttpLocalStorageToken(accessToken, refreshToken, null, data);
    }

    /**
     * 写入本地缓存令牌
     *
     * @param accessToken  认证令牌
     * @param refreshToken 刷新令牌
     * @param data         数据对象
     * @return 结果值
     */
    public static Mono<Map<String, Object>> setHttpLocalStorageToken(
            String accessToken, String refreshToken, Long expire, Map<String, Object> data) {
        return SPECIFICATION.setHttpLocalStorageToken(accessToken, refreshToken, expire, data);
    }

    /**
     * 清除本地缓存令牌
     *
     * @param response 返回对象
     * @return 结果值
     */
    public static Mono<Void> cleanHttpCookieToken(ServerHttpResponse response) {
        return SPECIFICATION.cleanHttpCookieToken(response);
    }

    /**
     * JWT 加密
     *
     * @param uid     用户编号
     * @param content 用户信息
     * @param secret  密钥
     * @return 令牌
     */
    public static String jwtEncryption(String uid, String content, String secret) {
        return SPECIFICATION.jwtEncryption(uid, content, secret);
    }

    /**
     * JWT 解密
     *
     * @param token  令牌
     * @param secret 密钥
     * @return 解密内容
     */
    public static String jwtDecryption(String token, String secret) {
        return SPECIFICATION.jwtDecryption(token, secret);
    }

    /**
     * 接口定义
     */
    public interface Specification {

        /**
         * 设置认证头名称
         *
         * @param auth 认证头名称
         */
        void setAuthHeader(String auth);

        /**
         * 读取认证头名称
         */
        String getAuthHeader();

        /**
         * 设置用户信息的头部名称
         *
         * @param name 用户信息的头部名称
         */
        void setUserInfoHeaderName(String name);

        /**
         * 读取用户信息的头部名称
         */
        String getUserInfoHeaderName();

        /**
         * 设置认证头类型
         *
         * @param type 认证头类型
         */
        void setAuthHeaderTokenType(String type);

        /**
         * 读取认证头类型
         */
        String getAuthHeaderTokenType();

        /**
         * 设置认证头必须的前缀
         *
         * @param prefix 认证头必须的前缀
         */
        void setAuthHeaderTokenPrefix(String prefix);

        /**
         * 设置 ACCESS TOKEN 请求参数名称
         *
         * @param params 请求参数名称
         */
        void setTokenParams(List<String> params);

        /**
         * 设置 AUTH COOKIE ACCESS TOKEN 名称
         *
         * @param name AUTH COOKIE ACCESS TOKEN 名称
         */
        void setAuthCookieAccessTokenName(String name);

        /**
         * 设置  AUTH COOKIE REFRESH TOKEN 名称
         *
         * @param name AUTH COOKIE REFRESH TOKEN 名称
         */
        void setAuthCookieRefreshTokenName(String name);

        /**
         * 设置过期时间
         *
         * @param time 过期时间
         */
        void setExpirationTime(long time);

        /**
         * 从请求头中获取令牌
         *
         * @param request 请求对象
         * @return 结果值
         */
        String getHeaderToken(ServerHttpRequest request);

        /**
         * 从请求参数中获取令牌
         *
         * @param request 请求对象
         * @return 结果值
         */
        String getQueryParamToken(ServerHttpRequest request);

        /**
         * 从请求 cookies 中获取令牌
         *
         * @param request 请求对象
         * @return 结果值
         */
        Mono<String> getHttpCookieToken(ServerHttpRequest request);

        /**
         * 从请求头或者参数中获取令牌
         *
         * @param request 请求对象
         * @return 结果值
         */
        public Mono<String> getHttpLocalStorageToken(ServerHttpRequest request);

        /**
         * 写入 cookie 令牌
         *
         * @param response     返回对象
         * @param accessToken  认证令牌
         * @param refreshToken 刷新令牌
         * @param expire       过期时间
         * @param result       结果对象
         * @return 结果值
         */
        Mono<Object> setHttpCookieToken(ServerHttpResponse response, String accessToken, String refreshToken, Long expire, Object result);

        /**
         * 写入本地缓存令牌
         *
         * @param accessToken  认证令牌
         * @param refreshToken 刷新令牌
         * @param expire       过期时间
         * @param data         数据对象
         * @return 结果值
         */
        Mono<Map<String, Object>> setHttpLocalStorageToken(String accessToken, String refreshToken, Long expire, Map<String, Object> data);

        /**
         * 清除本地缓存令牌
         *
         * @param response 返回对象
         * @return 结果值
         */
        Mono<Void> cleanHttpCookieToken(ServerHttpResponse response);

        /**
         * JWT 加密
         *
         * @param uid     用户编号
         * @param content 用户信息
         * @param secret  密钥
         * @return 令牌
         */
        String jwtEncryption(String uid, String content, String secret);

        /**
         * JWT 解密
         *
         * @param token  令牌
         * @param secret 密钥
         * @return 解密内容
         */
        String jwtDecryption(String token, String secret);
    }

    /**
     * 默认的实现类
     */
    public static class Achieve implements Specification {

        /**
         * 认证头名称
         */
        private String AUTH_HEADER = "Authorization";

        /**
         * 认证头类型
         */
        private String AUTH_HEADER_TOKEN_TYPE = "Bearer";

        /**
         * 用户信息的头部名称
         */
        private String USER_INFO_HEADER_NAME = "P6e-User-Info";

        /**
         * 认证头必须的前缀
         */
        private String AUTH_HEADER_TOKEN_PREFIX = AUTH_HEADER_TOKEN_TYPE + " ";

        /**
         * Query 方式 ACCESS TOKEN
         */
        private List<String> ACCESS_TOKEN_PARAM = List.of("accessToken", "access_token");

        /**
         * AUTH COOKIE ACCESS TOKEN 名称
         */
        private String AUTH_COOKIE_ACCESS_TOKEN_NAME = "P6E_AUTH_ACCESS_TOKEN";

        /**
         * AUTH COOKIE REFRESH TOKEN 名称
         */
        private String AUTH_COOKIE_REFRESH_TOKEN_NAME = "P6E_AUTH_REFRESH_TOKEN";

        /**
         * 认证过期时间
         */
        private long EXPIRATION_TIME = 3600 * 3L;

        @Override
        public void setAuthHeader(String auth) {
            AUTH_HEADER = auth;
        }

        @Override
        public String getAuthHeader() {
            return AUTH_HEADER;
        }

        @Override
        public void setUserInfoHeaderName(String name) {
            USER_INFO_HEADER_NAME = name;
        }

        @Override
        public String getUserInfoHeaderName() {
            return USER_INFO_HEADER_NAME;
        }

        @Override
        public void setAuthHeaderTokenType(String type) {
            AUTH_HEADER_TOKEN_TYPE = type;
        }

        @Override
        public String getAuthHeaderTokenType() {
            return AUTH_HEADER_TOKEN_TYPE;
        }

        @Override
        public void setAuthHeaderTokenPrefix(String prefix) {
            AUTH_HEADER_TOKEN_PREFIX = prefix;
        }

        @Override
        public void setTokenParams(List<String> params) {
            ACCESS_TOKEN_PARAM = params;
        }

        @Override
        public void setAuthCookieAccessTokenName(String name) {
            AUTH_COOKIE_ACCESS_TOKEN_NAME = name;
        }

        @Override
        public void setAuthCookieRefreshTokenName(String name) {
            AUTH_COOKIE_REFRESH_TOKEN_NAME = name;
        }

        @Override
        public void setExpirationTime(long time) {
            EXPIRATION_TIME = time;
        }

        @Override
        public String getHeaderToken(ServerHttpRequest request) {
            final List<String> authorizations = request.getHeaders().get(AUTH_HEADER);
            if (authorizations != null && !authorizations.isEmpty()) {
                final String authorization = authorizations.get(0);
                if (StringUtils.hasText(authorization)
                        && authorization.startsWith(AUTH_HEADER_TOKEN_PREFIX)) {
                    return authorization.substring(AUTH_HEADER_TOKEN_PREFIX.length());
                }
            }
            return null;
        }

        @Override
        public String getQueryParamToken(ServerHttpRequest request) {
            final MultiValueMap<String, String> params = request.getQueryParams();
            for (final String key : ACCESS_TOKEN_PARAM) {
                final List<String> values = params.get(key);
                if (values != null && !values.isEmpty()) {
                    return values.get(0);
                }
            }
            return null;
        }

        @Override
        public Mono<String> getHttpCookieToken(ServerHttpRequest request) {
            final MultiValueMap<String, HttpCookie> cookies = request.getCookies();
            for (final String key : cookies.keySet()) {
                if (key.equalsIgnoreCase(AUTH_COOKIE_ACCESS_TOKEN_NAME)) {
                    final List<HttpCookie> values = cookies.get(key);
                    if (values != null && !values.isEmpty()) {
                        return Mono.just(values.get(0).getValue().trim());
                    }
                }
            }
            return Mono.empty();
        }

        @Override
        public Mono<String> getHttpLocalStorageToken(ServerHttpRequest request) {
            String token = getHeaderToken(request);
            if (token == null) {
                token = getQueryParamToken(request);
            }
            return token == null ? Mono.empty() : Mono.just(token);
        }

        @Override
        public Mono<Object> setHttpCookieToken(
                ServerHttpResponse response, String accessToken, String refreshToken, Long expire, Object result) {
            final ResponseCookie accessTokenCookie = ResponseCookie
                    .from(AUTH_COOKIE_ACCESS_TOKEN_NAME, accessToken)
                    .maxAge(Duration.ofSeconds(expire == null ? EXPIRATION_TIME : expire))
                    .httpOnly(true)
                    .path("/")
                    .build();
            final ResponseCookie refreshTokenCookie = ResponseCookie
                    .from(AUTH_COOKIE_REFRESH_TOKEN_NAME, refreshToken)
                    .maxAge(Duration.ofSeconds(expire == null ? EXPIRATION_TIME : expire))
                    .httpOnly(true)
                    .path("/")
                    .build();
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
            return Mono.just(result);
        }

        @Override
        public Mono<Map<String, Object>> setHttpLocalStorageToken(
                String accessToken, String refreshToken, Long expire, Map<String, Object> data) {
            final Map<String, Object> result;
            if (data == null) {
                result = new HashMap<>(4);
            } else {
                result = new HashMap<>(data);
            }
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
            result.put("tokenType", AUTH_HEADER_TOKEN_TYPE);
            result.put("expire", expire == null ? EXPIRATION_TIME : expire);
            return Mono.just(result);
        }

        @Override
        public Mono<Void> cleanHttpCookieToken(ServerHttpResponse response) {
            return setHttpCookieToken(response, "", "", 0L, "")
                    .flatMap(r -> Mono.empty());
        }

        @Override
        public String jwtEncryption(String uid, String content, String secret) {
            final Date date = Date.from(LocalDateTime.now()
                    .plusSeconds(EXPIRATION_TIME)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            );
            return JWT
                    .create()
                    .withAudience(uid)
                    .withExpiresAt(date)
                    .withSubject(content)
                    .sign(Algorithm.HMAC256(secret));
        }

        @Override
        public String jwtDecryption(String token, String secret) {
            try {
                return JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getSubject();
            } catch (Exception e) {
                return null;
            }
        }
    }

}
