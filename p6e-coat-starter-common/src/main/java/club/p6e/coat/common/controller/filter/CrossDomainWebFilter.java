package club.p6e.coat.common.controller.filter;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.controller.BaseWebController;
import club.p6e.coat.common.utils.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 浏览器跨域处理过滤器
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class CrossDomainWebFilter implements Filter {

    /**
     * 分隔符号
     */
    private static final String ACCESS_CONTROL_DELIMITER = ",";

    /**
     * 通用内容
     */
    private static final String CROSS_DOMAIN_HEADER_GENERAL_CONTENT = "*";

    /**
     * 跨域配置 ACCESS_CONTROL_MAX_AGE
     */
    private static final long ACCESS_CONTROL_MAX_AGE = 3600L;

    /**
     * 跨域配置 ACCESS_CONTROL_ALLOW_ORIGIN
     */
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "*";

    /**
     * 跨域配置 ACCESS_CONTROL_ALLOW_ORIGIN
     */
    private static final boolean ACCESS_CONTROL_ALLOW_CREDENTIALS = true;

    /**
     * 跨域配置 ACCESS_CONTROL_ALLOW_HEADERS
     */
    private static final String[] ACCESS_CONTROL_ALLOW_HEADERS = new String[]{
            "Accept",
            "Host",
            "Origin",
            "Referer",
            "User-Agent",
            "Content-Type",
            "Authorization"
    };

    /**
     * 跨域配置 ACCESS_CONTROL_ALLOW_METHODS
     */
    private static final HttpMethod[] ACCESS_CONTROL_ALLOW_METHODS = new HttpMethod[]{
            HttpMethod.GET,
            HttpMethod.POST,
            HttpMethod.PUT,
            HttpMethod.DELETE,
            HttpMethod.OPTIONS,
    };

    /**
     * 错误结果对象
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "Unmatched origin cross domain requests");

    /**
     * 错误结果文本内容
     */
    private static final String ERROR_RESULT_CONTENT = JsonUtil.toJson(ERROR_RESULT);

    /**
     * 注入日志系统
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CrossDomainWebFilter.class);

    /**
     * 配置对象
     */
    private final Properties properties;

    /**
     * 构造方法初始化
     *
     * @param properties 配置对象
     */
    public CrossDomainWebFilter(Properties properties) {
        this.properties = properties;
    }

    /**
     * 初始化过滤器时候的方法回调
     *
     * @param filterConfig 过滤的配置对象
     */
    @Override
    public void init(FilterConfig filterConfig) {
        LOGGER.info("filter [ " + this.getClass() + " ] init complete ...");
    }

    /**
     * 过滤器内容的处理方法
     *
     * @param servletRequest  服务请求头对象
     * @param servletResponse 服务返回头对象
     * @param chain           内部代理对象
     * @throws IOException      请求头和返回头处理时候可能出现的 IO 异常
     * @throws ServletException 服务处理时候可能出现的异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final Properties.CrossDomain crossDomain = properties.getCrossDomain();
        if (crossDomain != null && crossDomain.isEnable()) {
            final HttpServletRequest request = (HttpServletRequest) servletRequest;
            final HttpServletResponse response = (HttpServletResponse) servletResponse;
            String origin = BaseWebController.getHeader(HttpHeaders.ORIGIN);
            if (validationOrigin(origin, List.of(crossDomain.getWhiteList()))) {
                origin = origin == null ? ACCESS_CONTROL_ALLOW_ORIGIN : origin;
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, String.valueOf(ACCESS_CONTROL_MAX_AGE));
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, String.valueOf(ACCESS_CONTROL_ALLOW_CREDENTIALS));
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(ACCESS_CONTROL_DELIMITER, ACCESS_CONTROL_ALLOW_HEADERS));
                response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        Arrays.stream(ACCESS_CONTROL_ALLOW_METHODS).map(HttpMethod::name).collect(Collectors.joining(ACCESS_CONTROL_DELIMITER)));
                if (HttpMethod.OPTIONS.matches(request.getMethod().toUpperCase())) {
                    response.setStatus(HttpStatus.OK.value());
                    // response.setStatus(HttpStatus.NO_CONTENT.value());
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
     * 摧毁过滤器的时候的回调方法
     */
    @Override
    public void destroy() {
        LOGGER.info("filter [ " + this.getClass() + " ] destroy complete !!");
    }

    /**
     * 验证 origin 是否合法
     *
     * @param origin    origin
     * @param whiteList whiteList
     * @return true or false
     */
    public boolean validationOrigin(String origin, List<String> whiteList) {
        if (whiteList != null && !whiteList.isEmpty()) {
            for (final String item : whiteList) {
                if (item.equalsIgnoreCase(CROSS_DOMAIN_HEADER_GENERAL_CONTENT) || origin.startsWith(item)) {
                    return true;
                }
            }
        }
        return false;
    }

}
