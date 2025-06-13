package club.p6e.cloud.gateway.auth;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Authentication Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Authentication Gateway Service Object
     */
    private final AuthenticationGatewayService service;

    /**
     * Constructor Initialization
     *
     * @param service Authentication Gateway Service object
     */
    public AuthenticationGatewayFilterFactory(AuthenticationGatewayService service) {
        this.service = service;
    }

    @Override
    public GatewayFilter apply(Object config) {
        return new CustomGatewayFilter(service);
    }

    /**
     * Custom Gateway Filter
     *
     * @param service Authentication Gateway Service Object
     */
    private record CustomGatewayFilter(AuthenticationGatewayService service) implements GatewayFilter {

        /**
         * User Info Header Name
         */
        @SuppressWarnings("ALL")
        private static final String USER_INFO_HEADER = "P6e-User-Info";

        /**
         * Authentication Header Name
         */
        @SuppressWarnings("ALL")
        private static final String AUTHENTICATION_HEADER = "P6e-Authentication";

        /**
         * Anonymous User
         */
        private static final User ANONYMOUS = new User() {
            @Override
            public String id() {
                return "0";
            }

            @Override
            public String password() {
                return "";
            }

            @Override
            public User password(String password) {
                return null;
            }

            @Override
            public String serialize() {
                System.out.println("11111111111 >>> " + toMap());
                System.out.println("2222222222 >>>> +" + JsonUtil.toJson(toMap()));
                return JsonUtil.toJson(toMap());
            }

            @Override
            public Map<String, Object> toMap() {
                return Map.of("id", "0");
            }
        };

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return service
                    .execute(exchange)
                    .defaultIfEmpty(ANONYMOUS)
                    .flatMap(u -> chain.filter(exchange.mutate().request(
                            exchange.getRequest().mutate()
                                    .header(USER_INFO_HEADER, u.serialize())
                                    .header(AUTHENTICATION_HEADER, ANONYMOUS == u ? "0" : "1")
                                    .build()
                    ).build()));
        }

    }

}
