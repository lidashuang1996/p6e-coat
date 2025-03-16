package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.auth.web.reactive.token.TokenGenerator;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

/**
 * Server Http Request Voucher Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
public class ServerHttpRequestAspect extends Aspect {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    Mono<Object> before(Object[] os) {
        for (int i = 0; i < os.length; i++) {
            if (os[i] instanceof final ServerWebExchange exchange) {
                final ServerHttpRequest request = new ServerHttpRequest(exchange.getRequest());
                os[i] = exchange.mutate().request(request).build();
                return request.init().map(that -> os);
            }
        }
        return Mono.just(os);
    }

    @Override
    Mono<Object> after(Object[] os) {
        User user = null;
        ServerWebExchange exchange = null;
        for (final Object item : os) {
            if (item instanceof User) {
                user = (User) item;
            } else if (item instanceof ServerWebExchange) {
                exchange = (ServerWebExchange) item;
            }
        }
        if (exchange != null && exchange.getRequest() instanceof final ServerHttpRequest request) {
            if (user == null) {
                return request.save().map(s -> os);
            } else {
                //  return request.remove().map(s -> os);
                return SpringUtil
                        .getBean(TokenGenerator.class)
                        .execute(exchange, user)
                        .map(r -> {
                            os[os.length - 1] = r;
                            return os;
                        });
            }
        }
        System.out.println("AAAAAAAAAA333333333333333333333");
        return Mono.just(os);
    }

}
