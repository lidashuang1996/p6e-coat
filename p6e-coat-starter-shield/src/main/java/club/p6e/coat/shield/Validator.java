package club.p6e.coat.shield;

import club.p6e.coat.common.context.ResultContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface Validator  {

    /**
     * 名称
     * @return 名称
     */
    String name();

    Mono<ResultContext> execute(ServerWebExchange exchange, String token);
}
