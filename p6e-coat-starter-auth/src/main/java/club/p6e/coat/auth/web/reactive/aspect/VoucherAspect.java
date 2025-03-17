package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Result Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Component
public class VoucherAspect {

    @Pointcut("execution(* club.p6e.coat.auth.web.reactive.controller.*.*(..))")
    public void pointcut() {
    }

    @SuppressWarnings("ALL")
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServerHttpRequest request = null;
        final Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServerWebExchange tmp) {
                request = new ServerHttpRequest(tmp.getRequest());
                args[i] = tmp.mutate().request(request).build();
                break;
            }
        }
        final Object result = joinPoint.proceed(args);
        if (request != null && result instanceof final Mono<?> mono) {
            final ServerHttpRequest r = request;
            return r.init().then(mono).flatMap(o -> r.save().map(t -> o));
        }
        return result;
    }

}
