package club.p6e.coat.auth.aspect;

import club.p6e.coat.common.utils.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

/**
 * Reactive Log Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Order(Integer.MIN_VALUE + 10000)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLogAspect {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveLogAspect.class);

    @Pointcut("execution(* club.p6e.coat.auth.controller.Reactive*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServerWebExchange exchange = null;
        final Object[] args = joinPoint.getArgs();
        for (final Object arg : args) {
            if (arg instanceof ServerWebExchange swe) {
                exchange = swe;
                break;
            }
        }
        if (exchange != null) {
            final ServerHttpRequest request = exchange.getRequest();
            LOGGER.info("↓======================================↓");
            LOGGER.info("[ {} ] >>> {}", request.getMethod(), request.getPath().value());
            LOGGER.info("REQUEST PARAMS: {}", JsonUtil.toJson(request.getQueryParams()));
            final Map<String, Object> attributes = request.getAttributes();
            for (final String key : attributes.keySet()) {
                LOGGER.info("REQUEST ATTRIBUTE: {} ::: {}", key, attributes.get(key));
            }
            LOGGER.info("↑======================================↑");
        }
        return joinPoint.proceed();
    }

}
