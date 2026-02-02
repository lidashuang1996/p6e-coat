package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.ReactiveVoucherCache;
import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.repository.ReactiveClientRepository;
import club.p6e.coat.auth.oauth2.validator.ReactiveRequestParameterValidator;
import club.p6e.coat.common.error.*;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.VerificationUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Reactive Authorize Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveAuthorizeService.class,
        ignored = ReactiveAuthorizeServiceImpl.class
)
@Component("club.p6e.coat.auth.oauth2.service.ReactiveAuthorizeServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class ReactiveAuthorizeServiceImpl implements ReactiveAuthorizeService {

    /**
     * Code Mode
     */
    private static final String CODE_MODE = "CODE";

    /**
     * Reactive Voucher Cache Object
     */
    private final ReactiveVoucherCache cache;

    /**
     * Reactive Client Repository Object
     */
    private final ReactiveClientRepository repository;

    /**
     * Constructor Initialization
     *
     * @param cache      Reactive Voucher Cache Object
     * @param repository Reactive Client Repository Object
     */
    public ReactiveAuthorizeServiceImpl(ReactiveVoucherCache cache, ReactiveClientRepository repository) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public Mono<IndexContext.Dto> execute(ServerWebExchange exchange, AuthorizeContext.Request request) {
        return ReactiveRequestParameterValidator
                .run(exchange, request)
                .flatMap(content -> {
                    final String scope = content.getScope();
                    final String state = content.getState();
                    final String clientId = content.getClientId();
                    final String redirectUri = content.getRedirectUri();
                    final String responseType = content.getResponseType();
                    if (!CODE_MODE.equalsIgnoreCase(responseType)) {
                        return Mono.error(new OAuth2ParameterException(
                                this.getClass(),
                                "fun Mono<IndexContext.Dto> execute(ServerWebExchange exchange, AuthorizeContext.Request request)",
                                "request parameter response_type<" + responseType + "> not support"
                        ));
                    }
                    return repository.findByAppId(clientId)
                            .switchIfEmpty(Mono.error(new OAuth2ClientException(
                                    this.getClass(),
                                    "fun Mono<IndexContext.Dto> execute(ServerWebExchange exchange, AuthorizeContext.Request request)",
                                    "[" + CODE_MODE + "] client_id<" + clientId + "> not match"
                            )))
                            .flatMap(client -> {
                                if (client.getEnable() != 1) {
                                    return Mono.error(new OAuth2ClientException(
                                            this.getClass(),
                                            "fun Mono<IndexContext.Dto> execute(ServerWebExchange exchange, AuthorizeContext.Request request)",
                                            "[" + CODE_MODE + "] client not enabled"
                                    ));
                                }
                                if (!VerificationUtil.validationOAuth2Scope(client.getScope(), scope)) {
                                    return Mono.error(new OAuth2ScopeException(
                                            this.getClass(),
                                            "fun Mono<IndexContext.Dto> execute(ServerWebExchange exchange, AuthorizeContext.Request request)",
                                            "[" + CODE_MODE + "] scope<" + scope + "> not match"
                                    ));
                                }
                                if (!VerificationUtil.validationOAuth2RedirectUri(client.getRedirectUri(), redirectUri)) {
                                    return Mono.error(new OAuth2RedirectUriException(
                                            this.getClass(),
                                            "fun Mono<IndexContext.Dto> execute(ServerWebExchange exchange, AuthorizeContext.Request request)",
                                            "[" + CODE_MODE + "] redirect_uri<" + redirectUri + "> not match"
                                    ));
                                }
                                final Properties.Page page = Properties.getInstance().getLogin().getPage();
                                final String voucher = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
                                return cache.set(voucher, new HashMap<>() {{
                                    put("type", "OAUTH2");
                                    put("time", String.valueOf(System.currentTimeMillis()));
                                    put("scope", scope);
                                    put("state", state);
                                    put("clientId", clientId);
                                    put("redirectUri", redirectUri);
                                    put("responseType", responseType);
                                }}).switchIfEmpty(Mono.error(new CacheException(
                                        this.getClass(),
                                        "fun Mono<IndexContext.Dto> execute(ServerWebExchange exchange, AuthorizeContext.Request request)",
                                        "[" + CODE_MODE + "] cache voucher<" + voucher + "> error"
                                ))).map(k -> new IndexContext.Dto().setType(page.getType()).setContent(TemplateParser.execute(page.getContent(), "VOUCHER", voucher)));
                            });
                });
    }

}
