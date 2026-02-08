package club.p6e.coat.shield;

import club.p6e.coat.common.context.ResultContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface Validator {

    /**
     * Name
     *
     * @return Name
     */
    String name();

    /**
     * Execute
     *
     * @param exchange Server Web Exchange Object
     * @param token    Token
     * @return
     */
    Mono<ResultContext> execute(ServerWebExchange exchange, String token);

}
