package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.auth.oauth2.context.AuthorizeContext;
import club.p6e.coat.auth.oauth2.context.TokenContext;
import club.p6e.coat.auth.oauth2.model.ClientModel;
import club.p6e.coat.auth.oauth2.repository.BlockingRepository;
import club.p6e.coat.common.error.Oauth2ParameterException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

public class BlockingTokenServiceImpl implements BlockingTokenService {

    /**
     * 认证模式
     */
    private static final String AUTHORIZATION_CODE_MODE = "authorization_code";

    /**
     * 客户端模式
     */
    private static final String CLIENT_CREDENTIALS_MODE = "client_credentials";
    private final BlockingRepository repository;

    @Override
    public IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, TokenContext.Request request) {
        final String grantType = request.getGrantType();
        if (CLIENT_CREDENTIALS_MODE.equalsIgnoreCase(grantType)) {
            return executeClientType(param);
        } else if (AUTHORIZATION_CODE_MODE.equalsIgnoreCase(grantType)) {
            return executeAuthorizationCodeType(param);
        } else {
            throw new Oauth2ParameterException(this.getClass(), "", "");
        }
    }

    public IndexContext.Dto executeClientType(TokenContext.Request request) {
        final String clientId = request.getClientId();
        final String clientSecret = request.getClientId();



    }

    public IndexContext.Dto executeAuthorizationCodeType(TokenContext.Request request) {
        final String code = request.getCode();
        final String clientId = request.getClientId();
        final String redirectUri = request.getRedirectUri();
        final String clientSecret = request.getClientSecret();

        final ClientModel client = repository.findClientByAppId(clientId);


    }

}
