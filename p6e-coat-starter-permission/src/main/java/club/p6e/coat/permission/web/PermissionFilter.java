package club.p6e.coat.permission.web;

import club.p6e.coat.common.error.PermissionException;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.validator.PermissionValidator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Permission Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnClass(name = "org.springframework.web.servlet.package-info")
public class PermissionFilter implements Filter, Ordered {

    @SuppressWarnings("ALL")
    private static final String PROJECT_HEADER = "P6e-Project";

    @SuppressWarnings("ALL")
    private static final String PERMISSION_HEADER = "P6e-Permission";

    @SuppressWarnings("ALL")
    private static final String USER_PERMISSION_HEADER = "P6e-User-Permission";

    /**
     * Permission Validator Object
     */
    private final PermissionValidator validator;

    /**
     * Constructor Initialization
     *
     * @param validator Permission Validator Object
     */
    public PermissionFilter(PermissionValidator validator) {
        this.validator = validator;
    }

    @Override
    public int getOrder() {
        return 20000;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        final List<String> permissions = new ArrayList<>();
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final String path = request.getServletPath();
        final String method = request.getMethod().toUpperCase();
        final String project = request.getHeader(PROJECT_HEADER);
        final Enumeration<String> list = request.getHeaders(USER_PERMISSION_HEADER);
        if (list != null) {
            while (list.hasMoreElements()) {
                final List<String> data = JsonUtil.fromJsonToList(list.nextElement(), String.class);
                if (data != null) {
                    permissions.addAll(data);
                }
            }
        }
        final PermissionDetails details;
        if (project == null || project.isEmpty()) {
            details = validator.execute(path, method, permissions);
        } else {
            details = validator.execute(path, method, project, permissions);
        }
        if (details == null) {
            throw new PermissionException(
                    this.getClass(),
                    "fun doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain).",
                    "request permission exception."
            );
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

}
