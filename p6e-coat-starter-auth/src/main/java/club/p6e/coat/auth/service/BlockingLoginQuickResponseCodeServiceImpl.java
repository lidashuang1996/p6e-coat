package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.token.BlockingTokenValidator;
import club.p6e.coat.auth.cache.BlockingLoginQuickResponseCodeCache;
import club.p6e.coat.common.exception.AuthException;
import club.p6e.coat.common.exception.CacheException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Blocking Login Quick Response Code Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingLoginQuickResponseCodeService.class,
        ignored = BlockingLoginQuickResponseCodeServiceImpl.class
)
@Component("club.p6e.coat.auth.service.BlockingLoginQuickResponseCodeServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginQuickResponseCodeServiceImpl implements BlockingLoginQuickResponseCodeService {

    /**
     * Blocking Token Validator Object
     */
    private final BlockingTokenValidator validator;

    /**
     * Blocking Login Quick Response Code Cache Object
     */
    private final BlockingLoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param validator Blocking Token Validator Object
     * @param cache     Blocking Login Quick Response Code Cache Object
     */
    public BlockingLoginQuickResponseCodeServiceImpl(BlockingTokenValidator validator, BlockingLoginQuickResponseCodeCache cache) {
        this.cache = cache;
        this.validator = validator;
    }

    @Override
    public LoginContext.QuickResponseCode.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCode.Request param
    ) {
        final String mark = param.getContent();
        final User user = validator.execute(httpServletRequest, httpServletResponse);
        if (user == null) {
            throw new AuthException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request param)",
                    "login quick response code auth exception"
            );
        } else {
            final String content = cache.get(mark);
            if (content == null) {
                throw new CacheException(
                        this.getClass(),
                        "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request param)",
                        "login quick response code cache data does not exist or expire exception"
                );
            } else if (BlockingLoginQuickResponseCodeCache.isEmpty(content)) {
                cache.set(mark, String.valueOf(user.id()));
                return new LoginContext.QuickResponseCode.Dto().setContent(String.valueOf(System.currentTimeMillis()));
            } else {
                throw new CacheException(
                        this.getClass(),
                        "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request param)",
                        "login quick response code cache data exist other user exception"
                );
            }
        }
    }

}
