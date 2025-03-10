package club.p6e.coat.auth.aspect;

import club.p6e.coat.auth.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public class WebFluxVoucherAspect extends WebFluxAspect {

    @Override
    Mono<Object> before(Object o) {
        if (o instanceof final Object[] os) {
            ServerWebExchange tmp = null;
            for (final Object item : os) {
                if (item instanceof ServerWebExchange) {
                    tmp = (ServerWebExchange) item;
                    break;
                }
            }
            if (tmp == null) {
                return Mono.just(o);
            } else {
                final ServerWebExchange exchange = tmp;
                return new ServerHttpRequest(exchange.getRequest()).init().map(that -> {
                    exchange.mutate().request(that).build();
                    return o;
                });
            }
        } else {
            return Mono.just(new Object[0]);
        }
    }

    @Override
    Mono<Object> after(Object o) {
        return null;
    }

}
