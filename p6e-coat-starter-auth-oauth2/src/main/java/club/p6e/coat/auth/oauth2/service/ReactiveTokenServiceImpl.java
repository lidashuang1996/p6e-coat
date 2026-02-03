package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.oauth2.cache.ReactiveAuthClientCache;
import club.p6e.coat.auth.oauth2.cache.ReactiveAuthUserCache;
import club.p6e.coat.auth.oauth2.cache.ReactiveCodeCache;
import club.p6e.coat.auth.oauth2.context.TokenContext;
import club.p6e.coat.auth.oauth2.repository.ReactiveClientRepository;
import club.p6e.coat.auth.oauth2.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.auth.repository.ReactiveUserRepository;
import club.p6e.coat.common.error.*;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Reactive Token Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveTokenService.class,
        ignored = ReactiveTokenServiceImpl.class
)
@Component("club.p6e.coat.auth.oauth2.service.ReactiveTokenServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveTokenServiceImpl implements ReactiveTokenService {

    /**
     * PASSWORD MODE
     */
    private static final String PASSWORD_MODE = "password";

    /**
     * CLIENT CREDENTIALS MODE
     */
    private static final String CLIENT_CREDENTIALS_MODE = "client_credentials";

    /**
     * AUTHORIZATION CODE MODE
     */
    private static final String AUTHORIZATION_CODE_MODE = "authorization_code";

    /**
     * User Builder Object
     */
    private final UserBuilder builder;

    /**
     * Reactive Code Cache Object
     */
    private final ReactiveCodeCache codeCache;

    /**
     * Reactive User Auth User Cache Object
     */
    private final ReactiveAuthUserCache authUserCache;

    /**
     * Reactive User Auth Client Cache Object
     */
    private final ReactiveAuthClientCache authClientCache;

    /**
     * Reactive User Repository Object
     */
    private final ReactiveUserRepository userRepository;

    /**
     * Reactive Client Repository Object
     */
    private final ReactiveClientRepository clientRepository;

    /**
     * Constructor Initialization
     *
     * @param builder          User Builder Object
     * @param codeCache        Reactive Code Cache Object
     * @param authUserCache    Reactive Auth User Cache Object
     * @param authClientCache  Reactive Auth Client Cache Object
     * @param userRepository   Reactive User Repository Object
     * @param clientRepository Reactive Client Repository Object
     */
    public ReactiveTokenServiceImpl(
            UserBuilder builder,
            ReactiveCodeCache codeCache,
            ReactiveAuthUserCache authUserCache,
            ReactiveAuthClientCache authClientCache,
            ReactiveUserRepository userRepository,
            ReactiveClientRepository clientRepository
    ) {
        this.builder = builder;
        this.codeCache = codeCache;
        this.authUserCache = authUserCache;
        this.authClientCache = authClientCache;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Mono<Map<String, Object>> execute(ServerWebExchange exchange, TokenContext.Request request) {
        return ReactiveRequestParameterValidator
                .run(exchange, request)
                .flatMap(content -> {
                    final String grantType = content.getGrantType();
                    if (PASSWORD_MODE.equalsIgnoreCase(grantType)) {
                        return executePasswordMode(content);
                    } else if (AUTHORIZATION_CODE_MODE.equalsIgnoreCase(grantType)) {
                        return executeAuthorizationCodeMode(content);
                    } else if (CLIENT_CREDENTIALS_MODE.equalsIgnoreCase(grantType)) {
                        return executeClientCredentialsMode(content);
                    } else {
                        return Mono.error(new OAuth2ParameterException(
                                this.getClass(),
                                "fun Map<String, Object> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, TokenContext.Request request)",
                                "request parameter grant_type<" + grantType + "> not support"
                        ));
                    }
                });
    }

    /**
     * Execute Password Mode
     *
     * @param request Token Context Request Object
     * @return Map<String, Object> Map Result Object
     */
    public Mono<Map<String, Object>> executePasswordMode(TokenContext.Request request) {
        final String username = request.getUsername();
        final String password = request.getPassword();
        final String clientId = request.getClientId();
        final String clientSecret = request.getClientSecret();
        return clientRepository.findByAppId(clientId)
                .filter(client -> client.getClientSecret().equals(clientSecret))
                .switchIfEmpty(Mono.error(new OAuth2ClientException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> executePasswordMode(TokenContext.Request request)",
                        "[" + PASSWORD_MODE + "] client_id<" + clientId + "> does not exist"
                )))
                .filter(client -> client.getEnable() != 1)
                .switchIfEmpty(Mono.error(new OAuth2ClientException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> executePasswordMode(TokenContext.Request request)",
                        "[" + PASSWORD_MODE + "] client not enabled"
                )))
                .flatMap(client -> {
                    if (!VerificationUtil.validateOAuth2Type(client.getType(), PASSWORD_MODE)) {
                        return Mono.error(new OAuth2ClientException(
                                this.getClass(),
                                "fun Mono<Map<String, Object>> executePasswordMode(TokenContext.Request request)",
                                "[" + PASSWORD_MODE + "] client type<" + PASSWORD_MODE + "> not support"
                        ));
                    }
                    final Mono<User> um = switch (Properties.getInstance().getMode()) {
                        case PHONE -> userRepository.findByPhone(username);
                        case MAILBOX -> userRepository.findByMailbox(username);
                        case ACCOUNT -> userRepository.findByAccount(username);
                        case PHONE_OR_MAILBOX -> userRepository.findByPhoneOrMailbox(username);
                    };
                    return um.filter(user -> user.password().equals(password))
                            .switchIfEmpty(Mono.error(new OAuth2UsernamePasswordException(
                                    this.getClass(),
                                    "fun Mono<Map<String, Object>> executePasswordMode(TokenContext.Request request)",
                                    "[" + PASSWORD_MODE + "] username<" + username + "> or password<" + password + "> not match"
                            )))
                            .flatMap(user -> {
                                final String token = GeneratorUtil.uuid() + GeneratorUtil.random();
                                return authUserCache.set(user.id(), token, "*", user.serialize(), expiration())
                                        .switchIfEmpty(Mono.error(new CacheException(
                                                this.getClass(),
                                                "fun Mono<Map<String, Object>> executePasswordMode(TokenContext.Request request)",
                                                "[" + PASSWORD_MODE + "] auth cache error"
                                        )))
                                        .map(k -> {
                                            final String oid = DigestUtils.md5DigestAsHex((client.getClientId() + "@" + user.id()).getBytes());
                                            return new HashMap<>() {{
                                                put("oid", oid);
                                                put("token", token);
                                                put("user", user.serialize());
                                                put("scope", "*");
                                                put("type", "Bearer");
                                                put("expiration", expiration());
                                            }};
                                        });
                            });
                });
    }

    /**
     * Execute Password Mode
     *
     * @param request Token Context Request Object
     * @return Map<String, Object> Map Result Object
     */
    public Mono<Map<String, Object>> executeAuthorizationCodeMode(TokenContext.Request request) {
        final String code = request.getCode();
        final String clientId = request.getClientId();
        final String redirectUri = request.getRedirectUri();
        final String clientSecret = request.getClientSecret();
        return codeCache.get(code)
                .switchIfEmpty(Mono.error(new OAuth2ParameterException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> executeAuthorizationCodeMode(TokenContext.Request request)",
                        "[" + AUTHORIZATION_CODE_MODE + "] code<" + code + "> does not exist or has expired"
                )))
                .flatMap(content -> {
                    final String cacheUser = content.get("user");
                    final String cacheScope = content.get("scope");
                    final String cacheClientId = content.get("client_id");
                    final String cacheRedirectUri = content.get("redirect_uri");
                    if (!cacheClientId.equals(clientId)) {
                        return Mono.error(new OAuth2ParameterException(
                                this.getClass(),
                                "fun Mono<Map<String, Object>> executeAuthorizationCodeMode(TokenContext.Request request)",
                                "[" + AUTHORIZATION_CODE_MODE + "] client_id<" + clientId + "> not match"
                        ));
                    }
                    if (!cacheRedirectUri.equals(redirectUri)) {
                        return Mono.error(new OAuth2ParameterException(
                                this.getClass(),
                                "fun Mono<Map<String, Object>> executeAuthorizationCodeMode(TokenContext.Request request)",
                                "[" + AUTHORIZATION_CODE_MODE + "] redirect_uri<" + redirectUri + "> not match"
                        ));
                    }
                    return clientRepository.findByAppId(clientId)
                            .filter(client -> client.getClientSecret().equals(clientSecret))
                            .switchIfEmpty(Mono.error(new OAuth2ClientException(
                                    this.getClass(),
                                    "fun Mono<Map<String, Object>> executeAuthorizationCodeMode(TokenContext.Request request)",
                                    "[" + AUTHORIZATION_CODE_MODE + "] client_id<" + clientId + "> does not exist"
                            )))
                            .filter(client -> client.getEnable() != 1)
                            .switchIfEmpty(Mono.error(new OAuth2ClientException(
                                    this.getClass(),
                                    "fun Mono<Map<String, Object>> executeAuthorizationCodeMode(TokenContext.Request request)",
                                    "[" + AUTHORIZATION_CODE_MODE + "] client not enabled"
                            )))
                            .flatMap(client -> {
                                if (!VerificationUtil.validateOAuth2Type(client.getType(), AUTHORIZATION_CODE_MODE)) {
                                    return Mono.error(new OAuth2ClientException(
                                            this.getClass(),
                                            "fun Mono<Map<String, Object>> executeAuthorizationCodeMode(TokenContext.Request request)",
                                            "[" + AUTHORIZATION_CODE_MODE + "] client type<" + AUTHORIZATION_CODE_MODE + "> not support"
                                    ));
                                }
                                final User user = builder.create(cacheUser);
                                final String token = GeneratorUtil.uuid() + GeneratorUtil.random();
                                return authUserCache.set(user.id(), token, cacheScope, cacheUser, expiration())
                                        .switchIfEmpty(Mono.error(new CacheException(
                                                this.getClass(),
                                                "fun Mono<Map<String, Object>> executeAuthorizationCodeMode(TokenContext.Request request)",
                                                "[" + AUTHORIZATION_CODE_MODE + "] auth cache error"
                                        )))
                                        .map(K -> {
                                            final String oid = DigestUtils.md5DigestAsHex((client.getClientId() + "@" + user.id()).getBytes());
                                            return new HashMap<>() {{
                                                put("type", "Bearer");
                                                put("expiration", expiration());
                                                put("oid", oid);
                                                put("token", token);
                                                put("user", cacheUser);
                                                put("scope", cacheScope);
                                            }};
                                        });
                            });
                });
    }

    /**
     * Execute Client Credentials Mode
     *
     * @param request Token Context Request Object
     * @return Map<String, Object> Map Result Object
     */
    public Mono<Map<String, Object>> executeClientCredentialsMode(TokenContext.Request request) {
        final String scope = request.getScope();
        final String clientId = request.getClientId();
        final String clientSecret = request.getClientSecret();
        return clientRepository.findByAppId(clientId)
                .switchIfEmpty(Mono.error(new OAuth2ClientException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> executeClientCredentialsMode(TokenContext.Request request)",
                        "[" + CLIENT_CREDENTIALS_MODE + "] client_id<" + clientId + "> or client_secret<" + clientSecret + "> not match"
                )))
                .filter(client -> client.getClientSecret().equals(clientSecret))
                .switchIfEmpty(Mono.error(new OAuth2ClientException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> executeClientCredentialsMode(TokenContext.Request request)",
                        "[" + CLIENT_CREDENTIALS_MODE + "] client_id<" + clientId + "> or client_secret<" + clientSecret + "> not match"
                )))
                .filter(client -> client.getEnable() != 1)
                .switchIfEmpty(Mono.error(new OAuth2ClientException(
                        this.getClass(),
                        "fun Mono<Map<String, Object>> executeClientCredentialsMode(TokenContext.Request request)",
                        "[" + CLIENT_CREDENTIALS_MODE + "] client not enabled"
                )))
                .flatMap(client -> {
                    if (!VerificationUtil.validateOAuth2Type(client.getType(), CLIENT_CREDENTIALS_MODE)) {
                        return Mono.error(new OAuth2ClientException(
                                this.getClass(),
                                "fun Mono<Map<String, Object>> executeClientCredentialsMode(TokenContext.Request request)",
                                "[" + CLIENT_CREDENTIALS_MODE + "] client type<" + CLIENT_CREDENTIALS_MODE + "> not support"
                        ));
                    }
                    final String fs = scope == null || scope.isEmpty() ? client.getScope() : scope;
                    if (!VerificationUtil.validateOAuth2Scope(client.getScope(), fs)) {
                        return Mono.error(new OAuth2ScopeException(
                                this.getClass(),
                                "fun Mono<Map<String, Object>> executeClientCredentialsMode(TokenContext.Request request)",
                                "[" + CLIENT_CREDENTIALS_MODE + "] scope<" + scope + "> not match"
                        ));
                    }
                    final String token = GeneratorUtil.uuid() + GeneratorUtil.random();
                    return authClientCache
                            .set(clientId, token, scope, JsonUtil.toJson(client), expiration())
                            .switchIfEmpty(Mono.error(new CacheException(
                                    this.getClass(),
                                    "fun Mono<Map<String, Object>> executeClientCredentialsMode(TokenContext.Request request)",
                                    "[" + CLIENT_CREDENTIALS_MODE + "] auth cache error"
                            )))
                            .map(k -> new HashMap<>() {{
                                put("scope", fs);
                                put("token", token);
                                put("type", "Bearer");
                                put("expiration", expiration());
                            }});
                });
    }

    /**
     * Expiration Time
     *
     * @return Expiration Time
     */
    protected long expiration() {
        return 3600L;
    }

}
