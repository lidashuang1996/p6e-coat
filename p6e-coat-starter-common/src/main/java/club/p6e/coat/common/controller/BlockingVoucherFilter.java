package club.p6e.coat.common.controller;

import club.p6e.coat.common.Properties;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Blocking Voucher Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingVoucherFilter implements Filter {

    /**
     * Voucher Header
     */
    private static final String VOUCHER_HEADER = "P6e-Voucher";

    /**
     * Error Result Object
     */
    private static final ResultContext ERROR_RESULT =
            ResultContext.build(401, "Unauthorized", "invalid voucher access");

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
    public BlockingVoucherFilter(Properties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        final Properties.Security security = properties.getSecurity();
        if (security != null && security.isEnable()) {
            final String voucher = BlockingWebUtil.getHeader(VOUCHER_HEADER);
            if (voucher != null) {
                for (final String item : security.getVouchers()) {
                    if (item.equals(voucher)) {
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

}
