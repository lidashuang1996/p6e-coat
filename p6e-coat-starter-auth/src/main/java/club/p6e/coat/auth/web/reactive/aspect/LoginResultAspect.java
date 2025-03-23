package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.token.web.reactive.TokenGenerator;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseCookie;
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
@Order(Integer.MIN_VALUE)
public class LoginResultAspect {

    @Pointcut("execution(* club.p6e.coat.auth.web.reactive.controller.*.*(..))")
    public void pointcut() {
    }

    @SuppressWarnings("ALL")
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ServerWebExchange exchange = null;
        final Object[] args = joinPoint.getArgs();
        for (final Object arg : args) {
            if (arg instanceof ServerWebExchange tmp) {
                exchange = tmp;
                break;
            }
        }
        final Object result = joinPoint.proceed();
        if (exchange != null && result instanceof final Mono<?> mono) {
            final ServerWebExchange e = exchange;
            return mono.flatMap(r -> {
                if (r instanceof final User user) {
                    return SpringUtil.getBean(TokenGenerator.class).execute(e, user).flatMap(o -> {
                        if (o instanceof final ResponseCookie cookie) {
                            e.getResponse().addCookie(cookie);
                            return Mono.just(String.valueOf(System.currentTimeMillis()));
                        } else {
                            return Mono.just(o);
                        }
                    }).map(ResultContext::build);
                }
                if (r instanceof final String string && string.equals("AUTHENTICATION")) {
                    return Mono.just(ResultContext.build(String.valueOf(System.currentTimeMillis())));
                }
                return Mono.just(ResultContext.build(r));
            });
        }
        return result;
    }

}
