package club.p6e.coat.auth.aspect;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
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
import org.springframework.stereotype.Component;

/**
 * Blocking Login Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Order(Integer.MIN_VALUE + 20000)
@ConditionalOnMissingBean(BlockingLoginAspect.class)
@Component("club.p6e.coat.auth.aspect.BlockingLoginAspect")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginAspect {

    @Pointcut("execution(* club.p6e.coat.auth.controller.Blocking*.*(..))")
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
                request.setAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.DELETE, "1");
                return ResultContext.build(SpringUtil.getBean(BlockingTokenGenerator.class).execute(request, response, u));
            } else if (result instanceof LoginContext.Authentication.Dto) {
                request.setAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.DELETE, "1");
                return ResultContext.build(String.valueOf(System.currentTimeMillis()));
            }
        }
        return result;
    }

}
