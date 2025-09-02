package club.p6e.coat.auth.web.aspect;

import club.p6e.coat.common.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Component
@Order(Integer.MIN_VALUE + 30000)
@ConditionalOnMissingBean(LoginAspect.class)
@ConditionalOnClass(name = "org.springframework.web.package-info")
public class LogAspect {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("execution(* club.p6e.coat.auth.web.controller.*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = null;
        final Object[] args = joinPoint.getArgs();
        final Map<String, Object> params = new HashMap<>();
        for (final Object arg : args) {
            if (arg instanceof HttpServletRequest hsr) {
                request = hsr;
            }
            if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)) {
                params.put(arg.getClass().getName(), arg);
            }
        }
        if (request != null) {
            LOGGER.info("⬇======================================⬇");
            LOGGER.info("[ {} ] >>> {}", request.getMethod(), request.getRequestURI());
            LOGGER.info("REQUEST PARAMS: {}", JsonUtil.toJson(params));
            final Enumeration<String> enumeration = request.getAttributeNames();
            while (enumeration.hasMoreElements()) {
                final String key = enumeration.nextElement();
                final Object value = request.getAttribute(key);
                LOGGER.info("REQUEST ATTRIBUTE: {} = {}", key, value);
            }
            LOGGER.info("↑======================================↑");
        }
        return joinPoint.proceed();
    }

}
