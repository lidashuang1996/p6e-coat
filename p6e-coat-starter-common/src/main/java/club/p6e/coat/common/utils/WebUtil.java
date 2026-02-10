package club.p6e.coat.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Web Flux Controller
 *
 * @author lidashuang
 * @version 1.0
 */
public class WebUtil {

    public static String AUTH_HEADER = "Authorization";
    public static String AUTH_HEADER_TOKEN_TYPE = "Bearer";
    public static String AUTH_HEADER_TOKEN_PREFIX = AUTH_HEADER_TOKEN_TYPE + " ";
    public static String ACCESS_TOKEN_PARAM1 = "accessToken";
    public static String ACCESS_TOKEN_PARAM2 = "access_token";
    public static String ACCESS_TOKEN_PARAM3 = "access-token";
    public static String REFRESH_TOKEN_PARAM1 = "refreshToken";
    public static String REFRESH_TOKEN_PARAM2 = "refresh_token";
    public static String REFRESH_TOKEN_PARAM3 = "refresh-token";
    public static String COOKIE_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static String COOKIE_REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment";

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
            for (final String key : request.getHeaders().headerNames()) {
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


    /**
     * 获取 HttpServletRequest 对象
     *
     * @return HttpServletRequest 对象
     */
    public static HttpServletRequest getRequest() {
        final ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            return servletRequestAttributes.getRequest();
        }
        throw new RuntimeException(" -> getRequest() HttpServletRequest error!");
    }

    /**
     * 获取 HttpServletResponse 对象
     *
     * @return HttpServletResponse 对象
     */
    public static HttpServletResponse getResponse() {
        final ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            return servletRequestAttributes.getResponse();
        }
        throw new RuntimeException(" -> getResponse() HttpServletResponse error!");
    }

    /**
     * 获取 ACCESS TOKEN 内容
     *
     * @return ACCESS TOKEN 内容
     */
    public static String getAccessToken() {
        String accessToken = getParam(ACCESS_TOKEN_PARAM1, ACCESS_TOKEN_PARAM2, ACCESS_TOKEN_PARAM3);
        if (accessToken == null) {
            accessToken = getHeaderToken();
        }
        if (accessToken == null) {
            accessToken = getCookieAccessToken();
        }
        return accessToken;
    }

    /**
     * 获取 REFRESH TOKEN 内容
     *
     * @return REFRESH TOKEN 内容
     */
    public static String getRefreshToken() {
        String refreshToken = getParam(REFRESH_TOKEN_PARAM1, REFRESH_TOKEN_PARAM2, REFRESH_TOKEN_PARAM3);
        if (refreshToken == null) {
            refreshToken = getCookieRefreshToken();
        }
        return refreshToken;
    }

    /**
     * 通过多个参数名称去获取 URL 路径上面的参数值
     *
     * @param params 参数名称
     * @return 读取的参数名称对应的值
     */
    public static String getParam(String... params) {
        String value;
        final HttpServletRequest request = getRequest();
        for (String param : params) {
            value = request.getParameter(param);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static String getParam(HttpServletRequest request, String... params) {
        String value;
        for (String param : params) {
            value = request.getParameter(param);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static Map<String, String> getParams(HttpServletRequest request) {
        final Map<String, String> params = new HashMap<>();
        final Enumeration<String> enumeration = request.getParameterNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final String value = request.getParameter(key);
                params.put(key, value);
            }
        }
        return params;
    }

    /**
     * 获取请求头部的信息
     *
     * @return 请求头部的信息
     */
    public static String getHeader(HttpServletRequest request, String name) {
        if (name != null) {
            final Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                final String element = enumeration.nextElement();
                if (name.equalsIgnoreCase(element)) {
                    return request.getHeader(element);
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
    public static String getHeaderToken() {
        return null;
    }

    /**
     * 获取 COOKIE 的信息
     *
     * @return COOKIE 的信息
     */
    public static Cookie getCookie(String name) {
        return null;
    }

    /**
     * 获取 COOKIE 的 ACCESS TOKEN 信息
     *
     * @return COOKIE 的 ACCESS TOKEN 信息
     */
    public static String getCookieAccessToken() {
        final Cookie cookie = getCookie(COOKIE_ACCESS_TOKEN);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    /**
     * 获取 COOKIE 的 REFRESH TOKEN 信息
     *
     * @return COOKIE 的 REFRESH TOKEN 信息
     */
    public static String getCookieRefreshToken() {
        final Cookie cookie = getCookie(COOKIE_REFRESH_TOKEN);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
}
