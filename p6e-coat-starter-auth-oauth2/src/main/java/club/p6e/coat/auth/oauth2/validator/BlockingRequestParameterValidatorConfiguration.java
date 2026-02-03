package club.p6e.coat.auth.oauth2.validator;

import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.context.InfoContext;
import club.p6e.coat.auth.oauth2.context.ReconfirmContext;
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

    public BlockingRequestParameterValidator validateInfoContextRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return InfoContext.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof final InfoContext.Request icr) {
                    String token = icr.getToken();
                    if (token == null) {
                        token = request.getParameter("token");
                        if (token == null) {
                            token = request.getHeader("Authentication");
                        }
                        if (token == null) {
                            token = request.getHeader("X-Authentication");
                        }
                        if (token == null) {
                            return null;
                        }
                    }
                    return param;
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
                    String clientId = tcr.getClientId();
                    if (clientId == null) {
                        clientId = request.getParameter("cid");
                    }
                    if (clientId == null) {
                        clientId = request.getParameter("client_id");
                    }
                    if (clientId == null) {
                        clientId = request.getParameter("clientId");
                    }
                    if (clientId != null) {
                        tcr.setClientId(clientId);
                    }
                    String grantType = tcr.getGrantType();
                    if (grantType == null) {
                        grantType = request.getParameter("gt");
                    }
                    if (grantType == null) {
                        grantType = request.getParameter("grant_type");
                    }
                    if (grantType == null) {
                        grantType = request.getParameter("grantType");
                    }
                    if (grantType != null) {
                        tcr.setGrantType(grantType);
                    }
                    String redirectUri = tcr.getRedirectUri();
                    if (redirectUri == null) {
                        redirectUri = request.getParameter("ru");
                    }
                    if (redirectUri == null) {
                        redirectUri = request.getParameter("redirect_uri");
                    }
                    if (redirectUri == null) {
                        redirectUri = request.getParameter("redirectUri");
                    }
                    if (redirectUri != null) {
                        tcr.setGrantType(redirectUri);
                    }
                    String clientSecret = tcr.getRedirectUri();
                    if (clientSecret == null) {
                        clientSecret = request.getParameter("ru");
                    }
                    if (clientSecret == null) {
                        clientSecret = request.getParameter("redirect_uri");
                    }
                    if (clientSecret == null) {
                        clientSecret = request.getParameter("redirectUri");
                    }
                    if (clientSecret != null) {
                        tcr.setClientSecret(clientSecret);
                    }
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

    public BlockingRequestParameterValidator validateReconfirmContextRequest() {
        return new BlockingRequestParameterValidator() {

            @Override
            public int order() {
                return 0;
            }

            @Override
            public Class<?> type() {
                return ReconfirmContext.Request.class;
            }

            @Override
            public <T> T execute(HttpServletRequest request, HttpServletResponse response, T param) {
                if (param instanceof final ReconfirmContext.Request tcr) {
                    String voucher = tcr.getVoucher();
                    if (tcr.getVoucher() == null) {
                        voucher = request.getParameter("v");
                        if (voucher == null) {
                            voucher = request.getParameter("voucher");
                        }
                        if (voucher == null) {
                            voucher = request.getHeader("Voucher");
                        }
                        if (voucher == null) {
                            voucher = request.getHeader("X-Voucher");
                        }
                        if (voucher == null) {
                            return null;
                        }
                    }
                    return param;
                }
                return null;
            }

        };
    }

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
                    String clientId = acr.getClientId();
                    if (clientId == null) {
                        clientId = request.getParameter("cid");
                        if (clientId == null) {
                            clientId = request.getParameter("client_id");
                        }
                        if (clientId == null) {
                            clientId = request.getParameter("clientId");
                        }
                        if (clientId != null) {
                            acr.setClientId(clientId);
                        }
                    }
                    String redirectUri = acr.getRedirectUri();
                    if (redirectUri == null) {
                        redirectUri = request.getParameter("uri");
                        if (redirectUri == null) {
                            redirectUri = request.getParameter("redirect_uri");
                        }
                        if (redirectUri == null) {
                            redirectUri = request.getParameter("redirectUri");
                        }
                        if (redirectUri != null) {
                            acr.setRedirectUri(redirectUri);
                        }
                    }
                    String responseType = acr.getResponseType();
                    if (responseType == null) {
                        responseType = request.getParameter("type");
                        if (responseType == null) {
                            responseType = request.getParameter("response_type");
                        }
                        if (responseType == null) {
                            responseType = request.getParameter("responseType");
                        }
                        if (responseType != null) {
                            acr.setResponseType(responseType);
                        }
                    }
                    if (acr.getState() != null && acr.getScope() != null
                            && acr.getClientId() != null && acr.getRedirectUri() != null && acr.getResponseType() != null) {
                        return param;
                    }
                }
                return null;
            }

        };
    }


}
