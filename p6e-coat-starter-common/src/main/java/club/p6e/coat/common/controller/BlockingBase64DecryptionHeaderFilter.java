package club.p6e.coat.common.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * Blocking Base64 Decryption Header Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingBase64DecryptionHeaderFilter implements Filter {

    /**
     * Match Header
     *
     * @param request Http Servlet Request Object
     * @return Match Header List Result
     */
    public List<String> match(HttpServletRequest request) {
        final List<String> result = new ArrayList<>();
        final Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            final String name = enumeration.nextElement();
            if (name != null && name.toLowerCase().startsWith("p6e-")) {
                result.add(name);
            }
        }
        return result;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new CustomHttpServletRequest((HttpServletRequest) request, this::match), response);
    }

    /**
     * Custom Http Servlet Request
     */
    private static class CustomHttpServletRequest extends HttpServletRequestWrapper {

        /**
         * Headers Object
         */
        private final Map<String, String> headers = new HashMap<>();

        /**
         * Constructor Initialization
         *
         * @param request Http Servlet Request Object
         * @param matcher Matcher Object
         */
        public CustomHttpServletRequest(HttpServletRequest request, Function<HttpServletRequest, List<String>> matcher) {
            super(request);
            final List<String> pending = matcher.apply(request);
            final Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                final String name = enumeration.nextElement();
                final String value = request.getHeader(name);
                if (name != null && value != null) {
                    if (pending.contains(name)) {
                        addHeader(name, new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8));
                    } else {
                        addHeader(name, value);
                    }
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

        /**
         * Add Header
         *
         * @param name  Header Name
         * @param value Header Value
         */
        public void addHeader(String name, String value) {
            this.headers.put(name, value);
        }

    }

}
