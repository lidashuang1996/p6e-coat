package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
public abstract class Aspect implements Ordered {

    /**
     * Aspect Before
     *
     * @param o Aspect Point Param Object
     * @return Aspect Point Result Object
     */
    abstract Mono<Object> before(Object[] o);

    /**
     * Aspect After
     *
     * @param o Aspect Point Param Object
     * @return Aspect Point Result Object
     */
    abstract Mono<Object> after(Object[] o);

    /**
     * Get Aspect List
     *
     * @return Aspect List
     */
    private static List<Aspect> getAspects() {
        final List<Aspect> list = new CopyOnWriteArrayList<>();
        final Map<String, Aspect> aspects = SpringUtil.getBeans(Aspect.class);
        for (final String key : aspects.keySet()) {
            list.add(aspects.get(key));
        }
        list.sort(Comparator.comparingInt(Ordered::getOrder));
        return list;
    }

    /**
     * Execute Aspect Before
     *
     * @param o Aspect Point Param Object
     * @return Aspect Point Result Object
     */
    public static Mono<Object> executeBefore(Object[] o) {
        return Mono.just(getAspects()).flatMap(l -> executeBefore(l, o));
    }

    /**
     * Execute Aspect Before
     *
     * @param l Aspect List Object
     * @param o Aspect Point Param Object
     * @return Aspect Point Result Object
     */
    private static Mono<Object> executeBefore(List<Aspect> l, Object[] o) {
        return l.isEmpty() ? Mono.just(o) : Mono.just(o).flatMap(s -> l.remove(0).before(s).flatMap(r -> executeBefore(l, s)));
    }

    /**
     * Execute Aspect After
     *
     * @param o Aspect Point Param Object
     * @return Aspect Point Result Object
     */
    public static Mono<Object> executeAfter(Object[] o) {
        return Mono.just(getAspects()).flatMap(l -> executeAfter(l, o));
    }

    /**
     * Execute Aspect After
     *
     * @param l Aspect List Object
     * @param o Aspect Point Param Object
     * @return Aspect Point Result Object
     */
    private static Mono<Object> executeAfter(List<Aspect> l, Object[] o) {
        return l.isEmpty() ? Mono.just(o) : Mono.just(o).flatMap(s -> l.remove(0).after(s).flatMap(r -> executeAfter(l, s)));
    }

}
