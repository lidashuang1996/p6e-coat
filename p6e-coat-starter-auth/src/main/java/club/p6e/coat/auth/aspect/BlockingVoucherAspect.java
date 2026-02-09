package club.p6e.coat.auth.aspect;

import club.p6e.coat.auth.cache.BlockingVoucherCache;
import club.p6e.coat.common.exception.VoucherException;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Blocking Voucher Aspect
 *
 * @author lidashuang
 * @version 1.0
 */
@Aspect
@Order(Integer.MIN_VALUE + 30000)
@ConditionalOnMissingBean(BlockingVoucherAspect.class)
@Component("club.p6e.coat.auth.aspect.BlockingVoucherAspect")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingVoucherAspect {

    /**
     * White Path List Object
     */
    private static final List<String> WHITE_LIST = new ArrayList<>(List.of(
            "club.p6e.coat.auth.controller.BlockingIndexController.def1()",
            "club.p6e.coat.auth.controller.BlockingIndexController.def2()",
            "club.p6e.coat.auth.controller.BlockingIndexController.def3()"
    ));

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

    @Pointcut("execution(* club.p6e.coat.auth.controller.Blocking*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletResponse response = null;
        MyHttpServletRequestWrapper request = null;
        final String tmn = joinPoint.getSignature().getName();
        final String tcn = joinPoint.getTarget().getClass().getName();
        final String path = tmn + "." + tcn + "()";
        if (WHITE_LIST.contains(path)) {
            return joinPoint.proceed();
        } else {
            final Object[] args = joinPoint.getArgs();
            for (final Object arg : args) {
                if (arg instanceof HttpServletRequest hsr) {
                    request = new MyHttpServletRequestWrapper(hsr);
                }
                if (arg instanceof HttpServletResponse hsr) {
                    response = hsr;
                }
            }
            final Object result = joinPoint.proceed(args);
            if (request != null && response != null && result != null) {
                request.save();
            }
            return result;
        }
    }

    /**
     * My Http Servlet Request Wrapper
     */
    public static class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

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
         * Http Servlet Request Object
         */
        private final HttpServletRequest request;

        /**
         * Mark
         */
        private String mark;

        /**
         * Constructor Initialization
         *
         * @param request Http Servlet Request Object
         */
        public MyHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
            init();
        }

        /**
         * Get Voucher Data
         *
         * @param vouchers Voucher List Object
         * @return Map Data Object
         */
        private Map<String, String> getVoucher(LinkedList<String> vouchers) {
            if (vouchers == null || vouchers.isEmpty()) {
                return new HashMap<>();
            }
            final String voucher = vouchers.poll();
            final Map<String, String> result = SpringUtil.getBean(BlockingVoucherCache.class).get(voucher);
            if (result == null) {
                return getVoucher(vouchers);
            } else {
                this.mark = voucher;
                result.put(MARK, voucher);
                return result;
            }
        }

        /**
         * Init Voucher Data
         */
        public void init() {
            final LinkedList<String> vouchers = new LinkedList<>();
            final String vouchers1 = this.request.getHeader(VOUCHER_HEADER);
            final String vouchers2 = this.request.getParameter(V_REQUEST_PARAM);
            final String vouchers3 = this.request.getParameter(VOUCHER_REQUEST_PARAM);
            if (vouchers1 != null) {
                vouchers.add(vouchers1);
            }
            if (vouchers2 != null) {
                vouchers.add(vouchers2);
            }
            if (vouchers3 != null) {
                vouchers.add(vouchers3);
            }
            if (vouchers.isEmpty()) {
                throw new VoucherException(
                        this.getClass(),
                        "fun void init()",
                        "request voucher does not exist"
                );
            } else {
                final Map<String, String> data = getVoucher(vouchers);
                if (data.isEmpty()) {
                    throw new VoucherException(
                            this.getClass(),
                            "fun void init()",
                            "request voucher does not exist or has expired"
                    );
                } else {
                    data.forEach(this::setAttribute);
                }
            }
        }

        /**
         * Save Cache Voucher Data
         */
        public void save() {
            final Map<String, String> content = new HashMap<>();
            this.attributes.forEach((k, v) -> content.put(k, String.valueOf(v)));
            if (this.attributes.containsKey(DELETE)) {
                delete();
            } else {
                SpringUtil.getBean(BlockingVoucherCache.class).set(this.mark, content);
            }
        }

        /**
         * Delete Voucher Data
         */
        public void delete() {
            SpringUtil.getBean(BlockingVoucherCache.class).del(this.mark);
        }

        @Override
        public Object getAttribute(String key) {
            return this.attributes.get(key);
        }

        @Override
        public void setAttribute(String key, Object value) {
            this.attributes.put(key, value);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            final List<String> names = new ArrayList<>();
            final Enumeration<String> enumeration = super.getAttributeNames();
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    names.add(enumeration.nextElement());
                }
            }
            names.addAll(this.attributes.keySet());
            return Collections.enumeration(names);
        }

        @Override
        public void removeAttribute(String name) {
            super.removeAttribute(name);
            this.attributes.remove(name);
        }

    }

}
