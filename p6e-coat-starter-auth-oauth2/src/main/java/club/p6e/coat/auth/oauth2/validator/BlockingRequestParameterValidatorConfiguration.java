package club.p6e.coat.auth.oauth2.validator;

import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.context.TokenContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Blocking Request Parameter Validator Configuration
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingRequestParameterValidatorConfiguration {

    public BlockingRequestParameterValidator validateAuthorizeContextRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return AuthorizeContext.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof final AuthorizeContext.Request acr) {
                    if (acr.getState() != null && acr.getScope() != null
                            && acr.getClientId() != null && acr.getRedirectUri() != null && acr.getResponseType() != null) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

    public BlockingRequestParameterValidator validateTokenContextRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return TokenContext.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof final TokenContext.Request tcr) {
                    if (tcr.getGrantType() == null) {
                        return null;
                    }
                    if ("password".equals(tcr.getGrantType()) && tcr.getClientId() != null && tcr.getClientSecret() != null && tcr.getUsername() != null && tcr.getPassword() != null) {
                        return param;
                    } else if ("client_credentials".equals(tcr.getGrantType()) && tcr.getClientId() != null && tcr.getClientSecret() != null && tcr.getScope() != null) {
                        return param;
                    } else if ("authorization_code".equals(tcr.getGrantType()) && tcr.getClientId() != null && tcr.getClientSecret() != null && tcr.getCode() != null && tcr.getRedirectUri() != null) {
                        return param;
                    }
                }
                return null;
            }

        };
    }

}
