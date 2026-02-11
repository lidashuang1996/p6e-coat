package club.p6e.cloud.gateway.filter;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Inject Language Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class InjectLanguageGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Language Param Name
     * Request To Carry Language Parameter
     */
    @SuppressWarnings("ALL")
    private static final String LANGUAGE_PARAM = "language";

    /**
     * Language Header Name
     * Request To Carry Language Request Header
     */
    @SuppressWarnings("ALL")
    private static final String X_LANGUAGE_HEADER = "X-Language";

    /**
     * Language Header Name
     * Request Header For The User Current Language
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String LANGUAGE_HEADER = "P6e-Language";

    @NonNull
    @Override
    public GatewayFilter apply(Object object) {
        return new CustomGatewayFilter();
    }

    /**
     * Custom Gateway Filter
     */
    public static class CustomGatewayFilter implements GatewayFilter {

        @NonNull
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            String language = request.getQueryParams().getFirst(LANGUAGE_PARAM);
            if (language == null) {
                language = request.getHeaders().getFirst(X_LANGUAGE_HEADER);
            }
            if (language == null) {
                return chain.filter(exchange);
            } else {
                return chain.filter(exchange.mutate().request(request.mutate().header(LANGUAGE_HEADER, language).build()).build());
            }
        }

    }

}
