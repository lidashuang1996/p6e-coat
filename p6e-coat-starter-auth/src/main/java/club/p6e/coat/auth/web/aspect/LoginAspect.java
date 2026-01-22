package club.p6e.coat.auth.web.aspect;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.token.BlockingTokenGenerator;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;

/**
 * Login Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Order(Integer.MIN_VALUE + 20000)
@ConditionalOnMissingBean(LoginAspect.class)
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class LoginAspect {

    @Pointcut("execution(* club.p6e.coat.auth.web.controller.*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = null;
        HttpServletResponse response = null;
        final Object[] args = joinPoint.getArgs();
        for (final Object arg : args) {
            if (arg instanceof HttpServletRequest hsr) {
                request = hsr;
            }
            if (arg instanceof HttpServletResponse hsr) {
                response = hsr;
            }
        }
        final Object result = joinPoint.proceed();
        if (request != null && response != null && result != null) {
            if (result instanceof final User u) {
                // delete voucher
                request.setAttribute(VoucherAspect.MyHttpServletRequestWrapper.DELETE, "1");
                return ResultContext.build(SpringUtil.getBean(BlockingTokenGenerator.class).execute(request, response, u));
            } else if (result instanceof final String rs && "AUTHENTICATION".equalsIgnoreCase(rs)) {
                // OAuth2 authentication
                // delete voucher
                request.setAttribute(VoucherAspect.MyHttpServletRequestWrapper.DELETE, "1");
                return ResultContext.build(String.valueOf(System.currentTimeMillis()));
            }
        }
        return result;
    }

}
