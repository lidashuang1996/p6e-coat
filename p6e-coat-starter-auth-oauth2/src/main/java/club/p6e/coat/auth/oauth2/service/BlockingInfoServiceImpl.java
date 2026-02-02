package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.oauth2.cache.BlockingAuthClientCache;
import club.p6e.coat.auth.oauth2.cache.BlockingAuthUserCache;
import club.p6e.coat.auth.oauth2.context.InfoContext;
import club.p6e.coat.common.error.*;
import club.p6e.coat.common.utils.JsonUtil;
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
        if (model == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun Map<String, Object> getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                    "token does not exist or has expired"
            );
        }
        final String user = authUserCache.getUser(model.getUid());
        if (user == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun Map<String, Object> getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                    "user does not exist"
            );
        }
        final Map<String, Object> result = JsonUtil.fromJsonToMap(user, String.class, Object.class);
        if (result == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun Map<String, Object> getUserInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                    "user does not exist"
            );
        }
        return result;
    }

    @Override
    public Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request) {
        final String token = request.getToken();
        final BlockingAuthClientCache.Model model = authClientCache.getToken(token);
        if (model == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                    "token does not exist or has expired"
            );
        }
        final String user = authClientCache.getClient(model.getCid());
        if (user == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                    "client does not exist"
            );
        }
        final Map<String, Object> result = JsonUtil.fromJsonToMap(user, String.class, Object.class);
        if (result == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun Map<String, Object> getClientInfo(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, InfoContext.Request request)",
                    "client does not exist"
            );
        }
        return result;
    }

}
