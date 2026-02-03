package club.p6e.coat.auth.oauth2.validator;

import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.context.InfoContext;
import club.p6e.coat.auth.oauth2.context.ReconfirmContext;
import club.p6e.coat.auth.oauth2.context.TokenContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Request Parameter Validator Configuration
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class ReactiveRequestParameterValidatorConfiguration {

    public ReactiveRequestParameterValidator validateInfoContextRequest() {
        return new ReactiveRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return InfoContext.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof final InfoContext.Request icr) {
                    if (icr.getToken() == null) {
                        String token = null;
                        final ServerHttpRequest request = exchange.getRequest();
                        final HttpHeaders headers = request.getHeaders();
                        final MultiValueMap<String, String> params = request.getQueryParams();
                        if (params != null) {
                            token = params.getFirst("token");
                        }
                        if (headers != null && token == null) {
                            token = headers.getFirst("Authentication");
                        }
                        if (headers != null && token == null) {
                            token = headers.getFirst("X-Authentication");
                        }
                        if (token == null) {
                            return Mono.empty();
                        }
                    }
                    return Mono.just(param);
                }
                return Mono.empty();
            }

        };
    }

    public ReactiveRequestParameterValidator validateTokenContextRequest() {
        return new ReactiveRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return TokenContext.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof final TokenContext.Request tcr) {
                    final ServerHttpRequest request = exchange.getRequest();
                    final MultiValueMap<String, String> params = request.getQueryParams();
                    String clientId = tcr.getClientId();
                    if (params != null && clientId == null) {
                        clientId = params.getFirst("cid");
                    }
                    if (clientId == null) {
                        clientId = params.getFirst("client_id");
                    }
                    if (clientId == null) {
                        clientId = params.getFirst("clientId");
                    }
                    if (clientId != null) {
                        tcr.setClientId(clientId);
                    }
                    String grantType = tcr.getGrantType();
                    if (grantType == null) {
                        grantType = params.getFirst("gt");
                    }
                    if (grantType == null) {
                        grantType = params.getFirst("grant_type");
                    }
                    if (grantType == null) {
                        grantType = params.getFirst("grantType");
                    }
                    if (grantType != null) {
                        tcr.setGrantType(grantType);
                    }
                    String redirectUri = tcr.getRedirectUri();
                    if (redirectUri == null) {
                        redirectUri = params.getFirst("ru");
                    }
                    if (redirectUri == null) {
                        redirectUri = params.getFirst("redirect_uri");
                    }
                    if (redirectUri == null) {
                        redirectUri = params.getFirst("redirectUri");
                    }
                    if (redirectUri != null) {
                        tcr.setGrantType(redirectUri);
                    }
                    String clientSecret = tcr.getRedirectUri();
                    if (clientSecret == null) {
                        clientSecret = params.getFirst("ru");
                    }
                    if (clientSecret == null) {
                        clientSecret = params.getFirst("redirect_uri");
                    }
                    if (clientSecret == null) {
                        clientSecret = params.getFirst("redirectUri");
                    }
                    if (clientSecret != null) {
                        tcr.setClientSecret(clientSecret);
                    }
                    if (tcr.getGrantType() == null) {
                        return Mono.empty();
                    }
                    if ("password".equalsIgnoreCase(tcr.getGrantType()) && tcr.getClientId() != null && tcr.getClientSecret() != null && tcr.getUsername() != null && tcr.getPassword() != null) {
                        return Mono.just(param);
                    } else if ("client_credentials".equalsIgnoreCase(tcr.getGrantType()) && tcr.getClientId() != null && tcr.getClientSecret() != null && tcr.getScope() != null) {
                        return Mono.just(param);
                    } else if ("authorization_code".equalsIgnoreCase(tcr.getGrantType()) && tcr.getClientId() != null && tcr.getClientSecret() != null && tcr.getCode() != null && tcr.getRedirectUri() != null) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }

    public ReactiveRequestParameterValidator validateReconfirmContextRequest() {
        return new ReactiveRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return ReconfirmContext.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof final ReconfirmContext.Request tcr) {
                    final ServerHttpRequest request = exchange.getRequest();
                    final HttpHeaders headers = request.getHeaders();
                    final MultiValueMap<String, String> params = request.getQueryParams();
                    String voucher = tcr.getVoucher();
                    if (voucher == null) {
                        voucher = params.getFirst("v");
                        if (voucher == null) {
                            voucher = params.getFirst("voucher");
                        }
                        if (voucher == null) {
                            voucher = headers.getFirst("Voucher");
                        }
                        if (voucher == null) {
                            voucher = headers.getFirst("X-Voucher");
                        }
                        if (voucher == null) {
                            return null;
                        }
                    }
                    return Mono.just(param);
                }
                return Mono.empty();
            }

        };
    }

    public ReactiveRequestParameterValidator validateAuthorizeContextRequest() {
        return new ReactiveRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return AuthorizeContext.Request.class;
            }

            @Override
            public <T> Mono<T> execute(ServerWebExchange exchange, T param) {
                if (param instanceof final AuthorizeContext.Request acr) {
                    final ServerHttpRequest request = exchange.getRequest();
                    final HttpHeaders headers = request.getHeaders();
                    final MultiValueMap<String, String> params = request.getQueryParams();
                    String clientId = acr.getClientId();
                    if (acr.getClientId() == null) {
                        clientId = params.getFirst("cid");
                        if (clientId == null) {
                            clientId = params.getFirst("client_id");
                        }
                        if (clientId == null) {
                            clientId = params.getFirst("clientId");
                        }
                        if (clientId != null) {
                            acr.setClientId(clientId);
                        }
                    }
                    String redirectUri = acr.getRedirectUri();
                    if (acr.getRedirectUri() == null) {
                        redirectUri = params.getFirst("uri");
                        if (redirectUri == null) {
                            redirectUri = params.getFirst("redirect_uri");
                        }
                        if (redirectUri == null) {
                            redirectUri = params.getFirst("redirectUri");
                        }
                        if (redirectUri != null) {
                            acr.setRedirectUri(redirectUri);
                        }
                    }
                    String responseType = acr.getResponseType();
                    if (acr.getResponseType() == null) {
                        responseType = params.getFirst("type");
                        if (responseType == null) {
                            responseType = params.getFirst("response_type");
                        }
                        if (responseType == null) {
                            responseType = params.getFirst("responseType");
                        }
                        if (responseType != null) {
                            acr.setResponseType(responseType);
                        }
                    }
                    if (acr.getState() != null && acr.getScope() != null
                            && acr.getClientId() != null && acr.getRedirectUri() != null && acr.getResponseType() != null) {
                        return Mono.just(param);
                    }
                }
                return Mono.empty();
            }

        };
    }


}
