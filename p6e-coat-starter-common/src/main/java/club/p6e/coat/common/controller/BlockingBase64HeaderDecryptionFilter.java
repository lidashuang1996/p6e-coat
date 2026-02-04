package club.p6e.coat.common.controller;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Blocking Base64 Header Decryption Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingBase64HeaderDecryptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new CustomHttpServletRequest((HttpServletRequest) servletRequest), servletResponse);
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
         */
        public CustomHttpServletRequest(HttpServletRequest request) {
            super(request);
            final Enumeration<String> enumeration = request.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                final String name = enumeration.nextElement();
                final String value = request.getHeader(name);
                if (name != null) {
                    if (name.toLowerCase().startsWith("p6e-")) {
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

        public void addHeader(String name, String value) {
            this.headers.put(name, value);
        }

    }

}
