package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.token.web.TokenValidator;
import club.p6e.coat.auth.web.cache.LoginQuickResponseCodeCache;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Quick Response Code Callback Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(LoginQuickResponseCodeService.class)
public class LoginQuickResponseCodeServiceImpl implements LoginQuickResponseCodeService {

    /**
     * Token Validator Object
     */
    private final TokenValidator validator;

    /**
     * Quick Response Code Login Cache Object
     */
    private final LoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param validator Token Validator Object
     * @param cache     Quick Response Code Login Cache Object
     */
    public LoginQuickResponseCodeServiceImpl(TokenValidator validator, LoginQuickResponseCodeCache cache) {
        this.cache = cache;
        this.validator = validator;
    }

    @Override
    public LoginContext.QuickResponseCodeCallback.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCode.Request param
    ) {
        final String mark = param.getContent();
        final User user = validator.execute(httpServletRequest, httpServletResponse);
        if (user == null) {
            throw GlobalExceptionContext.exceptionAuthException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request param)",
                    "quick response code callback login auth exception"
            );
        } else {
            final String content = cache.get(mark);
            if (content == null) {
                throw GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request param)",
                        "quick response code callback login cache data does not exist or expire exception"
                );
            } else if (LoginQuickResponseCodeCache.isEmpty(content)) {
                return new LoginContext.QuickResponseCodeCallback.Dto().setContent(String.valueOf(System.currentTimeMillis()));
            } else {
                throw GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCodeCallback.Request param)",
                        "quick response code callback login cache other data exists exception"
                );
            }
        }
    }

}
