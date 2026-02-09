package club.p6e.coat.permission.filter;

import club.p6e.coat.common.exception.PermissionException;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.permission.PermissionDetails;
import club.p6e.coat.permission.validator.PermissionValidator;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.*;

/**
 * Blocking Permission Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingPermissionFilter implements Filter, Ordered {

    /**
     * Permission Header
     * Save The Request Header Of The Permission Information Used In The Current Request
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String PERMISSION_HEADER = "P6e-Permission";

    /**
     * User Permission Header
     * Request Header For Saving User Owned Permission Group
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
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
    public BlockingPermissionFilter(PermissionValidator validator) {
        this.validator = validator;
    }

    @Override
    public int getOrder() {
        return 20000;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final PermissionDetails details = validate(request);
        if (details == null) {
            throw new PermissionException(
                    this.getClass(),
                    "fun doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)",
                    "request permission exception"
            );
        } else {
            filterChain.doFilter(new MyHttpServletRequestWrapper(request, details), servletResponse);
        }
    }

    /**
     * Validate Request Permission
     *
     * @param request Http Servlet Request Object
     * @return Permission Details Object
     */
    public PermissionDetails validate(HttpServletRequest request) {
        final List<String> permissions = new ArrayList<>();
        final String method = request.getMethod().toUpperCase();
        final String user = request.getHeader(USER_PERMISSION_HEADER);
        final String path = request.getContextPath() + request.getServletPath();
        if (user != null) {
            final List<String> data = JsonUtil.fromJsonToList(user, String.class);
            if (data != null) {
                permissions.addAll(data);
            }
        }
        return this.validator.execute(path, method, permissions);
    }

    /**
     * My Http Servlet Request Wrapper
     */
    private static class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

        /**
         * Header Data Object
         */
        private final Map<String, String> headers = new HashMap<>();

        /**
         * @param request Http Servlet Request Object
         * @param details Permission Details Object
         */
        public MyHttpServletRequestWrapper(HttpServletRequest request, PermissionDetails details) {
            super(request);
            this.headers.put(PERMISSION_HEADER, JsonUtil.toJson(details));
        }

        @Override
        public String getHeader(String name) {
            final String value = this.headers.get(name);
            if (value == null) {
                return super.getHeader(name);
            } else {
                return value;
            }
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            final List<String> values = new ArrayList<>();
            if (this.headers.containsKey(name)) {
                values.add(this.headers.get(name));
            }
            final Enumeration<String> originalHeaders = super.getHeaders(name);
            if (originalHeaders != null) {
                while (originalHeaders.hasMoreElements()) {
                    values.add(originalHeaders.nextElement());
                }
            }
            return Collections.enumeration(values);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            final Set<String> names = new HashSet<>(this.headers.keySet());
            final Enumeration<String> originalNames = super.getHeaderNames();
            if (originalNames != null) {
                while (originalNames.hasMoreElements()) {
                    names.add(originalNames.nextElement());
                }
            }
            return Collections.enumeration(names);
        }

    }

}
