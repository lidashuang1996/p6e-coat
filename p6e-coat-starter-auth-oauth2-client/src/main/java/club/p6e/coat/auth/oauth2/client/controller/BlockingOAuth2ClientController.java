package club.p6e.coat.auth.oauth2.client.controller;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.oauth2.client.Properties;
import club.p6e.coat.auth.oauth2.client.cache.BlockingOAuth2StateCache;
import club.p6e.coat.auth.token.BlockingTokenCleaner;
import club.p6e.coat.auth.token.BlockingTokenGenerator;
import club.p6e.coat.auth.token.BlockingTokenValidator;
import club.p6e.coat.common.context.ResultContext;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.exception.ParameterException;
import club.p6e.coat.common.exception.ResourceException;
import club.p6e.coat.common.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Blocking OAuth2 Client Controller
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
@ConditionalOnMissingBean(BlockingOAuth2ClientController.class)
@RestController("club.p6e.coat.auth.oauth2.client.controller.BlockingOAuth2ClientController")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingOAuth2ClientController {

    /**
     * User Builder Object
     */
    private final UserBuilder builder;

    /**
     * Blocking Token Generator Object
     */
    private final BlockingTokenGenerator generator;

    /**
     * Blocking Token Validator Object
     */
    private final BlockingTokenValidator validator;

    /**
     * Blocking Token Cleaner Object
     */
    private final BlockingTokenCleaner cleaner;

    /**
     * Blocking OAuth2 State Cache Object
     */
    private final BlockingOAuth2StateCache stateCache;

    /**
     * Constructor Initialization
     *
     * @param builder    User Builder Object
     * @param cleaner    Blocking Token Cleaner Object
     * @param generator  Blocking Token Generator Object
     * @param validator  Blocking Token Validator Object
     * @param stateCache Blocking OAuth2 State Cache Object
     */
    public BlockingOAuth2ClientController(
            UserBuilder builder,
            BlockingTokenCleaner cleaner,
            BlockingTokenGenerator generator,
            BlockingTokenValidator validator,
            BlockingOAuth2StateCache stateCache
    ) {
        this.builder = builder;
        this.cleaner = cleaner;
        this.generator = generator;
        this.validator = validator;
        this.stateCache = stateCache;
    }

    @RequestMapping("/sso/oauth2/auth")
    public void auth(HttpServletRequest request, HttpServletResponse response) {
        try {
            final Properties properties = Properties.getInstance();
            final Map<String, String> params = WebUtil.getRequestQueryParams(request);
            final String source = WebUtil.getParam(request, "source");
            final String redirectUri = WebUtil.getParam(request, "redirect_uri", "redirectUri");
            final String state = GeneratorUtil.random(8, true, false);
            stateCache.set(state, source == null ? "" : source);
            final StringBuilder extend = new StringBuilder();
            params.forEach((key, value) -> {
                if ("source".equalsIgnoreCase(key) || "redirect_uri".equalsIgnoreCase(key) || "redirectUri".equalsIgnoreCase(key)) {
                    extend.append("&").append(key).append("=").append(URLEncoder.encode(params.get(key), StandardCharsets.UTF_8));
                }
            });
            final String url = TemplateParser.execute(properties.getAuthorizeTemplate(), Map.of(
                    "STATE", state,
                    "URL", properties.getAuthorizeUrl(),
                    "SCOPE", properties.getAuthorizeScope(),
                    "APP_ID", properties.getAuthorizeAppId(),
                    "EXTEND", extend.isEmpty() ? "" : extend.toString(),
                    "URI", redirectUri == null ? properties.getAuthorizeAppRedirectUri() : redirectUri
            ));
            response.sendRedirect(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/sso/oauth2/auth/callback")
    public ResultContext callback(HttpServletRequest request, HttpServletResponse response) {
        final String code = WebUtil.getParam(request, "code");
        final String state = WebUtil.getParam(request, "state");
        if (code == null || state == null) {
            throw new ParameterException(
                    this.getClass(),
                    "fun ResultContext callback(HttpServletResponse response)",
                    "request parameter<code/state> does not exist exception"
            );
        } else {
            final String content = stateCache.get(state);
            if (content == null) {
                throw new CacheException(
                        this.getClass(),
                        "fun ResultContext callback(HttpServletResponse response)",
                        "request parameter cache data does no exist exception"
                );
            } else {
                try {
                    final Properties properties = Properties.getInstance();
                    final String result = HttpUtil.doPost(TemplateParser.execute(properties.getAuthorizeTokenUrl(), Map.of(
                            "code", code,
                            "grant_type", "authorization_code",
                            "client_id", properties.getAuthorizeAppId(),
                            "client_secret", properties.getAuthorizeAppSecret(),
                            "redirect_uri", properties.getAuthorizeAppRedirectUri()
                    )));
                    final AuthCallbackModel acm = JsonUtil.fromJson(result, AuthCallbackModel.class);
                    if (acm == null || acm.getCode() != 0) {
                        throw new ResourceException(
                                this.getClass(),
                                "fun ResultContext callback(HttpServletResponse response)",
                                "oauth2 callback get user information exception"
                        );
                    } else {
                        try {
                            return ResultContext.build(authorization(acm, request, response));
                        } finally {
                            HttpUtil.doPost(TemplateParser.execute(properties.getAuthorizeLogoutUrl(), Map.of("token", acm.getData().getToken())));
                        }
                    }
                } finally {
                    stateCache.del(state);
                }
            }
        }
    }

    @RequestMapping("/sso/oauth2/auth/logout")
    public ResultContext logout(HttpServletRequest request, HttpServletResponse response) {
        final User user = validator.execute(request, response);
        if (user != null) {
            cleaner.execute(request, response);
        }
        return ResultContext.build();
    }

    /**
     * Authorization
     *
     * @param acm      Auth Callback Model Object
     * @param request  Http Servlet Request Object
     * @param response Http Servlet Response Object
     * @return Result Object
     */
    public Object authorization(AuthCallbackModel acm, HttpServletRequest request, HttpServletResponse response) {
        return generator.execute(request, response, builder.create(acm.getData().getUser()));
    }

    /**
     * Auth Callback Model
     */
    @Data
    @Accessors(chain = true)
    public static class AuthCallbackModel implements Serializable {
        private Integer code;
        private String message;
        private AuthCallbackDataModel data;
    }

    /**
     * Auth Callback Data Model
     */
    @Data
    @Accessors(chain = true)
    public static class AuthCallbackDataModel implements Serializable {
        private String oid;
        private String user;
        private String token;
        private String type;
        private String scope;
        private Long expiration;
    }

}
