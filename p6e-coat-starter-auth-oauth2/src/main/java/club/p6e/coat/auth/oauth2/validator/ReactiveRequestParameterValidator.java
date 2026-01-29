package club.p6e.coat.auth.oauth2.validator;

import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Request Parameter Validator
 *
 * @author lidashuang
 * @version 1.0
 */
public interface ReactiveRequestParameterValidator {

    /**
     * Execute Validator
     *
     * @param exchange Server Web Exchange
     * @param param    T Param Object
     * @return T Result Object
     */
    @SuppressWarnings("ALL")
    static <T> Mono<T> run(ServerWebExchange exchange, T param) {
        if (param == null) {
            return Mono.empty();
        } else {
            final List<ReactiveRequestParameterValidator> list = new ArrayList<>();
            final Map<String, ReactiveRequestParameterValidator> data = SpringUtil.getBeans(ReactiveRequestParameterValidator.class);
            for (ReactiveRequestParameterValidator value : data.values()) {
                if (value.type().equals(param.getClass())) {
                    list.add(value);
                }
            }
            if (list.isEmpty()) {
                return Mono.just(param);
            } else {
                list.sort(Comparator.comparingInt(ReactiveRequestParameterValidator::order));
                return Flux.fromStream(list.stream().map(v -> v.execute(exchange, param))).then(Mono.just(param));
            }
        }
    }

    /**
     * Order
     *
     * @return Order Object
     */
    int order();

    /**
     * Class Type
     *
     * @return Class Object
     */
    Class<?> type();

    /**
     * Execute Validator
     *
     * @param param T Param Object
     * @return T Result Object
     */
    <T> Mono<T> execute(ServerWebExchange exchange, T param);

}
