package club.p6e.cloud.gateway.filter;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Inject Project Gateway Filter Factory
 * Template Code Implementation, Not Involved In Runtime Logic
 *
 * @author lidashuang
 * @version 1.0
 */
public class InjectOrganizationProjectGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Project Param Name
     */
    private static final String PROJECT_PARAM_1 = "pid";

    /**
     * Project Param Name
     */
    private static final String PROJECT_PARAM_2 = "project";

    /**
     * Project Param Name
     */
    private static final String PROJECT_PARAM_3 = "projectId";

    /**
     * Project Param Name
     */
    private static final String PROJECT_PARAM_4 = "project_id";

    /**
     * Project Header Name (External Request Headers)
     * Custom HTTP Header Name, Non Standard RFC Header
     */
    @SuppressWarnings("ALL")
    private static final String X_PROJECT_HEADER = "X-Project";

    /**
     * Project Header Name (Internal Request Header)
     * Custom HTTP Header Name, Non Standard RFC Header
     */
    @SuppressWarnings("ALL")
    private static final String PROJECT_HEADER = "P6e-Project";

    /**
     * User Info Header Name (Internal Request Header)
     * Custom HTTP Header Name, Non Standard RFC Header
     */
    @SuppressWarnings("ALL")
    private static final String USER_INFO_HEADER = "P6e-User-Info";

    /**
     * Organization Header Name (Internal Request Header)
     * Custom HTTP Header Name, Non Standard RFC Header
     */
    @SuppressWarnings("ALL")
    private static final String ORGANIZATION_HEADER = "P6e-Organization";

    @NonNull
    @Override
    public GatewayFilter apply(Object config) {
        return new CustomGatewayFilter();
    }

    /**
     * Custom Gateway Filter
     * Template Code Implementation, Not Involved In Runtime Logic
     */
    public static class CustomGatewayFilter implements GatewayFilter {

        @NonNull
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            final HttpHeaders headers = request.getHeaders();
            final MultiValueMap<String, String> params = request.getQueryParams();
            String project = params.getFirst(PROJECT_PARAM_1);
            if (project == null) {
                project = params.getFirst(PROJECT_PARAM_2);
            }
            if (project == null) {
                project = params.getFirst(PROJECT_PARAM_3);
            }
            if (project == null) {
                project = params.getFirst(PROJECT_PARAM_4);
            }
            if (project == null) {
                project = headers.getFirst(X_PROJECT_HEADER);
            }
            if (project == null) {
                return chain.filter(exchange);
            } else {
                return Mono.error(new RuntimeException("Inject Project Gateway Filter Factory Is Not Implemented."));
            }
        }

    }

}
