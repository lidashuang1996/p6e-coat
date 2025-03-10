package club.p6e.coat.common.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;

/**
 * Base Web Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BaseWebController extends BaseController {

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
        throw new RuntimeException(BaseWebController.class + " -> getRequest() HttpServletRequest error!");
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
        throw new RuntimeException(BaseWebController.class + " -> getResponse() HttpServletResponse error!");
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

    /**
     * 获取请求头部的信息
     *
     * @return 请求头部的信息
     */
    public static String getHeader(String name) {
        if (name != null) {
            final HttpServletRequest request = getRequest();
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
        final String requestHeaderContent = getHeader(AUTH_HEADER);
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
    public static Cookie getCookie(String name) {
        final HttpServletRequest request = getRequest();
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (name.equalsIgnoreCase(cookie.getName())) {
                    return cookie;
                }
            }
        }
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
