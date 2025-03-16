//package club.p6e.coat.auth.web.reactive.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.web.server.ServerWebExchange;
//
///**
// * Login Result Aspect
// *
// * @author lidashuang
// * @version 1.0
// */
//@Aspect
//public class LoginResultAspect {
//
//    @Pointcut("execution(* club.p6e.coat.auth.web.reactive.controller.*.*(..))")
//    public void pointcut() {
//    }
//
//    @Around("pointcut()")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
////        ServerWebExchange exchange = null;
////        final Object[] args = joinPoint.getArgs();
////        for (final Object o : args) {
////            if (o instanceof ServerWebExchange tmp) {
////                exchange = tmp;
////                break;
////            }
////        }
//        return joinPoint.proceed();
//    }
//
//}
