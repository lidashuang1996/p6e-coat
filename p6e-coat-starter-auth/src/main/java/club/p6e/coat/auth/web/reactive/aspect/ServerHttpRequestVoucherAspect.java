package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Server Http Request Voucher Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
public class ServerHttpRequestVoucherAspect extends Aspect {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    Mono<Object> before(Object[] o) {
        ServerWebExchange tmp = null;
        for (final Object item : o) {
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
    }

    @Override
    Mono<Object> after(Object[] o) {
        ServerWebExchange tmp = null;
        for (final Object item : o) {
            if (item instanceof ServerWebExchange) {
                tmp = (ServerWebExchange) item;
                break;
            }
        }
        if (tmp != null && tmp.getRequest() instanceof final ServerHttpRequest request) {
            return request.cache().map(s -> o[o.length - 1]);
        }
        return Mono.just(o[o.length - 1]);
    }

}
