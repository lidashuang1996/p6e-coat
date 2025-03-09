package club.p6e.coat.auth.token;

import club.p6e.coat.auth.TokenGenerator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface WebFluxTokenGenerator extends TokenGenerator<ServerWebExchange, Mono<Object>> {
}
