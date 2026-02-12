package club.p6e.coat.auth.service;

import club.p6e.coat.auth.token.ReactiveTokenCleaner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Index Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.service.ReactiveLogoutServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLogoutServiceImpl implements ReactiveLogoutService {

    /**
     * Reactive Token Object
     */
    private final ReactiveTokenCleaner cleaner;

    /**
     * Constructor Initialization
     *
     * @param cleaner Reactive Token Object
     */
    public ReactiveLogoutServiceImpl(ReactiveTokenCleaner cleaner) {
        this.cleaner = cleaner;
    }

    @Override
    public Mono<Object> execute(ServerWebExchange exchange) {
        return cleaner.execute(exchange);
    }

}
