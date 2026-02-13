package club.p6e.coat.common.controller;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

/**
 * Reactive Base64 Encryption Header Filter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveBase64EncryptionHeaderFilter implements WebFilter {

    /**
     * Match Header
     *
     * @param request Server Http Request Object
     * @return Match Header List Result
     */
    public List<String> match(ServerHttpRequest request) {
        final List<String> result = new ArrayList<>();
        final HttpHeaders headers = request.getHeaders();
        for (final String name : headers.headerNames()) {
            if (name.toLowerCase().startsWith("p6e-")) {
                result.add(name);
            }
        }
        return result;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return chain.filter(exchange.mutate().request(new CustomServerHttpRequestDecorator(exchange.getRequest(), this::match)).build());
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
         * @param matcher  Matcher Object
         */
        public CustomServerHttpRequestDecorator(ServerHttpRequest delegate, Function<ServerHttpRequest, List<String>> matcher) {
            super(delegate);
            this.httpHeaders = new HttpHeaders();
            final HttpHeaders superHeaders = super.getHeaders();
            final List<String> pending = matcher.apply(delegate);
            for (final String name : superHeaders.headerNames()) {
                final List<String> values = superHeaders.get(name);
                if (values != null) {
                    if (pending.contains(name)) {
                        this.httpHeaders.addAll(name, values.stream().map(v -> Base64.getEncoder().encodeToString(v.getBytes(StandardCharsets.UTF_8))).toList());
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
