package club.p6e.coat.auth.validator;

import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * 参数验证器
 *
 * @author lidashuang
 * @version 1.0
 */
public class ParameterValidator {

    /**
     * 是否初始化
     */
    private static boolean IS_INIT = false;

    /**
     * 验证器的缓存对象
     */
    private static final Map<Class<?>, ParameterValidatorInterface> CACHE = new HashMap<>();

    /**
     * 初始化
     */
    private synchronized static void init() {
        if (!IS_INIT) {
            IS_INIT = true;
            final Map<String, ParameterValidatorInterface>
                    map = SpringUtil.getBeans(ParameterValidatorInterface.class);
            for (final ParameterValidatorInterface value : map.values()) {
                CACHE.put(value.select(), value);
            }
        }
    }

    /**
     * 执行验证
     *
     * @param param 需要验证的参数
     * @return 验证的结果
     */
    public static Mono<Void> execute(ServerWebExchange exchange, Object param) {
        if (!IS_INIT) {
            init();
        }
        final List<Class<?>> objectClassList = getObjectClassList(param);
        final List<ParameterValidatorInterface> validatorList = new ArrayList<>();
        for (final Class<?> item : objectClassList) {
            final ParameterValidatorInterface validator = CACHE.get(item);
            if (validator != null) {
                validatorList.add(validator);
            }
        }
        if (validatorList.isEmpty()) {
            return Mono.just(true).then();
        } else {
            validatorList.sort(Comparator.comparingInt(ParameterValidatorInterface::order));
            return Flux
                    .just(validatorList)
                    .flatMap(Flux::fromIterable)
                    .flatMap(v -> v.execute(exchange, param))
                    .filter(b -> b)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeParameterException(
                            ParameterValidator.class,
                            "fun execute(ServerWebExchange exchange, Object param)",
                            "Request parameter exception."
                    ))).then();
        }
    }

    /**
     * 获取 Object 对象的类型列表
     *
     * @param o Object 对象
     * @return 类型列表
     */
    private static List<Class<?>> getObjectClassList(Object o) {
        final List<Class<?>> list = new ArrayList<>();
        list.add(o.getClass());
        Class<?> superclass = o.getClass().getSuperclass();
        while (!Objects.equals(superclass, Object.class)) {
            list.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return list;
    }

}
