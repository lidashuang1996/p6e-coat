package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.token.web.reactive.TokenGenerator;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Order(Integer.MIN_VALUE + 20000)
@ConditionalOnMissingBean(
        value = LoginAspect.class,
        ignored = LoginAspect.class
)
@Component("club.p6e.coat.auth.web.reactive.aspect.LoginAspect")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class LoginAspect {

    @Pointcut("execution(* club.p6e.coat.auth.web.reactive.controller.*.*(..))")
    public void pointcut() {
    }

    @SuppressWarnings("ALL")
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final Object[] args = joinPoint.getArgs();
        ServerWebExchange exchange = null;
        for (final Object arg : args) {
            if (arg instanceof ServerWebExchange swe) {
                exchange = swe;
                break;
            }
        }
        final Object result = joinPoint.proceed();
        if (exchange != null && result instanceof Mono<?> mono) {
            final ServerWebExchange e = exchange;
            return mono.flatMap(r -> {
                if (r instanceof final User ru) {
                    // delete voucher
                    e.getRequest().getAttributes().put(VoucherAspect.MyHttpServletRequestWrapper.DELETE, "1");
                    return SpringUtil.getBean(TokenGenerator.class).execute(e, ru).map(ResultContext::build);
                } else if (r instanceof final String rs && "AUTHENTICATION".equalsIgnoreCase(rs)) {
                    // OAuth2 authentication
                    // delete voucher
                    e.getRequest().getAttributes().put(VoucherAspect.MyHttpServletRequestWrapper.DELETE, "1");
                    return Mono.just(ResultContext.build(String.valueOf(System.currentTimeMillis())));
                }
                return Mono.just(ResultContext.build(r));
            });
        }
        return result;
    }

}
