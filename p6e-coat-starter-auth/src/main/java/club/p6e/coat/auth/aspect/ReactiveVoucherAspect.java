package club.p6e.coat.auth.aspect;

import club.p6e.coat.auth.cache.ReactiveVoucherCache;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.exception.VoucherException;
import club.p6e.coat.common.utils.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
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
 * Reactive Voucher Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Order(Integer.MIN_VALUE + 30000)
@ConditionalOnMissingBean(ReactiveVoucherAspect.class)
@Component("club.p6e.coat.auth.aspect.ReactiveVoucherAspect")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveVoucherAspect {

    /**
     * White Path List Object
     */
    private static final List<String> WHITE_LIST = List.of(
            "club.p6e.coat.auth.controller.ReactiveIndexController.def1()",
            "club.p6e.coat.auth.controller.ReactiveIndexController.def2()",
            "club.p6e.coat.auth.controller.ReactiveIndexController.def3()"
    );

    /**
     * Add White Path
     *
     * @param path Path
     */
    @SuppressWarnings("ALL")
    public static void addWhitePath(String path) {
        WHITE_LIST.add(path);
    }

    /**
     * Remove White Path
     *
     * @param path Path
     */
    @SuppressWarnings("ALL")
    public static void removeWhitePath(String path) {
        WHITE_LIST.remove(path);
    }

    @Pointcut("execution(* club.p6e.coat.auth.controller.Reactive*.*(..))")
    public void pointcut() {
    }

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
            for (final Object arg : args) {
                if (arg instanceof ServerWebExchange swe) {
                    request = new MyServerHttpRequestDecorator(swe.getRequest());
                    swe.mutate().request(request).build();
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

    /**
     * My Server Http Request Decorator
     */
    public static class MyServerHttpRequestDecorator extends ServerHttpRequestDecorator {

        /**
         * Voucher Mark
         */
        public static final String MARK = "__VOUCHER_MARK__";

        /**
         * Voucher Delete Mark
         */
        public static final String DELETE = "__VOUCHER_DELETE__";

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
                    .getBean(ReactiveVoucherCache.class)
                    .get(voucher)
                    .switchIfEmpty(getVoucher(vouchers))
                    .map(m -> {
                        this.mark = voucher;
                        m.put(MARK, voucher);
                        return m;
                    });
        }

        /**
         * Init Voucher
         *
         * @return My Server Http Request Decorator Object
         */
        public Mono<MyServerHttpRequestDecorator> init() {
            final List<String> vouchers1 = getDelegate().getHeaders().get(VOUCHER_HEADER);
            final List<String> vouchers2 = getDelegate().getQueryParams().get(V_REQUEST_PARAM);
            final List<String> vouchers3 = getDelegate().getQueryParams().get(VOUCHER_REQUEST_PARAM);
            final LinkedList<String> vouchers = new LinkedList<>();
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
                return Mono.error(new VoucherException(
                        this.getClass(),
                        "fun Mono<MyServerHttpRequestDecorator> init()",
                        "request voucher does not exist"
                ));
            } else {
                return getVoucher(vouchers)
                        .switchIfEmpty(Mono.error(new VoucherException(
                                this.getClass(),
                                "fun Mono<MyServerHttpRequestDecorator> init()",
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
            if (this.attributes.containsKey(DELETE)) {
                return delete().map(d -> this);
            } else {
                this.attributes.forEach((k, v) -> content.put(k, String.valueOf(v)));
                return SpringUtil
                        .getBean(ReactiveVoucherCache.class)
                        .set(this.mark, content)
                        .switchIfEmpty(Mono.error(new CacheException(
                                this.getClass(),
                                "fun Mono<MyServerHttpRequestDecorator> save()",
                                "request voucher cache data save exception"
                        )))
                        .map(b -> this);
            }
        }

        /**
         * Delete Voucher
         *
         * @return Server Http Request Object
         */
        public Mono<MyServerHttpRequestDecorator> delete() {
            return SpringUtil.getBean(ReactiveVoucherCache.class).del(this.mark).map(b -> this);
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
