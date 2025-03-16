package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.auth.web.reactive.token.TokenGenerator;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
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
public class GlobalAspect {

    @Pointcut("execution(* club.p6e.coat.auth.web.reactive.controller.*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServerHttpRequest request = null;
        ServerWebExchange exchange = null;
        final Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServerWebExchange tmp) {
                request = new ServerHttpRequest(tmp.getRequest());
                exchange = tmp.mutate().request(request).build();
                args[i] = exchange;
                break;
            }
        }
        System.out.println("nnnnnnnnnnnnnnnnnnnn1111111111111111111111111111");
        final Object result = joinPoint.proceed(args);
        if (request != null && result instanceof final Mono<?> mono) {
            final ServerWebExchange e = exchange;
            return request.init().then(mono).flatMap(r -> {
                System.out.println("2222222222222223333333333333");
                if (r instanceof final User user) {
                    System.out.println("nnnnnnnnnnnnnnnnnnnn222222222222222222");
                    return SpringUtil.getBean(TokenGenerator.class).execute(e, user).map(ResultContext::build);
                }
                return Mono.just(ResultContext.build(r));
            });
        }
        return result;
    }

}
