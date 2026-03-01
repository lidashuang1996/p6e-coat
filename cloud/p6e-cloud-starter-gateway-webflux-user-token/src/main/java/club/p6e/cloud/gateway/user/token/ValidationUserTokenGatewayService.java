package club.p6e.cloud.gateway.user.token;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.token.ReactiveLocalStorageCacheTokenGenerator;
import club.p6e.coat.auth.token.ReactiveLocalStorageCacheTokenValidator;
import club.p6e.coat.auth.token.ReactiveUserTokenCache;
import club.p6e.coat.common.utils.WebUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Validation Permission Gateway Service
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(ValidationUserTokenGatewayService.class)
public class ValidationUserTokenGatewayService {

    /**
     * Reactive Permission Filter Object
     */
    private final UserBuilder builder;
    private final UserRepository userRepository;
    private final UserTokenRepository userTokenRepository;
    private final ReactiveLocalStorageCacheTokenValidator validator;
    private final UserTokenLocalStorageCacheTokenGenerator generator;

    protected static final String DEVICE_HEADER_NAME = "P6e-Device";

    /**
     * Constructor Initialization
     *
     * @param userRepository      Permission Validator Object
     * @param userTokenRepository Permission Validator Object
     */
    public ValidationUserTokenGatewayService(UserRepository userRepository, UserTokenRepository userTokenRepository, UserBuilder builder, ReactiveUserTokenCache cache) {
        this.builder = builder;
        this.userRepository = userRepository;
        this.userTokenRepository = userTokenRepository;
        this.generator = new UserTokenLocalStorageCacheTokenGenerator(cache);
        this.validator = new ReactiveLocalStorageCacheTokenValidator(builder, cache);
    }

    /**
     * Execute User Service
     *
     * @param exchange Server Web Exchange Object
     * @return Mono<User> User Object
     */
    public Mono<ServerWebExchange> execute(ServerWebExchange exchange) {
        final String token = WebUtil.getToken(exchange.getRequest());
        if (token.startsWith("p6e@")) {
            return validator.execute(exchange).switchIfEmpty(userTokenRepository.get(token).flatMap(m -> userRepository.get(m.getUid())).map(builder::create).flatMap(u -> generator.execute(exchange, u, token).map(_ -> u))).map(_ -> exchange);
        } else {
            return Mono.empty();
        }
    }

    private static class UserTokenLocalStorageCacheTokenGenerator extends ReactiveLocalStorageCacheTokenGenerator {

        private String token;

        /**
         * Constructor Initialization
         *
         * @param cache User Token Cache Object
         */
        public UserTokenLocalStorageCacheTokenGenerator(ReactiveUserTokenCache cache) {
            super(cache);
        }

        public Mono<Object> execute(ServerWebExchange exchange, User user, String token) {
            return Mono.just(token).flatMap(t -> {
                this.token = t;
                return super.execute(exchange.mutate().request(exchange.getRequest().mutate().header(DEVICE_HEADER_NAME, "USER_TOKEN").build()).build(), user);
            }).map(o -> {
                this.token = null;
                return o;
            });
        }

        @Override
        public String token() {
            return token == null ? super.token() : token;
        }

    }

}
