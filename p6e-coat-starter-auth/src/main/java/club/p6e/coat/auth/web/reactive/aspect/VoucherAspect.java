package club.p6e.coat.auth.web.reactive.aspect;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.cache.VoucherCache;
import club.p6e.coat.common.utils.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Voucher Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Component
@ConditionalOnMissingBean(VoucherAspect.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class VoucherAspect {

    /**
     * White Path List Object
     */
    private static final List<String> WHITE_LIST = List.of(
            "club.p6e.coat.auth.web.reactive.controller.IndexController.def1()",
            "club.p6e.coat.auth.web.reactive.controller.IndexController.def2()",
            "club.p6e.coat.auth.web.reactive.controller.IndexController.def3()"
    );

    @Pointcut("execution(* club.p6e.coat.auth.web.reactive.controller.*.*(..))")
    public void pointcut() {
    }

    @SuppressWarnings("ALL")
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        final String methodName = joinPoint.getSignature().getName();
        final String targetClassName = joinPoint.getTarget().getClass().getName();
        final String path = targetClassName + "." + methodName + "()";
        if (WHITE_LIST.contains(path)) {
            return joinPoint.proceed();
        } else {
            MyServerHttpRequestDecorator request = null;
            final Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof ServerWebExchange swe) {
                    request = new MyServerHttpRequestDecorator(swe.getRequest());
                    args[i] = swe.mutate().request(request).build();
                    break;
                }
            }
            final Object result = joinPoint.proceed(args);
            if (request != null && result instanceof final Mono<?> mono) {
                final MyServerHttpRequestDecorator r = request;
                return r.init().then(mono).flatMap(o -> r.save().map(t -> o));
            }
            return result;
        }
    }

    public static class MyServerHttpRequestDecorator extends ServerHttpRequestDecorator {

        /**
         * Http Request Header Voucher Name
         */
        @SuppressWarnings("ALL")
        private static final String VOUCHER_HEADER = "X-Voucher";

        /**
         * Http Request Param V
         */
        private static final String V_REQUEST_PARAM = "v";

        /**
         * Http Request Param Voucher
         */
        private static final String VOUCHER_REQUEST_PARAM = "voucher";

        /**
         * Cache Key [ACCOUNT]
         */
        @SuppressWarnings("ALL")
        public static final String ACCOUNT = "ACCOUNT";

        /**
         * Cache Key [QUICK_RESPONSE_CODE_LOGIN_MARK]
         */
        @SuppressWarnings("ALL")
        public static final String QUICK_RESPONSE_CODE_LOGIN_MARK = "QUICK_RESPONSE_CODE_LOGIN_MARK";

        /**
         * Cache Key [ACCOUNT_PASSWORD_SIGNATURE_MARK]
         */
        @SuppressWarnings("ALL")
        public static final String ACCOUNT_PASSWORD_SIGNATURE_MARK = "ACCOUNT_PASSWORD_SIGNATURE_MARK";

        /**
         * Server Http Request Attributes Object
         */
        private final Map<String, Object> attributes = new ConcurrentHashMap<>();

        /**
         * Mark
         */
        private String mark;

        /**
         * Constructor Initialization
         *
         * @param delegate Server Http Request Object
         */
        public MyServerHttpRequestDecorator(ServerHttpRequest delegate) {
            super(delegate);
        }

        /**
         * Get Voucher Data
         *
         * @param vouchers Voucher List Object
         * @return Map Data Object
         */
        private Mono<Map<String, String>> getVoucher(LinkedList<String> vouchers) {
            if (vouchers == null || vouchers.isEmpty()) {
                return Mono.empty();
            }
            final String voucher = vouchers.poll();
            return SpringUtil
                    .getBean(VoucherCache.class)
                    .get(voucher)
                    .switchIfEmpty(getVoucher(vouchers))
                    .map(m -> {
                        this.mark = voucher;
                        return m;
                    });
        }

        /**
         * Init Voucher
         *
         * @return My Server Http Request Decorator Object
         */
        public Mono<MyServerHttpRequestDecorator> init() {
            final LinkedList<String> vouchers = new LinkedList<>();
            final List<String> vouchers1 = getDelegate().getHeaders().get(VOUCHER_HEADER);
            final List<String> vouchers2 = getDelegate().getQueryParams().get(V_REQUEST_PARAM);
            final List<String> vouchers3 = getDelegate().getQueryParams().get(VOUCHER_REQUEST_PARAM);
            if (vouchers1 != null) {
                vouchers.addAll(vouchers1);
            }
            if (vouchers2 != null) {
                vouchers.addAll(vouchers2);
            }
            if (vouchers3 != null) {
                vouchers.addAll(vouchers3);
            }
            if (vouchers.isEmpty()) {
                return Mono.error(GlobalExceptionContext.executeVoucherException(
                        this.getClass(),
                        "fun init()",
                        "request voucher does not exist"
                ));
            } else {
                return getVoucher(vouchers)
                        .switchIfEmpty(Mono.error(GlobalExceptionContext.executeVoucherException(
                                this.getClass(),
                                "fun init()",
                                "request voucher does not exist or has expired"
                        ))).map(m -> {
                            m.forEach(this::setAttribute);
                            return this;
                        });
            }
        }

        /**
         * Cache Voucher
         *
         * @return My Server Http Request Decorator Object
         */
        public Mono<MyServerHttpRequestDecorator> save() {
            final Map<String, String> content = new HashMap<>();
            this.attributes.forEach((k, v) -> content.put(k, String.valueOf(v)));
            return SpringUtil
                    .getBean(VoucherCache.class)
                    .set(this.mark, content)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                            this.getClass(),
                            "fun save()",
                            "request voucher cache data save exception"
                    )))
                    .map(b -> this);
        }

        /**
         * Delete Voucher
         *
         * @return Server Http Request Object
         */
        @SuppressWarnings("ALL")
        public Mono<MyServerHttpRequestDecorator> delete() {
            return SpringUtil.getBean(VoucherCache.class).del(this.mark).map(b -> this);
        }

        /**
         * Get Attribute
         *
         * @param key Key
         * @return Value
         */
        @SuppressWarnings("ALL")
        public Object getAttribute(String key) {
            return this.attributes.get(key);
        }

        /**
         * Set Attribute
         *
         * @param key   Key
         * @param value Value
         */
        public void setAttribute(String key, Object value) {
            this.attributes.put(key, value);
        }

        @Override
        public @NonNull Map<String, Object> getAttributes() {
            return this.attributes;
        }

    }

}
