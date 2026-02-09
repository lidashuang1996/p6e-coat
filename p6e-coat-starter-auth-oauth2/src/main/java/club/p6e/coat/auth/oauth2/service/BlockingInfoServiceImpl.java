package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.cache.BlockingAuthClientCache;
import club.p6e.coat.auth.oauth2.cache.BlockingAuthUserCache;
import club.p6e.coat.auth.oauth2.context.InfoContext;
import club.p6e.coat.common.exception.*;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.VerificationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Blocking Info Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingInfoService.class,
        ignored = BlockingInfoServiceImpl.class
)
@Component("club.p6e.coat.auth.oauth2.service.BlockingInfoServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingInfoServiceImpl implements BlockingInfoService {

    /**
     * User Info Scope
     */
    private static final String USER_INFO_SCOPE = "user_info";

    /**
     * Client Info Scope
     */
    private static final String CLIENT_INFO_SCOPE = "client_info";

    /**
     * Blocking Auth User Cache Object
     */
    private final BlockingAuthUserCache authUserCache;

    /**
     * Blocking Auth Client Cache Object
     */
    private final BlockingAuthClientCache authClientCache;

    /**
     * Constructor Initialization
     *
     * @param authUserCache Blocking Auth User Cache Object
     */
    public BlockingInfoServiceImpl(BlockingAuthUserCache authUserCache, BlockingAuthClientCache authClientCache) {
        this.authUserCache = authUserCache;
        this.authClientCache = authClientCache;
    }

    @Override
    public Map<String, Object> getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request) {
        final String token = request.getToken();
        final BlockingAuthUserCache.Model model = authUserCache.getToken(token);
        if (model != null) {
            if (VerificationUtil.validateOAuth2Scope(model.getScope(), USER_INFO_SCOPE)) {
                final String user = authUserCache.getUser(model.getUid());
                if (user != null) {
                    final Map<String, Object> result = JsonUtil.fromJsonToMap(user, String.class, Object.class);
                    if (result != null) {
                        return result;
                    }
                }
            } else {
                throw new OAuth2ScopeException(
                        this.getClass(),
                        "fun Map<String, Object> getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                        "token does not have the scope of " + USER_INFO_SCOPE
                );
            }
        }
        throw new AuthException(
                this.getClass(),
                "fun Map<String, Object> getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                "token does not exist or has expired"
        );
    }

    @Override
    public Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request) {
        final String token = request.getToken();
        final BlockingAuthClientCache.Model model = authClientCache.getToken(token);
        if (model != null) {
            if (VerificationUtil.validateOAuth2Scope(model.getScope(), CLIENT_INFO_SCOPE)) {
                final String client = authClientCache.getClient(model.getCid());
                if (client != null) {
                    final Map<String, Object> result = JsonUtil.fromJsonToMap(client, String.class, Object.class);
                    if (result != null) {
                        return result;
                    }
                }
            } else {
                throw new OAuth2ScopeException(
                        this.getClass(),
                        "fun Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                        "token does not have the scope of " + CLIENT_INFO_SCOPE
                );
            }
        }
        throw new AuthException(
                this.getClass(),
                "fun Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                "token does not exist or has expired"
        );
    }

}
