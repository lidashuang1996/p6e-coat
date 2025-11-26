package club.p6e.cloud.gateway.auth;

import club.p6e.coat.auth.User;
import club.p6e.coat.common.utils.JsonUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Authentication Gateway Filter Factory
 *
 * @author lidashuang
 * @version 1.0
 */
public class InjectAuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    /**
     * Authentication Gateway Service Object
     */
    private final InjectAuthenticationGatewayService service;

    /**
     * Constructor Initialization
     *
     * @param service Authentication Gateway Service object
     */
    public InjectAuthenticationGatewayFilterFactory(InjectAuthenticationGatewayService service) {
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
    public record CustomGatewayFilter(InjectAuthenticationGatewayService service) implements GatewayFilter {

        /**
         * User Info Header Name
         * Request Header For User Information
         * Request Header Is Customized By The Program And Not Carried By The User Request
         * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
         */
        @SuppressWarnings("ALL")
        private static final String USER_INFO_HEADER = "P6e-User-Info";

        /**
         * Authentication Header Name
         * Request Header For Authentication
         * Request Header Is Customized By The Program And Not Carried By The User Request
         * When Receiving Requests, It Is Necessary To Clear The Request Header Carried By The User To Ensure Program Security
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
                return this;
            }

            @Override
            public String serialize() {
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
                    // if the user is not logged in, the default is anonymous
                    .defaultIfEmpty(ANONYMOUS)
                    .flatMap(u -> chain.filter(exchange.mutate().request(
                            exchange.getRequest().mutate()
                                    .header(USER_INFO_HEADER, u.serialize())
                                    // mark user 0: anonymous, 1: login
                                    .header(AUTHENTICATION_HEADER, ANONYMOUS == u ? "0" : "1")
                                    .build()
                    ).build()));
        }

    }

}
