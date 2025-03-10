package club.p6e.coat.auth.aspect;

import reactor.core.publisher.Mono;

/**
 * @author lidashuang
 * @version 1.0
 */
public abstract class WebFluxAspect implements Aspect {

    abstract Mono<Object> before(Object o);
//
//    /**
//     * 后置执行
//     *
//     * @param data 实体对象
//     * @return 实体对象
//     */
    abstract Mono<Object> after(Object o);

    public static Mono<Object> before(Object[] o) {
        return Mono.just(o);
    }

    public static Mono<Object> after(Object[] o) {
        return Mono.just("SUCCESS");
    }

}
