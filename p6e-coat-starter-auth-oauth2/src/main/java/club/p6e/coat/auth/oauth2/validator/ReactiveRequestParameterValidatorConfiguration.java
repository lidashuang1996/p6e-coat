package club.p6e.coat.auth.oauth2.validator;

import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.context.TokenContext;
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
                    if (acr.getState() != null && acr.getScope() != null
                            && acr.getClientId() != null && acr.getRedirectUri() != null && acr.getResponseType() != null) {
                        return Mono.just(param);
                    }
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
                    if (tcr.getGrantType() == null) {
                        return null;
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

}
