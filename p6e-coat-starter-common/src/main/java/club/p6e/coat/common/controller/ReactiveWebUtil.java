package club.p6e.coat.common.controller;

import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Web Flux Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveWebUtil extends BaseController {

    /**
     * 获取 ACCESS TOKEN 内容
     *
     * @param exchange ServerWebExchange 对象
     * @return ACCESS TOKEN 内容
     */
    public static String getAccessToken(ServerWebExchange exchange) {
        return getAccessToken(exchange.getRequest());
    }

    /**
     * 获取 ACCESS TOKEN 内容
     *
     * @param request ServerHttpRequest 对象
     * @return ACCESS TOKEN 内容
     */
    public static String getAccessToken(ServerHttpRequest request) {
        String accessToken = getParam(request, ACCESS_TOKEN_PARAM1, ACCESS_TOKEN_PARAM2, ACCESS_TOKEN_PARAM3);
        if (accessToken == null) {
            accessToken = getHeaderToken(request);
        }
        if (accessToken == null) {
            accessToken = getCookieAccessToken(request);
        }
        return accessToken;
    }

    /**
     * 获取 REFRESH TOKEN 内容
     *
     * @param exchange ServerWebExchange 对象
     * @return REFRESH TOKEN 内容
     */
    public static String getRefreshToken(ServerWebExchange exchange) {
        return getRefreshToken(exchange.getRequest());
    }

    /**
     * 获取 REFRESH TOKEN 内容
     *
     * @param request ServerHttpRequest 对象
     * @return REFRESH TOKEN 内容
     */
    public static String getRefreshToken(ServerHttpRequest request) {
        String refreshToken = getParam(request, REFRESH_TOKEN_PARAM1, REFRESH_TOKEN_PARAM2, REFRESH_TOKEN_PARAM3);
        if (refreshToken == null) {
            refreshToken = getCookieRefreshToken(request);
        }
        return refreshToken;
    }

    /**
     * 通过多个参数名称去获取 URL 路径上面的参数值
     *
     * @param params 参数名称
     * @return 读取的参数名称对应的值
     */
    public static String getParam(ServerHttpRequest request, String... params) {
        String value;
        for (String param : params) {
            value = request.getQueryParams().getFirst(param);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取请求头部的信息
     *
     * @return 请求头部的信息
     */
    public static String getHeader(ServerHttpRequest request, String name) {
        if (name != null) {
            for (final String key : request.getHeaders().keySet()) {
                if (name.equalsIgnoreCase(key)) {
                    return request.getHeaders().getFirst(key);
                }
            }
        }
        return null;
    }

    /**
     * 获取请求头部存在的 TOKEN 信息
     *
     * @return 头部的 TOKEN 信息
     */
    public static String getHeaderToken(ServerHttpRequest request) {
        final String requestHeaderContent = getHeader(request, AUTH_HEADER);
        if (requestHeaderContent != null
                && requestHeaderContent.startsWith(AUTH_HEADER_TOKEN_PREFIX)) {
            return requestHeaderContent.substring(AUTH_HEADER_TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 获取 COOKIE 的信息
     *
     * @return COOKIE 的信息
     */
    public static List<HttpCookie> getCookie(ServerHttpRequest request, String name) {
        return request.getCookies().get(name);
    }

    /**
     * 获取 COOKIE 的 TOKEN 信息
     *
     * @return COOKIE 的 ACCESS TOKEN 信息
     */
    public static String getCookieAccessToken(ServerHttpRequest request) {
        final List<HttpCookie> cookies = getCookie(request, COOKIE_ACCESS_TOKEN);
        if (cookies != null && !cookies.isEmpty()) {
            return cookies.get(0).getValue();
        }
        return null;
    }

    /**
     * 获取 COOKIE 的 TOKEN 信息
     *
     * @return COOKIE 的 REFRESH TOKEN 信息
     */
    public static String getCookieRefreshToken(ServerHttpRequest request) {
        return null;
    }

    public static Map<String, String> getParams(ServerHttpRequest request) {
        final Map<String, String> result = new HashMap<>();
        final MultiValueMap<String, String> params = request.getQueryParams();
        if (params != null) {
            for (final String key : params.keySet()) {
                result.put(key, params.getFirst(key));
            }
        }
        return result;
    }
}
