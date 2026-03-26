package club.p6e.cloud.gateway;

import org.jspecify.annotations.NonNull;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic Web Filter
 *
 * @author lidashuang
 * @version 1.0
 */
public class BaseWebFilter implements WebFilter, Ordered {

    /**
     * Order
     */
    private static final int ORDER = Integer.MIN_VALUE + 2000;

    /**
     * Only Response Header
     */
    private static final List<String> ONLY_RESPONSE_HEADERS = List.of(
            "Content-Type",
            "Access-Control",
            "Access-Control-Max-Age",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Headers",
            "Access-Control-Allow-Methods",
            "Access-Control-Allow-Credentials"
    );

    @Override
    public int getOrder() {
        return ORDER;
    }

    @NonNull
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();
        return chain.filter(exchange
                .mutate()
                .request(new CustomServerHttpRequestDecorator(request))
                .response(new CustomServerHttpResponseDecorator(response))
                .build()
        );
    }

    /**
     * Custom Server Http Request Decorator
     */
    private static class CustomServerHttpRequestDecorator extends ServerHttpRequestDecorator {

        /**
         * Constructor Initialization
         */
        public CustomServerHttpRequestDecorator(ServerHttpRequest delegate) {
            super(delegate);
            // request header is used internally for calling
            // prohibit sending requests that carry this request header to downstream services
            final List<String> headers = new ArrayList<>();
            for (final String headerName : this.getHeaders().headerNames()) {
                if (headerName.toLowerCase().startsWith("p6e-")) {
                    headers.add(headerName);
                }
            }
            headers.forEach(h -> this.getHeaders().remove(h));
        }

    }

    /**
     * Custom Server Http Response Decorator
     */
    private static class CustomServerHttpResponseDecorator extends ServerHttpResponseDecorator {

        /**
         * Constructor Initialization
         */
        public CustomServerHttpResponseDecorator(ServerHttpResponse delegate) {
            super(delegate);
        }

        @NonNull
        @Override
        public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
            final List<String> headers = new ArrayList<>();
            for (final String headerName : this.getHeaders().headerNames()) {
                if (ONLY_RESPONSE_HEADERS.contains(headerName)) {
                    headers.add(headerName);
                }
            }
            headers.forEach(h -> this.getHeaders().set(h, this.getHeaders().getFirst(h)));
            return super.writeWith(body);
        }

    }

}
