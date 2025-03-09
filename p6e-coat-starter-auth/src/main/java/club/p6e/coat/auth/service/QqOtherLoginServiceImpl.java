//package club.p6e.coat.auth.service;
//
//import club.p6e.coat.auth.cache.StateOtherLoginCache;
//import club.p6e.coat.auth.generator.StateOtherLoginGenerator;
//import club.p6e.coat.auth.repository.UserAuthRepository;
//import club.p6e.coat.auth.repository.UserRepository;
//import club.p6e.coat.common.utils.HttpUtil;
//import club.p6e.coat.common.utils.JsonUtil;
//import club.p6e.coat.common.utils.TemplateParser;
//import club.p6e.coat.auth.password.AuthPasswordEncryptor;
//import club.p6e.coat.auth.AuthUser;
//import club.p6e.coat.auth.AuthVoucher;
//import club.p6e.coat.auth.Properties;
//import club.p6e.coat.auth.cache.RegisterOtherLoginCache;
//import club.p6e.coat.auth.error.GlobalExceptionContext;
//import club.p6e.coat.auth.generator.RegisterOtherLoginGenerator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.Data;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Map;
//
//
///**
// * @author lidashuang
// * @version 1.0
// */
//public class QqOtherLoginServiceImpl implements QqOtherLoginService {
//
//    private static final String TYPE = "QQ";
//
//    private final AuthUser<?> au;
//    private final AuthPasswordEncryptor passwordEncryptor;
//    private final UserRepository userRepository;
//    private final UserAuthRepository userAuthRepository;
//    private final StateOtherLoginCache stateCache;
//    private final StateOtherLoginGenerator generator;
//    private final Properties.Login.Other other;
//
//    private final RegisterOtherLoginCache registerCache;
//
//    private final RegisterOtherLoginGenerator registerGenerator;
//
//    private final Properties properties;
//
//    public QqOtherLoginServiceImpl(AuthUser<?> au, Properties properties, StateOtherLoginCache stateCache, RegisterOtherLoginCache registerCache,
//                                   StateOtherLoginGenerator generator, AuthPasswordEncryptor passwordEncryptor, RegisterOtherLoginGenerator registerGenerator,
//                                   UserRepository userRepository, UserAuthRepository userAuthRepository) {
//        this.au = au;
//        this.properties = properties;
//        this.stateCache = stateCache;
//        this.registerCache = registerCache;
//        this.passwordEncryptor = passwordEncryptor;
//        this.generator = generator;
//        this.userRepository = userRepository;
//        this.registerGenerator = registerGenerator;
//        this.userAuthRepository = userAuthRepository;
//        this.other = properties.getLogin().getOthers().get(TYPE);
//    }
//
//    @Override
//    public Mono<String> home(ServerWebExchange exchange) {
//        return AuthVoucher
//                .init(exchange)
//                .flatMap(v -> {
//                    if (other != null) {
//                        final MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();
//                        final String display = params.getFirst("display");
//                        final String state = generator.execute();
//                        return stateCache
//                                .set(TYPE, state, v.getMark())
//                                .flatMap(b -> {
//                                    final Map<String, String> data = new HashMap<>(2);
//                                    data.put(AuthVoucher.OTHER_LOGIN_TYPE, TYPE);
//                                    data.put(AuthVoucher.OTHER_LOGIN_DATE, String.valueOf(System.currentTimeMillis()));
//                                    return v.set(data);
//                                })
//                                .map(vv -> {
//                                    String rt = TemplateParser.execute(
//                                            other.getConfig().get("home"), other.getConfig());
//                                    if (display != null) {
//                                        rt = rt + "&display=" + display;
//                                    }
//                                    return rt + "&state=" + state;
//                                });
//                    } else {
//                        return Mono.error(GlobalExceptionContext.exceptionConfigException(
//                                this.getClass(),
//                                "fun home(ServerWebExchange exchange)",
//                                "Reading of [ " + TYPE + " ] related configuration files error."
//                        ));
//                    }
//                });
//    }
//
//    @Override
//    public Mono<AuthUser.Model> callback(ServerWebExchange exchange) {
//        final MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();
//        final String code = params.getFirst("code");
//        final String state = params.getFirst("state");
//        if (code == null || state == null) {
//            return Mono.error(GlobalExceptionContext.executeParameterException(
//                    this.getClass(),
//                    "fun callback(ServerWebExchange exchange)",
//                    "[ " + TYPE + " ] callback code: " + code + " state: " + state + " parameter exception."
//            ));
//        }
//        return stateCache
//                .get(TYPE, state)
//                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionStateOtherLoginException(
//                        this.getClass(),
//                        "fun callback(ServerWebExchange exchange)",
//                        "[ " + TYPE + " ] state ==> " + state + " expired or not exist !!"
//                )))
//                .flatMap(AuthVoucher::init)
//                .flatMap(v -> doToken(code)
//                        .flatMap(tm -> doMe(tm.getAccessToken())
//                                .flatMap(mm -> select(passwordEncryptor.execute(mm.getOpenId()))
//                                        .switchIfEmpty(
//                                                properties.getRegister().isEnable() ?
//                                                        Mono.defer(() -> doInfo(tm.getAccessToken(), mm.getOpenId())
//                                                                .flatMap(im -> {
//                                                                    final String m = registerGenerator.execute();
//                                                                    return registerCache
//                                                                            .set(m, JsonUtil.toJson(im))
//                                                                            .flatMap(b -> Mono.error(
//                                                                                    GlobalExceptionContext.exceptionRegisterOtherLoginException(
//                                                                                            this.getClass(),
//                                                                                            "fun callback(ServerWebExchange exchange)",
//                                                                                            m
//                                                                                    ))
//                                                                            );
//                                                                })) :
//                                                        Mono.error(GlobalExceptionContext.exceptionOtherLoginException(
//                                                                this.getClass(),
//                                                                "fun callback(ServerWebExchange exchange)",
//                                                                "account is not bound to an exception."
//                                                        )))
//                                )));
//    }
//
//
//    private Mono<AuthUser.Model> select(String qq) {
//        System.out.println("QQ >>>>>>>> " + qq);
//        return userAuthRepository
//                .findByQq(qq)
//                .flatMap(uam -> userRepository
//                        .findById(uam.getId())
//                        .map(um -> au.create(um, uam)));
//    }
//
//    private Mono<TokenResultModel> doToken(String code) {
//        final String url = TemplateParser.execute(
//                other.getConfig().get("token"), other.getConfig()) + "&fmt=json&code=" + code;
//        return HttpUtil
//                .doGet(url)
//                .flatMap(s -> {
//                    final TokenResultModel m = JsonUtil.fromJson(s, TokenResultModel.class);
//                    System.out.println(m);
//                    return m == null || m.getAccessToken() == null ? Mono.empty() : Mono.just(m);
//                })
//                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionOtherLoginException(
//                        this.getClass(),
//                        "fun doToken(String code)",
//                        "[ QQ ] HTTP >>> " + url + " result error !!"
//                )));
//    }
//
//    private Mono<MeResultModel> doMe(String token) {
//        final String url = other.getConfig().get("me") + "?fmt=json&access_token=" + token;
//        return HttpUtil
//                .doGet(url)
//                .flatMap(s -> {
//                    final MeResultModel m = JsonUtil.fromJson(s, MeResultModel.class);
//                    System.out.println(m);
//                    return m == null || m.getOpenId() == null ? Mono.empty() : Mono.just(m);
//                })
//                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionOtherLoginException(
//                        this.getClass(),
//                        "fun doToken(String code)",
//                        "[ QQ ] HTTP >>> " + url + " result error !!"
//                )));
//    }
//
//    private Mono<InfoResultModel> doInfo(String token, String openId) {
//        final String url = TemplateParser.execute(other.getConfig().get("info"),
//                other.getConfig()) + "&access_token=" + token + "&openid=" + openId;
//        return HttpUtil
//                .doGet(url)
//                .flatMap(s -> {
//                    final InfoResultModel m = JsonUtil.fromJson(s, InfoResultModel.class);
//                    System.out.println(m);
//                    return m == null || m.getRet() == null || m.getRet() < 0 ? Mono.empty() : Mono.just(m);
//                })
//                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionOtherLoginException(
//                        this.getClass(),
//                        "fun doToken(String code)",
//                        "[ QQ ] HTTP >>> " + url + " result error !!"
//                )));
//    }
//
//    @Data
//    private static class TokenResultModel implements Serializable {
//        @JsonProperty("access_token")
//        private String accessToken;
//        @JsonProperty("refresh_token")
//        private String refreshToken;
//        @JsonProperty("expires_in")
//        private long expiresIn;
//    }
//
//    @Data
//    private static class MeResultModel implements Serializable {
//        @JsonProperty("client_id")
//        private String clientId;
//        @JsonProperty("openid")
//        private String openId;
//    }
//
//    @Data
//    private static class InfoResultModel implements Serializable {
//        @JsonProperty("ret")
//        private Integer ret;
//        @JsonProperty("msg")
//        private String msg;
//        @JsonProperty("nickname")
//        private String nickname;
//        @JsonProperty("figureurl_qq")
//        private String avatar;
//        @JsonProperty("gender")
//        private String gender;
//        @JsonProperty("gender_type")
//        private String gender_type;
//        @JsonProperty("province")
//        private String province;
//        @JsonProperty("city")
//        private String city;
//    }
//
//}
