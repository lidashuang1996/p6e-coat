package club.p6e.coat.common.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * Web Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class WebUtil {

    /**
     * Definition
     */
    public interface Definition {

        /**
         * Get Token
         *
         * @param request Server Http Request Object
         * @return Token
         */
        String getToken(org.springframework.http.server.reactive.ServerHttpRequest request);

        /**
         * Get Token
         *
         * @param request Http Servlet Request Object
         * @return Token
         */
        String getToken(jakarta.servlet.http.HttpServletRequest request);

        /**
         * Get Param
         *
         * @param request Server Http Request Object
         * @param params  Param Array Object
         * @return param
         */
        String getParam(org.springframework.http.server.reactive.ServerHttpRequest request, String... params);

        /**
         * Get Param
         *
         * @param request Http Servlet Request Object
         * @param params  Param Array Object
         * @return param
         */
        String getParam(jakarta.servlet.http.HttpServletRequest request, String... params);

        /**
         * Get Header
         *
         * @param request Server Http Request Object
         * @param headers Header Array Object
         * @return header
         */
        String getHeader(org.springframework.http.server.reactive.ServerHttpRequest request, String... headers);

        /**
         * Get Header
         *
         * @param request Http Servlet Request Object
         * @param headers Header Array Object
         * @return header
         */
        String getHeader(jakarta.servlet.http.HttpServletRequest request, String... headers);

        /**
         * Get Headers Value
         *
         * @param request Server Http Request Object
         * @param filter  Filter Object
         * @return headers
         */
        List<String> getHeader(org.springframework.http.server.reactive.ServerHttpRequest request, Function<String, Boolean> filter);

        /**
         * Get Headers Value
         *
         * @param request Http Servlet Request Object
         * @param filter  Filter Object
         * @return headers
         */
        List<String> getHeader(jakarta.servlet.http.HttpServletRequest request, Function<String, Boolean> filter);

        /**
         * Get Cookie
         *
         * @param request Server Http Request Object
         * @param cookies Cookie Array Object
         * @return Http Cookie Object
         */
        org.springframework.http.HttpCookie getCookie(org.springframework.http.server.reactive.ServerHttpRequest request, String... cookies);

        /**
         * Get Cookie
         *
         * @param request Http Servlet Request Object
         * @param cookies Cookie Array Object
         * @return Cookie Object
         */
        jakarta.servlet.http.Cookie getCookie(jakarta.servlet.http.HttpServletRequest request, String... cookies);

        /**
         * Get Url Params
         *
         * @param url Url String
         * @return Url Params Map Object
         */
        Map<String, String> getUrlParams(String url);

        /**
         * Get Request Query Params
         *
         * @param request Server Http Request Object
         * @return Url Params Map Object
         */
        Map<String, String> getRequestQueryParams(org.springframework.http.server.reactive.ServerHttpRequest request);

        /**
         * Get Request Query Params
         *
         * @param request Http Servlet Request Object
         * @return Url Params Map Object
         */
        Map<String, String> getRequestQueryParams(jakarta.servlet.http.HttpServletRequest request);

        /**
         * Merge Url Params
         *
         * @param url    Url String
         * @param params Params Map Object
         * @return Complete Url
         */
        String mergeUrlParams(String url, Map<String, String> params);

    }

    /**
     * Implementation
     */
    public static class Implementation implements Definition {

        /**
         * Token Param
         */
        private static final String TOKEN_PARAM = "token";

        /**
         * Auth Header
         */
        private static final String AUTH_HEADER = "Authorization";

        /**
         * Auth Header Type
         */
        private static final String AUTH_HEADER_TOKEN_TYPE = "Bearer";

        /**
         * Auth Header Token Prefix
         */
        private static final String AUTH_HEADER_TOKEN_PREFIX = AUTH_HEADER_TOKEN_TYPE + " ";

        /**
         * Get Token
         *
         * @param param  Param
         * @param header Header
         * @return Token
         */
        private String getToken(String param, String header) {
            String token = param;
            if (token == null) {
                token = header;
                if (token != null && token.startsWith(AUTH_HEADER_TOKEN_PREFIX)) {
                    token = token.substring(AUTH_HEADER_TOKEN_PREFIX.length());
                }
            }
            return token;
        }

        @Override
        public String getToken(org.springframework.http.server.reactive.ServerHttpRequest request) {
            return getToken(getParam(request, TOKEN_PARAM), getHeader(request, AUTH_HEADER));
        }

        @Override
        public String getToken(jakarta.servlet.http.HttpServletRequest request) {
            return getToken(getParam(request, TOKEN_PARAM), getHeader(request, AUTH_HEADER));
        }

        @Override
        public String getParam(org.springframework.http.server.reactive.ServerHttpRequest request, String... params) {
            String value = null;
            if (request != null) {
                for (final String param : params) {
                    value = request.getQueryParams().getFirst(param);
                    if (value != null) {
                        break;
                    }
                }
            }
            return value;
        }

        @Override
        public String getParam(jakarta.servlet.http.HttpServletRequest request, String... params) {
            String value = null;
            if (request != null) {
                for (final String param : params) {
                    value = request.getParameter(param);
                    if (value != null) {
                        break;
                    }
                }
            }
            return value;
        }

        @Override
        public String getHeader(org.springframework.http.server.reactive.ServerHttpRequest request, String... headers) {
            String value = null;
            if (request != null) {
                for (final String header : headers) {
                    value = request.getHeaders().getFirst(header);
                    if (value != null) {
                        break;
                    }
                }
            }
            return value;
        }

        @Override
        public String getHeader(jakarta.servlet.http.HttpServletRequest request, String... headers) {
            String value = null;
            if (request != null) {
                for (final String header : headers) {
                    value = request.getHeader(header);
                    if (value != null) {
                        break;
                    }
                }
            }
            return value;
        }

        @Override
        public List<String> getHeader(org.springframework.http.server.reactive.ServerHttpRequest request, Function<String, Boolean> filter) {
            final List<String> result = new ArrayList<>();
            final org.springframework.http.HttpHeaders httpHeaders = request.getHeaders();
            for (final String key : httpHeaders.headerNames()) {
                final String value = httpHeaders.getFirst(key);
                final Boolean bool = filter.apply(key + "=" + value);
                if (bool != null && bool) {
                    result.add(key);
                }
            }
            return result;
        }

        @Override
        public List<String> getHeader(jakarta.servlet.http.HttpServletRequest request, Function<String, Boolean> filter) {
            final List<String> result = new ArrayList<>();
            final Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final String value = request.getHeader(key);
                final Boolean bool = filter.apply(key + "=" + value);
                if (bool != null && bool) {
                    result.add(key);
                }
            }
            return result;
        }

        @Override
        public org.springframework.http.HttpCookie getCookie(org.springframework.http.server.reactive.ServerHttpRequest request, String... cookies) {
            org.springframework.http.HttpCookie value = null;
            if (request != null) {
                final org.springframework.util.MultiValueMap<String, org.springframework.http.HttpCookie> httpCookies = request.getCookies();
                for (final String cookie : cookies) {
                    value = httpCookies.getFirst(cookie);
                    if (value != null) {
                        break;
                    }
                }
            }
            return value;
        }

        @Override
        public jakarta.servlet.http.Cookie getCookie(jakarta.servlet.http.HttpServletRequest request, String... cookies) {
            jakarta.servlet.http.Cookie value = null;
            if (request != null) {
                final jakarta.servlet.http.Cookie[] httpCookies = request.getCookies();
                if (httpCookies != null) {
                    for (final String cookie : cookies) {
                        for (final jakarta.servlet.http.Cookie httpCookie : httpCookies) {
                            if (httpCookie != null && httpCookie.getName().equals(cookie)) {
                                value = httpCookie;
                                break;
                            }
                        }
                    }
                }
            }
            return value;
        }

        @Override
        public Map<String, String> getUrlParams(String url) {
            final Map<String, String> result = new HashMap<>();
            if (url != null && !url.isEmpty()) {
                final String content = url.substring(url.indexOf("?") + 1);
                final String[] params = content.split("&");
                for (final String param : params) {
                    final String[] ps = param.split("=");
                    if (ps.length == 2) {
                        result.put(ps[0], URLDecoder.decode(ps[1], StandardCharsets.UTF_8));
                    }
                }
            }
            return result;
        }

        @Override
        public Map<String, String> getRequestQueryParams(org.springframework.http.server.reactive.ServerHttpRequest request) {
            return getUrlParams(request.getURI().getRawQuery());
        }

        @Override
        public Map<String, String> getRequestQueryParams(jakarta.servlet.http.HttpServletRequest request) {
            return getUrlParams(request.getQueryString());
        }

        @Override
        public String mergeUrlParams(String url, Map<String, String> params) {
            if (params != null) {
                final String content = params.entrySet().stream().map(entry ->
                        entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8)
                ).reduce((a, b) -> a + "&" + b).orElse("");
                return (url.lastIndexOf("?") >= 0 ? "&" : "?") + content;
            }
            return url;
        }

    }

    /**
     * Default Definition Implementation Object
     */
    private static Definition DEFINITION = new Implementation();

    /**
     * Set Definition Implementation Object
     *
     * @param implementation Definition Implementation Object
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * Get Token
     *
     * @param request Server Http Request Object
     * @return Token
     */
    public static String getToken(org.springframework.http.server.reactive.ServerHttpRequest request) {
        return DEFINITION.getToken(request);
    }

    /**
     * Get Token
     *
     * @param request Http Servlet Request Object
     * @return Token
     */
    public static String getToken(jakarta.servlet.http.HttpServletRequest request) {
        return DEFINITION.getToken(request);
    }

    /**
     * Get Param
     *
     * @param request Server Http Request Object
     * @param params  Param Array Object
     * @return param
     */
    public static String getParam(org.springframework.http.server.reactive.ServerHttpRequest request, String... params) {
        return DEFINITION.getParam(request, params);
    }

    /**
     * Get Param
     *
     * @param request Http Servlet Request Object
     * @param params  Param Array Object
     * @return param
     */
    public static String getParam(jakarta.servlet.http.HttpServletRequest request, String... params) {
        return DEFINITION.getParam(request, params);
    }

    /**
     * Get Header
     *
     * @param request Server Http Request Object
     * @param headers Header Array Object
     * @return header
     */
    public static String getHeader(org.springframework.http.server.reactive.ServerHttpRequest request, String... headers) {
        return DEFINITION.getHeader(request, headers);
    }

    /**
     * Get Header
     *
     * @param request Http Servlet Request Object
     * @param headers Header Array Object
     * @return header
     */
    public static String getHeader(jakarta.servlet.http.HttpServletRequest request, String... headers) {
        return DEFINITION.getHeader(request, headers);
    }

    /**
     * Get Headers Value
     *
     * @param request Server Http Request Object
     * @param filter  Filter Object
     * @return headers
     */
    public static List<String> getHeader(org.springframework.http.server.reactive.ServerHttpRequest request, Function<String, Boolean> filter) {
        return DEFINITION.getHeader(request, filter);
    }

    /**
     * Get Headers Value
     *
     * @param request Http Servlet Request Object
     * @param filter  Filter Object
     * @return headers
     */
    public static List<String> getHeader(jakarta.servlet.http.HttpServletRequest request, Function<String, Boolean> filter) {
        return DEFINITION.getHeader(request, filter);
    }

    /**
     * Get Cookie
     *
     * @param request Server Http Request Object
     * @param cookies Cookie Array Object
     * @return Http Cookie Object
     */
    public static org.springframework.http.HttpCookie getCookie(org.springframework.http.server.reactive.ServerHttpRequest request, String... cookies) {
        return DEFINITION.getCookie(request, cookies);
    }

    /**
     * Get Cookie
     *
     * @param request Http Servlet Request Object
     * @param cookies Cookie Array Object
     * @return Cookie Object
     */
    public static jakarta.servlet.http.Cookie getCookie(jakarta.servlet.http.HttpServletRequest request, String... cookies) {
        return DEFINITION.getCookie(request, cookies);
    }

    /**
     * Get Url Params
     *
     * @param url Url String
     * @return Url Params Map Object
     */
    public static Map<String, String> getUrlParams(String url) {
        return DEFINITION.getUrlParams(url);
    }

    /**
     * Get Request Query Params
     *
     * @param request Server Http Request Object
     * @return Url Params Map Object
     */
    public static Map<String, String> getRequestQueryParams(org.springframework.http.server.reactive.ServerHttpRequest request) {
        return DEFINITION.getRequestQueryParams(request);
    }

    /**
     * Get Request Query Params
     *
     * @param request Http Servlet Request Object
     * @return Url Params Map Object
     */
    public static Map<String, String> getRequestQueryParams(jakarta.servlet.http.HttpServletRequest request) {
        return DEFINITION.getRequestQueryParams(request);
    }

    /**
     * Merge Url Params
     *
     * @param url    Url String
     * @param params Params Map Object
     * @return Complete Url
     */
    public static String mergeUrlParams(String url, Map<String, String> params) {
        return DEFINITION.mergeUrlParams(url, params);
    }

}
