package club.p6e.cloud.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Inject Organization Project Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class InjectOrganizationProjectGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Project Param Name
     * Request To Carry Project Parameter [pid/1]
     */
    @SuppressWarnings("ALL")
    private static final String PROJECT_PARAM_1 = "pid";

    /**
     * Project Param Name
     * Request To Carry Project Parameter [project/2]
     */
    @SuppressWarnings("ALL")
    private static final String PROJECT_PARAM_2 = "project";

    /**
     * Project Param Name
     * Request To Carry Project Parameter [projectId/3]
     */
    @SuppressWarnings("ALL")
    private static final String PROJECT_PARAM_3 = "projectId";

    /**
     * Project Header Name
     * Request To Carry Project Request Header
     */
    @SuppressWarnings("ALL")
    private static final String X_PROJECT_HEADER = "X-Project";

    /**
     * Project Header Name
     * Request Header For The User Current Project
     * Request Header Is Customized By The Program And Not Carried By The User Request
     * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
     */
    @SuppressWarnings("ALL")
    private static final String PROJECT_HEADER = "P6e-Project";

    @Override
    public GatewayFilter apply(Object config) {
        return new CustomGatewayFilter();
    }

    /**
     * Custom Gateway Filter
     */
    public static class CustomGatewayFilter implements GatewayFilter {

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            final ServerHttpRequest request = exchange.getRequest();
            String project = request.getQueryParams().getFirst(PROJECT_PARAM_1);
            if (project == null) {
                project = request.getHeaders().getFirst(PROJECT_PARAM_2);
            }
            if (project == null) {
                project = request.getHeaders().getFirst(PROJECT_PARAM_3);
            }
            if (project == null) {
                project = request.getHeaders().getFirst(X_PROJECT_HEADER);
            }
            if (project == null) {
                return chain.filter(exchange);
            } else {
                return chain.filter(exchange.mutate().request(
                        exchange.getRequest().mutate().header(PROJECT_HEADER, project).build()
                ).build());
            }
        }

    }

}
