package club.p6e.coat.auth;

import club.p6e.coat.auth.cache.VoucherCache;
import club.p6e.coat.auth.generator.VoucherGenerator;
import club.p6e.coat.auth.model.OAuth2ClientModel;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.controller.BaseWebFluxController;
import club.p6e.coat.common.utils.SpringUtil;
import lombok.Getter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public class AuthVoucher implements Serializable {

    @SuppressWarnings("ALL")
    public static final String IP = "IP";
    @SuppressWarnings("ALL")
    public static final String DEVICE = "DEVICE";

    public static final String INDEX = "INDEX";
    public static final String INDEX_DATE = "INDEX_DATE";
    public static final String REGISTER = "REGISTER";
    public static final String REGISTER_DATE = "REGISTER_DATE";

    public static final String ACCOUNT_PASSWORD_CODEC_MARK = "ACCOUNT_PASSWORD_CODEC_MARK";
    public static final String ACCOUNT_PASSWORD_CODEC_DATE = "ACCOUNT_PASSWORD_CODEC_DATE";
    public static final String ACCOUNT = "ACCOUNT";
    public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String VERIFICATION_CODE_LOGIN_MARK = "VERIFICATION_CODE_LOGIN_MARK";
    public static final String VERIFICATION_CODE_LOGIN_DATE = "VERIFICATION_CODE_LOGIN_DATE";
    public static final String QUICK_RESPONSE_CODE_LOGIN_MARK = "QUICK_RESPONSE_CODE_LOGIN_MARK";
    public static final String QUICK_RESPONSE_CODE_LOGIN_DATE = "QUICK_RESPONSE_CODE_LOGIN_DATE";

    public static final String OAUTH2 = "OAUTH2";
    public static final String OAUTH2_DATE = "OAUTH2_DATE";
    public static final String OAUTH2_STATE = "OAUTH2_STATE";
    public static final String OAUTH2_SCOPE = "OAUTH2_SCOPE";
    public static final String OAUTH2_CLIENT_ID = "OAUTH2_CLIENT_ID";
    public static final String OAUTH2_REDIRECT_URI = "OAUTH2_REDIRECT_URI";
    public static final String OAUTH2_RESPONSE_TYPE = "OAUTH2_RESPONSE_TYPE";
    public static final String OAUTH2_CLIENT_RECONFIRM = "OAUTH2_CLIENT_RECONFIRM";
    public static final String OAUTH2_CLIENT_NAME = "OAUTH2_CLIENT_NAME";
    public static final String OAUTH2_CLIENT_AVATAR = "OAUTH2_CLIENT_AVATAR";
    public static final String OAUTH2_CLIENT_DESCRIBE = "OAUTH2_CLIENT_DESCRIBE";
    public static final String OAUTH2_USER_ID = "OAUTH2_USER_ID";
    public static final String OAUTH2_USER_INFO = "OAUTH2_USER_INFO";

    private static final String VOUCHER_HEADER_NAME = "X-Voucher";
    private static final String VOUCHER_URL_PARAM_NAME = "voucher";

    /**
     * 标记内容
     */
    @Getter
    private final String mark;

    /**
     * 凭证缓存对象
     */
    private final VoucherCache cache;

    /**
     * 数据对象
     */
    @Getter
    private final Map<String, String> data;

    /**
     * 初始化认证凭证上下文对象
     *
     * @param exchange 服务器交换对象
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public static Mono<AuthVoucher> init(ServerWebExchange exchange) {
        final String voucher;
        final ServerHttpRequest request = exchange.getRequest();
        final VoucherCache cache = SpringUtil.getBean(VoucherCache.class);
        final String param = BaseWebFluxController.getParam(request, VOUCHER_URL_PARAM_NAME);
        if (param == null) {
            voucher = BaseWebFluxController.getHeader(request, VOUCHER_HEADER_NAME);
        } else {
            voucher = param;
        }
        if (StringUtils.hasText(voucher)) {
            return cache
                    .get(voucher)
                    .map(m -> new AuthVoucher(voucher, m, cache))
                    .switchIfEmpty(Mono.error(GlobalExceptionContext.executeVoucherException(
                            AuthVoucher.class,
                            "fun init(ServerWebExchange exchange)",
                            "Voucher request parameter does not data or expired exception."
                    )));
        } else {
            return Mono.error(GlobalExceptionContext.executeVoucherException(
                    AuthVoucher.class,
                    "fun init(ServerWebExchange exchange)",
                    "Voucher request parameter does not exist exception."
            ));
        }
    }

    /**
     * 创建主页凭证对象
     *
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    @SuppressWarnings("ALL")
    public static Mono<AuthVoucher> createIndex(ServerWebExchange exchange) {
        String device = "PC";
        try {
            final String content = exchange.getRequest().getHeaders().getFirst("X-Android");
            if (content != null && !content.isEmpty()) {
                device = "ANDROID";
            }
        } catch (Exception e) {
            device = "PC";
        }
        final String finalDevice = device;
        return create(new HashMap<>() {{
            put(AuthVoucher.INDEX, "true");
            put(AuthVoucher.DEVICE, finalDevice);
            put(AuthVoucher.INDEX_DATE, String.valueOf(System.currentTimeMillis()));
        }});
    }

    /**
     * 创建 OAUTH2 主页凭证对象
     *
     * @param ocm         凭证客户端模型
     * @param type        类型
     * @param redirectUri 重定向
     * @param scope       作用域
     * @param state       状态标记
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public static Mono<AuthVoucher> createOAuth2Index(
            OAuth2ClientModel ocm,
            String type,
            String redirectUri,
            String scope,
            String state
    ) {
        return create(new HashMap<>() {{
            put(AuthVoucher.OAUTH2, "true");
            put(AuthVoucher.OAUTH2_DATE, String.valueOf(System.currentTimeMillis()));
            put(AuthVoucher.OAUTH2_SCOPE, scope);
            put(AuthVoucher.OAUTH2_RESPONSE_TYPE, type);
            put(AuthVoucher.OAUTH2_REDIRECT_URI, redirectUri);
            put(AuthVoucher.OAUTH2_CLIENT_ID, ocm.getClientId());
            put(AuthVoucher.OAUTH2_CLIENT_NAME, ocm.getClientName());
            put(AuthVoucher.OAUTH2_CLIENT_AVATAR, ocm.getClientAvatar());
            put(AuthVoucher.OAUTH2_CLIENT_DESCRIBE, ocm.getClientDescription());
            put(AuthVoucher.OAUTH2_CLIENT_RECONFIRM, String.valueOf(ocm.getReconfirm()));
            if (state != null) {
                put(AuthVoucher.OAUTH2_STATE, state);
            }
        }});
    }

    /**
     * 创建认证凭证上下文对象
     *
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public static Mono<AuthVoucher> create(Map<String, String> data) {
        final VoucherCache cache = SpringUtil.getBean(VoucherCache.class);
        final String voucher = SpringUtil.getBean(VoucherGenerator.class).execute();
        return cache
                .set(voucher, data)
                .flatMap(b -> b ? Mono.just(new AuthVoucher(voucher, data, cache)) : Mono.error(
                        GlobalExceptionContext.executeVoucherException(
                                AuthVoucher.class,
                                "fun create(Map<String, String> data).",
                                "Voucher create data cache exception."
                        )));
    }

    /**
     * 构造函数初始化
     *
     * @param mark  标记内容
     * @param data  数据内容
     * @param cache 缓存对象
     */
    private AuthVoucher(String mark, Map<String, String> data, VoucherCache cache) {
        this.mark = mark;
        this.cache = cache;
        this.data = new HashMap<>();
        if (data != null && !data.isEmpty()) {
            for (final String key : data.keySet()) {
                this.data.put(key, data.get(key));
            }
        }
    }

    /**
     * 读取账号数据
     *
     * @return 账号数据
     */
    public String getAccount() {
        return this.get(ACCOUNT);
    }

    /**
     * 写入账号数据
     *
     * @param account 账号数据
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public Mono<AuthVoucher> setAccount(String account) {
        final Map<String, String> map = new HashMap<>();
        map.put(ACCOUNT, account);
        return this.set(map);
    }

    /**
     * 读取账号类型数据
     *
     * @return 账号类型数据
     */
    public String getAccountType() {
        return this.get(ACCOUNT_TYPE);
    }

    /**
     * 设置 OAuth2 用户信息
     *
     * @param uid  用户 ID
     * @param info 用户信息
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public Mono<AuthVoucher> setOAuth2User(String uid, String info) {
        final Map<String, String> map = new HashMap<>();
        map.put(AuthVoucher.OAUTH2_USER_ID, uid);
        map.put(AuthVoucher.OAUTH2_USER_INFO, info);
        return this.set(map);
    }

    /**
     * 读取账号密码登录密码编码器标记
     *
     * @return 账号密码登录密码编码器标记
     */
    public String getAccountPasswordCodecMark() {
        return get(AuthVoucher.ACCOUNT_PASSWORD_CODEC_MARK);
    }

    /**
     * 设置账号密码登录密码编码器标记
     *
     * @param mark 编码器标记
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public Mono<AuthVoucher> setAccountPasswordCodecMark(String mark) {
        this.data.put(AuthVoucher.ACCOUNT_PASSWORD_CODEC_MARK, mark);
        this.data.put(AuthVoucher.ACCOUNT_PASSWORD_CODEC_DATE, String.valueOf(System.currentTimeMillis()));
        return set(this.data);
    }

    /**
     * 写入验证码登录数据
     *
     * @param account     账号数据
     * @param accountType 账号类型
     * @param mark        结果标记
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public Mono<AuthVoucher> setVerificationCodeData(String account, String accountType, String mark) {
        this.data.put(AuthVoucher.ACCOUNT, account);
        this.data.put(AuthVoucher.ACCOUNT_TYPE, accountType);
        this.data.put(AuthVoucher.VERIFICATION_CODE_LOGIN_MARK, mark);
        this.data.put(AuthVoucher.VERIFICATION_CODE_LOGIN_DATE, String.valueOf(System.currentTimeMillis()));
        return set(this.data);
    }

    /**
     * 读取二维码登录数据
     *
     * @return 二维码登录数据
     */
    public String getQuickResponseCodeLoginMark() {
        return get(AuthVoucher.QUICK_RESPONSE_CODE_LOGIN_MARK);
    }

    /**
     * 写入二维码登录数据
     *
     * @param code 账号数据
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public Mono<AuthVoucher> setQuickResponseCodeLoginData(String code) {
        this.data.put(AuthVoucher.QUICK_RESPONSE_CODE_LOGIN_MARK, code);
        this.data.put(AuthVoucher.QUICK_RESPONSE_CODE_LOGIN_DATE, String.valueOf(System.currentTimeMillis()));
        return set(this.data);
    }

    /**
     * 判断是否包含 OAuth2 认证
     *
     * @return OAuth2 是否包含结果
     */
    public boolean isOAuth2() {
        return StringUtils.hasText(this.get(AuthVoucher.OAUTH2));
    }

    /**
     * 判断 OAuth2 是否完成
     *
     * @return OAuth2 是否完成结果
     */
    public boolean isOAuth2Complete() {
        return StringUtils.hasText(this.get(AuthVoucher.OAUTH2));
    }

    /**
     * 获取 OAuth2 数据
     *
     * @return OAuth2 数据
     */
    public Map<String, Object> getOAuth2() {
        final String clientId = this.get(AuthVoucher.OAUTH2_CLIENT_ID);
        final String clientName = this.get(AuthVoucher.OAUTH2_CLIENT_NAME);
        final String clientAvatar = this.get(AuthVoucher.OAUTH2_CLIENT_AVATAR);
        final String clientDescribe = this.get(AuthVoucher.OAUTH2_CLIENT_DESCRIBE);
        final String clientReconfirm = this.get(AuthVoucher.OAUTH2_CLIENT_RECONFIRM);
        final Map<String, Object> client = new HashMap<>(5);
        client.put("clientId", clientId);
        client.put("clientName", clientName);
        client.put("clientAvatar", clientAvatar);
        client.put("clientDescribe", clientDescribe);
        client.put("clientReconfirm", clientReconfirm);
        return client;
    }

    /**
     * 获取属性值
     *
     * @param key 属性键
     * @return 属性值
     */
    public String get(String key) {
        return data.get(key);
    }

    /**
     * 写入数据
     *
     * @param map 数据对象
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public Mono<AuthVoucher> set(Map<String, String> map) {
        return cache
                .set(mark, map)
                .map(b -> {
                    if (b) {
                        for (final String key : map.keySet()) {
                            data.put(key, map.get(key));
                        }
                    }
                    return this;
                });
    }

    /**
     * 删除
     *
     * @return Mono<AuthVoucherContext> 认证凭证上下文对象
     */
    public Mono<AuthVoucher> del() {
        return cache.del(mark).map(b -> this);
    }

    /**
     * 设备信息
     *
     * @return 设备信息数据
     */
    public String device() {
        return this.get(DEVICE);
    }

}
