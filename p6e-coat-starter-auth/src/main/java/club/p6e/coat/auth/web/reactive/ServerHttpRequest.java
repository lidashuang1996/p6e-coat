package club.p6e.coat.auth.web.reactive;

import club.p6e.coat.auth.web.reactive.cache.VoucherCache;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.SpringUtil;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server Http Request
 *
 * @author lidashuang
 * @version 1.0
 */
public class ServerHttpRequest extends ServerHttpRequestDecorator {

    /**
     * Http Request Header Voucher Name
     */
    @SuppressWarnings("ALL")
    private static final String VOUCHER_HEADER = "P6e-Voucher";

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
    private static final String ACCOUNT = "ACCOUNT";

    /**
     * Cache Key [QUICK_RESPONSE_CODE_LOGIN_MARK]
     */
    private static final String QUICK_RESPONSE_CODE_LOGIN_MARK = "QUICK_RESPONSE_CODE_LOGIN_MARK";

    /**
     * Cache Key [ACCOUNT_PASSWORD_SIGNATURE_MARK]
     */
    private static final String ACCOUNT_PASSWORD_SIGNATURE_MARK = "ACCOUNT_PASSWORD_SIGNATURE_MARK";

    /**
     * Mark
     */
    private String mark;

    /**
     * Server Http Request Attributes Object
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    /**
     * Constructor Initialization
     *
     * @param delegate Server Http Request Object (org.springframework.http.server.reactive.ServerHttpRequest)
     */
    public ServerHttpRequest(org.springframework.http.server.reactive.ServerHttpRequest delegate) {
        super(delegate);
    }

    /**
     * Get Voucher Data
     *
     * @param vouchers Voucher List Object
     * @return Map Data Object
     */
    private Mono<Map<String, String>> getVoucher(List<String> vouchers) {
        if (vouchers == null || vouchers.isEmpty()) {
            return Mono.empty();
        }
        final String voucher = vouchers.remove(0);
        System.out.println("1111111111111111");
        System.out.println(
                SpringUtil
                        .getBean(VoucherCache.class)
        );
        System.out.println("22222222222222");
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
     * @return Server Http Request Object
     */
    public Mono<ServerHttpRequest> init() {
        final List<String> vouchers = new ArrayList<>();
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
        vouchers.add("123456");
        if (vouchers.isEmpty()) {
            return Mono.error(GlobalExceptionContext.executeVoucherException(
                    this.getClass(),
                    "fun init().",
                    "request voucher does not exist."
            ));
        } else {
            return getVoucher(vouchers)
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeVoucherException(
                            this.getClass(),
                            "fun init().",
                            "request voucher does not exist or has expired."
                    )))
                    .map(m -> {
                        m.forEach(this::setAttribute);
                        return this;
                    });
        }
    }

    /**
     * Cache Voucher
     *
     * @return Server Http Request Object
     */
    public Mono<ServerHttpRequest> save() {
        final Map<String, String> content = new HashMap<>();
        this.attributes.forEach((k, v) -> content.put(k, String.valueOf(v)));
        System.out.println("00000 >>> 0 " + this.attributes);
        return SpringUtil
                .getBean(VoucherCache.class)
                .set(this.mark, content)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun init().",
                        "request voucher cache exception."
                )))
                .map(b -> this);
    }

    /**
     * Delete Voucher
     *
     * @return Server Http Request Object
     */
    public Mono<ServerHttpRequest> remove() {
        return SpringUtil.getBean(VoucherCache.class).del(this.mark).map(b -> this);
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

    /**
     * Get Attribute
     *
     * @param key Key
     * @return Value
     */
    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    /**
     * Set Account
     *
     * @param account Account
     */
    public void setAccount(String account) {
        setAttribute(ACCOUNT, account);
    }

    /**
     * Get Account
     *
     * @return Account
     */
    public String getAccount() {
        final Object data = getAttribute(ACCOUNT);
        return data == null ? null : String.valueOf(data);
    }

    /**
     * Set Account Password Signature Mark
     *
     * @param mark Mark
     */
    public void setAccountPasswordSignatureMark(String mark) {
        setAttribute(ACCOUNT_PASSWORD_SIGNATURE_MARK, mark);
    }

    /**
     * Get Account Password Signature Mark
     *
     * @return Mark
     */
    public String getAccountPasswordSignatureMark() {
        final Object data = getAttribute(ACCOUNT_PASSWORD_SIGNATURE_MARK);
        return data == null ? null : String.valueOf(data);
    }

    /**
     * Set Quick Response Code Login Mark
     *
     * @param mark Mark
     */
    public void setQuickResponseCodeLoginMark(String mark) {
        setAttribute(QUICK_RESPONSE_CODE_LOGIN_MARK, mark);
    }

    /**
     * Get Quick Response Code Login Mark
     *
     * @return Mark
     */
    public String getQuickResponseCodeLoginMark() {
        final Object data = getAttribute(QUICK_RESPONSE_CODE_LOGIN_MARK);
        return data == null ? null : String.valueOf(data);
    }

    public String getDevice() {
        return "PC";
    }
}
