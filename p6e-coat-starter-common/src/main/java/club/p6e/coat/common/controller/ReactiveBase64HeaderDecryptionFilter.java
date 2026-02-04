package club.p6e.coat.common.controller;

import jakarta.servlet.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Reactive Base64 Header Decryption Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveBase64HeaderDecryptionFilter implements WebFilter {

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return chain.filter(exchange.mutate().request(new CustomServerHttpRequestDecorator(exchange.getRequest())).build());
    }

    /**
     * Custom Server Http Request Decorator
     */
    private static class CustomServerHttpRequestDecorator extends ServerHttpRequestDecorator {

        /**
         * Http Headers Object
         */
        private final HttpHeaders httpHeaders;

        /**
         * Constructor Initialization
         *
         * @param delegate Server Http Request Object
         */
        public CustomServerHttpRequestDecorator(ServerHttpRequest delegate) {
            super(delegate);
            this.httpHeaders = new HttpHeaders();
            final HttpHeaders superHeaders = super.getHeaders();
            for (final String name : superHeaders.keySet()) {
                final List<String> values = superHeaders.get(name);
                if (values != null) {
                    if (name.toLowerCase().startsWith("p6e-")) {
                        this.httpHeaders.addAll(name, values.stream().map(v -> new String(Base64.getDecoder().decode(v), StandardCharsets.UTF_8)).toList());
                    } else {
                        this.httpHeaders.addAll(name, values);
                    }
                }
            }
        }

        @NonNull
        @Override
        public HttpHeaders getHeaders() {
            return httpHeaders;
        }

    }

}
