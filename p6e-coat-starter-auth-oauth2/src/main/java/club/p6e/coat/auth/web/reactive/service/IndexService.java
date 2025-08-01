package club.p6e.coat.auth.web.reactive.service;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface IndexService {

    Mono<String[]> execute(ServerWebExchange exchange);

}
