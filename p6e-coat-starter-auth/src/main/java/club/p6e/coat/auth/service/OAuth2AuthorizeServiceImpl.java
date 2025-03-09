package club.p6e.coat.auth.service;

import club.p6e.coat.auth.AuthPage;
import club.p6e.coat.auth.repository.OAuth2ClientRepository;
import club.p6e.coat.common.utils.TemplateParser;
import club.p6e.coat.common.utils.VerificationUtil;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.context.OAuth2Context;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * OAUTH2 认证服务的默认实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class OAuth2AuthorizeServiceImpl implements OAuth2AuthorizeService {

    /**
     * CODE 类型
     */
    private static final String CODE_TYPE = "CODE";

    /**
     * 配置文件对象
     */
    private final Properties properties;

    /**
     * OAUTH CLIENT 存储库
     */
    private final OAuth2ClientRepository repository;

    /**
     * 构造方法初始化
     *
     * @param properties 配置文件对象
     * @param repository OAUTH CLIENT2 存储库
     */
    public OAuth2AuthorizeServiceImpl(
            Properties properties,
            OAuth2ClientRepository repository
    ) {
        this.properties = properties;
        this.repository = repository;
    }

    @Override
    public Mono<Void> execute(ServerWebExchange exchange, OAuth2Context.Auth.Request param) {
        final String responseType = param.getResponseType();
        if (CODE_TYPE.equalsIgnoreCase(responseType)) {
            // 判断是否启用 CODE 模型
            if (properties.getOauth2().getAuthorizationCode().isEnable()) {
                // 如果启用 CODE 模型就执行 CODE 类型的代码
                return executeCodeType(exchange, param);
            } else {
                return Mono.error(GlobalExceptionContext.executeServiceNotEnabledException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, OAuth2Context.Auth.Request param)",
                        "OAuth2 auth type [" + responseType + "] not enabled exception."
                ));
            }
        }
        return Mono.error(GlobalExceptionContext.executeTypeNotSupportedException(
                this.getClass(),
                "fun execute(Oauth2Context.Auth.Request param).",
                "OAuth2 auth service not supported exception."
        ));
    }

    /**
     * 执行 CODE 类型的认证处理
     *
     * @param param 请求对象
     * @return 结果对象
     */
    private Mono<Void> executeCodeType(ServerWebExchange exchange, OAuth2Context.Auth.Request param) {
        final String state = param.getState();
        final String scope = param.getScope();
        final String clientId = param.getClientId();
        final String redirectUri = param.getRedirectUri();
        return repository
                .findByClientId(clientId)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeOauth2ClientException(
                        this.getClass(),
                        "fun executeCodeType(ServerWebExchange exchange, OAuth2Context.Auth.Request param)",
                        "OAuth2 code type mode client id exception."
                )))
                .flatMap(m -> {
                    // 验证是否启用
                    if (m.getEnabled() == null || !"1".equals(String.valueOf(m.getEnabled()))) {
                        return Mono.error(GlobalExceptionContext.executeOauth2ClientException(
                                this.getClass(),
                                "fun executeCodeType(ServerWebExchange exchange, OAuth2Context.Auth.Request param)",
                                "OAuth2 code type mode client not enabled exception."
                        ));
                    }
                    // 验证作用域
                    if (!VerificationUtil.validationOAuth2Scope(m.getScope(), scope)) {
                        return Mono.error(GlobalExceptionContext.executeOauth2ScopeException(
                                this.getClass(),
                                "fun executeCodeType(ServerWebExchange exchange, OAuth2Context.Auth.Request param)",
                                "OAuth2 code type mode check scope exception."
                        ));
                    }
                    // 验证重定向
                    if (!VerificationUtil.validationOAuth2RedirectUri(m.getRedirectUri(), redirectUri)) {
                        return Mono.error(GlobalExceptionContext.executeOauth2RedirectUriException(
                                this.getClass(),
                                "fun executeCodeType(ServerWebExchange exchange, OAuth2Context.Auth.Request param)",
                                "OAuth2 code type mode check redirect uri exception."
                        ));
                    }
                    return AuthVoucher.createOAuth2Index(m, CODE_TYPE, redirectUri, scope, state);
                })
                .flatMap(v -> write(exchange, v.getMark()));
    }


    /**
     * 写入返回数据
     *
     * @param exchange ServerWebExchange 对象
     * @return Mono/Void 对象
     */
    private Mono<Void> write(ServerWebExchange exchange, String voucher) {
        final ServerHttpResponse response = exchange.getResponse();
        final AuthPage.Model login = AuthPage.oAuth2Login();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(login.getType());
        return response.writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(
                TemplateParser.execute(
                        login.getContent(), "page", "login", "voucher", voucher
                ).getBytes(StandardCharsets.UTF_8)
        )));
    }

}
