package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.WebUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Blocking Cross Domain Filter
 *
 * @author lidashuang
 * @version 1.0
 */
public class BlockingCrossDomainFilter implements Filter {

    /**
     * Error Result Object
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "mismatched origin cross domain request");

    /**
     * Error Result Content Object
     */
    private static final String ERROR_RESULT_CONTENT = JsonUtil.toJson(ERROR_RESULT);

    /**
     * Properties Object
     */
    private final Properties properties;

    /**
     * Constructor Initialization
     *
     * @param properties Properties Object
     */
    public BlockingCrossDomainFilter(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final Properties.CrossDomain crossDomain = properties.getCrossDomain();
        if (crossDomain != null && crossDomain.isEnable()) {
            final HttpServletRequest request = (HttpServletRequest) servletRequest;
            final HttpServletResponse response = (HttpServletResponse) servletResponse;
            final String origin = WebUtil.getHeader(request, HttpHeaders.ORIGIN);
            if (validationOrigin(origin, crossDomain.getWhiteList())) {
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin == null ? "*" : origin);
                response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, getAccessControlMaxAge());
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, getAccessControlAllowMethods());
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, getAccessControlAllowHeaders());
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, getAccessControlAllowCredentials());
                if (HttpMethod.OPTIONS.matches(request.getMethod().toUpperCase())) {
                    response.setStatus(HttpStatus.OK.value());
                } else {
                    chain.doFilter(servletRequest, servletResponse);
                }
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                try (final OutputStream output = response.getOutputStream()) {
                    output.write(ERROR_RESULT_CONTENT.getBytes(StandardCharsets.UTF_8));
                }
            }
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    /**
     * Validation Origin
     *
     * @param origin    Origin String
     * @param whiteList White List Object
     * @return true or false
     */
    @SuppressWarnings("ALL")
    public boolean validationOrigin(String origin, List<String> whiteList) {
        if (origin == null || whiteList == null || whiteList.isEmpty()) {
            return false;
        }
        for (final String item : whiteList) {
            if ("*".equals(item)) {
                return true;
            }
            try {
                final URI originUri = URI.create(origin);
                final URI patternUri = URI.create(item);
                if (originUri.getHost() != null
                        && originUri.getScheme() != null
                        && originUri.getHost().equals(patternUri.getHost())
                        && originUri.getScheme().equals(patternUri.getScheme())
                        && String.valueOf(originUri.getPort()).equals(String.valueOf(patternUri.getPort()))) {
                    return true;
                }
            } catch (Exception _) {
                // ignore exception
            }
        }
        return false;
    }

    /**
     * Access Control Max Age
     *
     * @return Access Control Max Age
     */
    public static String getAccessControlMaxAge() {
        return "3600";
    }

    /**
     * Access Control Allow Credentials
     *
     * @return Access Control Allow Credentials
     */
    public static String getAccessControlAllowCredentials() {
        return "true";
    }

    /**
     * Access Control Allow Methods
     *
     * @return Access Control Allow Methods
     */
    public static String getAccessControlAllowMethods() {
        return Stream.of(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.OPTIONS).map(HttpMethod::name).collect(Collectors.joining(","));
    }

    /**
     * Access Control Allow Headers
     *
     * @return Access Control Allow Headers
     */
    public static String getAccessControlAllowHeaders() {
        return String.join(",", "Accept", "Host", "Origin", "Referer", "User-Agent", "Content-Type", "Authorization", "X-Project", "X-Voucher", "X-Language", "X-Token", "X-Authorization");
    }

}
