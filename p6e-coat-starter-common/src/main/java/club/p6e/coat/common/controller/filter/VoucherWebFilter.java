package club.p6e.coat.common.controller.filter;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.controller.BaseWebController;
import club.p6e.coat.common.utils.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 凭证认证过滤器
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class VoucherWebFilter implements Filter {

    /**
     * 凭证的头部
     */
    private static final String VOUCHER_HEADER = "P6e-Voucher";

    /**
     * 注入日志系统
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VoucherWebFilter.class);

    /**
     * 错误结果对象
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "Access with invalid voucher");

    /**
     * 错误结果文本内容
     */
    private static final String ERROR_RESULT_CONTENT = JsonUtil.toJson(ERROR_RESULT);

    /**
     * 配置对象
     */
    private final Properties properties;

    /**
     * 构造方法初始化
     *
     * @param properties 配置对象
     */
    public VoucherWebFilter(Properties properties) {
        this.properties = properties;
    }

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
        final Properties.Security security = properties.getSecurity();
        if (security != null && security.isEnable()) {
            final String voucher = BaseWebController.getHeader(VOUCHER_HEADER);
            if (voucher != null) {
                for (final String item : security.getVouchers()) {
                    if (item.equalsIgnoreCase(voucher)) {
                        chain.doFilter(servletRequest, servletResponse);
                        return;
                    }
                }
            }
            final HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            try (final OutputStream output = response.getOutputStream()) {
                output.write(ERROR_RESULT_CONTENT.getBytes(StandardCharsets.UTF_8));
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
        LOGGER.info("FILTER [ {} ] DESTROY COMPLETE !!", this.getClass());
    }

}
