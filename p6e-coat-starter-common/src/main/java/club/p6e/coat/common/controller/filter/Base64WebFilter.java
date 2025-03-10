package club.p6e.coat.common.controller.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * BASE64 过滤器
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class Base64WebFilter implements Filter {

    /**
     * 注入日志系统
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Base64WebFilter.class);

    /**
     * 初始化过滤器时候的方法回调
     *
     * @param config 过滤的配置对象
     */
    @Override
    public void init(FilterConfig config) {
        LOGGER.info("FILTER [ {} ] INIT COMPLETE ...", this.getClass());
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
        chain.doFilter(new CustomHttpServletRequest((HttpServletRequest) servletRequest), servletResponse);
    }

    /**
     * 摧毁过滤器的时候的回调方法
     */
    @Override
    public void destroy() {
        LOGGER.info("FILTER [ {} ] DESTROY COMPLETE !!", this.getClass());
    }


    /**
     * Custom Http Servlet Request
     */
    private static class CustomHttpServletRequest extends HttpServletRequestWrapper {

        private final Map<String, String> headers = new HashMap<>();

        /**
         * 构造方法初始化
         *
         * @param request HttpServletRequest 对象
         */
        public CustomHttpServletRequest(HttpServletRequest request) {
            super(request);
            final Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                final String name = enumeration.nextElement();
                if (name != null && name.toLowerCase().startsWith("p6e-")) {
                    addHeader(name.toLowerCase(), new String(Base64.getDecoder().decode(request.getHeader(name)), StandardCharsets.UTF_8));
                } else {
                    addHeader(name.toLowerCase(), request.getHeader(name));
                }
            }
        }

        @Override
        public String getHeader(String name) {
            return headers.get(name.toLowerCase());
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(headers.keySet());
        }

        public void addHeader(String name, String value) {
            this.headers.put(name, value);
        }

    }

}
