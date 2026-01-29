package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.oauth2.cache.BlockingAuthClientCache;
import club.p6e.coat.auth.oauth2.cache.BlockingAuthUserCache;
import club.p6e.coat.auth.oauth2.cache.BlockingCodeCache;
import club.p6e.coat.auth.oauth2.context.TokenContext;
import club.p6e.coat.auth.oauth2.model.ClientModel;
import club.p6e.coat.auth.oauth2.repository.BlockingClientRepository;
import club.p6e.coat.auth.oauth2.validator.BlockingRequestParameterValidator;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.error.OAuth2ClientException;
import club.p6e.coat.common.error.OAuth2ParameterException;
import club.p6e.coat.common.error.OAuth2ScopeException;
import club.p6e.coat.common.error.OAuth2UsernamePasswordException;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Blocking Token Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingTokenService.class,
        ignored = BlockingTokenServiceImpl.class
)
@Component("club.p6e.coat.auth.oauth2.service.BlockingTokenServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingTokenServiceImpl implements BlockingTokenService {

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
     * Blocking Code Cache Object
     */
    private final BlockingCodeCache codeCache;

    /**
     * Blocking User Auth User Cache Object
     */
    private final BlockingAuthUserCache authUserCache;

    /**
     * Blocking User Auth Client Cache Object
     */
    private final BlockingAuthClientCache authClientCache;

    /**
     * Blocking User Repository Object
     */
    private final BlockingUserRepository userRepository;

    /**
     * Blocking Client Repository Object
     */
    private final BlockingClientRepository clientRepository;

    /**
     * Constructor Initialization
     *
     * @param builder          User Builder Object
     * @param codeCache        Blocking Code Cache Object
     * @param authUserCache    Blocking Auth User Cache Object
     * @param authClientCache  Blocking Auth Client Cache Object
     * @param userRepository   Blocking User Repository Object
     * @param clientRepository Blocking Client Repository Object
     *
     */
    public BlockingTokenServiceImpl(
            UserBuilder builder,
            BlockingCodeCache codeCache,
            BlockingAuthUserCache authUserCache,
            BlockingAuthClientCache authClientCache,
            BlockingUserRepository userRepository,
            BlockingClientRepository clientRepository
    ) {
        this.builder = builder;
        this.codeCache = codeCache;
        this.authUserCache = authUserCache;
        this.authClientCache = authClientCache;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public Map<String, Object> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, TokenContext.Request request) {
        final TokenContext.Request content = BlockingRequestParameterValidator.run(httpServletRequest, httpServletResponse, request);
        final String grantType = content.getGrantType();
        if (PASSWORD_MODE.equalsIgnoreCase(grantType)) {
            return executePasswordMode(content);
        } else if (AUTHORIZATION_CODE_MODE.equalsIgnoreCase(grantType)) {
            return executeAuthorizationCodeMode(content);
        } else if (CLIENT_CREDENTIALS_MODE.equalsIgnoreCase(grantType)) {
            return executeClientCredentialsMode(content);
        } else {
            throw new OAuth2ParameterException(
                    this.getClass(),
                    "fun Map<String, Object> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, TokenContext.Request request)",
                    "request parameter grant_type<" + grantType + "> not support"
            );
        }
    }

    /**
     * Execute Password Mode
     *
     * @param request Token Context Request Object
     * @return Map<String, Object> Map Result Object
     */
    public Map<String, Object> executePasswordMode(TokenContext.Request request) {
        final String username = request.getUsername();
        final String password = request.getPassword();
        final String clientId = request.getClientId();
        final String clientSecret = request.getClientSecret();
        final ClientModel client = clientRepository.findByAppId(clientId);
        if (client == null || client.getAppSecret().equals(clientSecret)) {
            throw new OAuth2ClientException(
                    this.getClass(),
                    "fun Map<String, Object> executePasswordMode(TokenContext.Request request)",
                    "[" + PASSWORD_MODE + "] client_id<" + clientId + "> or client_secret<" + clientSecret + "> not match"
            );
        }
        final User user = switch (Properties.getInstance().getMode()) {
            case PHONE -> userRepository.findByPhone(username);
            case MAILBOX -> userRepository.findByMailbox(username);
            case ACCOUNT -> userRepository.findByAccount(username);
            case PHONE_OR_MAILBOX -> userRepository.findByPhoneOrMailbox(username);
        };
        if (user == null || !user.password().equals(password)) {
            throw new OAuth2UsernamePasswordException(
                    this.getClass(),
                    "fun Map<String, Object> executePasswordMode(TokenContext.Request request)",
                    "[" + PASSWORD_MODE + "] username<" + username + "> or password<" + password + "> not match"
            );
        } else {
            final String token = GeneratorUtil.uuid() + GeneratorUtil.random();
            authUserCache.set(user.id(), token, "*", user.serialize(), 3600L);
            final String oid = DigestUtils.md5DigestAsHex((client.getAppId() + "@" + user.id()).getBytes());
            return new HashMap<>() {{
                put("oid", oid);
                put("token", token);
                put("user", user.serialize());
                put("scope", "*");
                put("expiration", 3600L);
                put("type", "Bearer");
            }};
        }
    }

    /**
     * Execute Password Mode
     *
     * @param request Token Context Request Object
     * @return Map<String, Object> Map Result Object
     */
    public Map<String, Object> executeAuthorizationCodeMode(TokenContext.Request request) {
        final String code = request.getCode();
        final String clientId = request.getClientId();
        final String redirectUri = request.getRedirectUri();
        final String clientSecret = request.getClientSecret();
        final Map<String, String> content = codeCache.get(code);
        if (content == null) {
            throw new OAuth2ParameterException(
                    this.getClass(),
                    "fun Map<String, Object> executeAuthorizationCodeMode(TokenContext.Request request)",
                    "[" + AUTHORIZATION_CODE_MODE + "] code<" + code + "> does not exist or has expired"
            );
        } else {
            final String cacheUser = content.get("user");
            final String cacheScope = content.get("scope");
            final String cacheClientId = content.get("client_id");
            final String cacheRedirectUri = content.get("redirect_uri");
            if (!cacheClientId.equals(clientId)) {
                throw new OAuth2ParameterException(
                        this.getClass(),
                        "fun Map<String, Object> executeAuthorizationCodeMode(TokenContext.Request request)",
                        "[" + AUTHORIZATION_CODE_MODE + "] client_id<" + clientId + "> not match"
                );
            }
            if (!cacheRedirectUri.equals(redirectUri)) {
                throw new OAuth2ParameterException(
                        this.getClass(),
                        "fun Map<String, Object> executeAuthorizationCodeMode(TokenContext.Request request)",
                        "[" + AUTHORIZATION_CODE_MODE + "] redirect_uri<" + redirectUri + "> not match"
                );
            }
            final ClientModel client = clientRepository.findByAppId(clientId);
            if (client == null || client.getAppSecret().equals(clientSecret)) {
                throw new OAuth2ClientException(
                        this.getClass(),
                        "fun Map<String, Object> executeAuthorizationCodeMode(TokenContext.Request request)",
                        "[" + AUTHORIZATION_CODE_MODE + "] client_secret<" + clientSecret + "> not match"
                );
            }
            final User user = builder.create(cacheUser);
            final String token = GeneratorUtil.uuid() + GeneratorUtil.random();
            authUserCache.set(user.id(), token, cacheScope, cacheUser, 3600L);
            final String oid = DigestUtils.md5DigestAsHex((client.getAppId() + "@" + user.id()).getBytes());
            return new HashMap<>() {{
                put("oid", oid);
                put("token", token);
                put("user", cacheUser);
                put("scope", cacheScope);
                put("expiration", 3600L);
                put("type", "Bearer");
            }};
        }
    }

    /**
     * Execute Client Credentials Mode
     *
     * @param request Token Context Request Object
     * @return Map<String, Object> Map Result Object
     */
    public Map<String, Object> executeClientCredentialsMode(TokenContext.Request request) {
        String scope = request.getScope();
        final String clientId = request.getClientId();
        final String clientSecret = request.getClientSecret();
        final ClientModel client = clientRepository.findByAppId(clientId);
        if (client == null || client.getAppSecret().equals(clientSecret)) {
            throw new OAuth2ClientException(
                    this.getClass(),
                    "fun Map<String, Object> executeClientCredentialsMode(TokenContext.Request request)",
                    "[" + CLIENT_CREDENTIALS_MODE + "] client_id<" + clientId + "> or client_secret<" + clientSecret + "> not match"
            );
        }
        scope = scope == null || scope.isEmpty() ? client.getScope() : scope;
        if (!VerificationUtil.validationOAuth2Scope(client.getScope(), scope)) {
            throw new OAuth2ScopeException(
                    this.getClass(),
                    "fun Map<String, Object> executeClientCredentialsMode(TokenContext.Request request)",
                    "[" + CLIENT_CREDENTIALS_MODE + "] scope<" + scope + "> not match"
            );
        }
        final String fs = scope;
        final String token = GeneratorUtil.uuid() + GeneratorUtil.random();
        authClientCache.set(clientId, token, scope, JsonUtil.toJson(client), 3600L);
        return new HashMap<>() {{
            put("scope", fs);
            put("token", token);
            put("type", "Bearer");
            put("expiration", 3600L);
        }};
    }

}
