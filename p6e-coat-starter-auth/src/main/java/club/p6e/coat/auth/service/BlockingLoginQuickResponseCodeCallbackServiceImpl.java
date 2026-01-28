package club.p6e.coat.auth.service;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.aspect.BlockingVoucherAspect;
import club.p6e.coat.auth.cache.BlockingLoginQuickResponseCodeCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.repository.BlockingUserRepository;
import club.p6e.coat.common.utils.TransformationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Blocking Login Quick Response Code Callback Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingLoginQuickResponseCodeCallbackService.class,
        ignored = BlockingLoginQuickResponseCodeCallbackServiceImpl.class
)
@Component("club.p6e.coat.auth.service.BlockingLoginQuickResponseCodeCallbackServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLoginQuickResponseCodeCallbackServiceImpl implements BlockingLoginQuickResponseCodeCallbackService {

    /**
     * Blocking User Repository Object
     */
    private final BlockingUserRepository repository;

    /**
     * Blocking Login Quick Response Code Cache Object
     */
    private final BlockingLoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param repository Blocking User Repository Object
     * @param cache      Blocking Login Quick Response Code Cache Object
     */
    public BlockingLoginQuickResponseCodeCallbackServiceImpl(BlockingUserRepository repository, BlockingLoginQuickResponseCodeCache cache) {
        this.cache = cache;
        this.repository = repository;
    }

    @Override
    public User execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            LoginContext.QuickResponseCodeCallback.Request param
    ) {
        final String mark = TransformationUtil.objectToString(httpServletRequest.getAttribute(BlockingVoucherAspect.MyHttpServletRequestWrapper.QUICK_RESPONSE_CODE_LOGIN_MARK));
        final String content = cache.get(mark);
        if (content == null) {
            throw GlobalExceptionContext.executeCacheException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCode.Request param)",
                    "login quick response code cache data does not exist or expire exception"
            );
        } else if (BlockingLoginQuickResponseCodeCache.isEmpty(content)) {
            throw GlobalExceptionContext.executeQrCodeDataNullException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCode.Request param)",
                    "login quick response code data is null exception"
            );
        } else {
            cache.del(mark);
            final User user = repository.findById(Integer.valueOf(content));
            if (user == null) {
                throw GlobalExceptionContext.executeUserNoExistException(
                        this.getClass(),
                        "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, LoginContext.QuickResponseCode.Request param)",
                        "login quick response code user id select data does not exist exception"
                );
            } else {
                return user;
            }
        }
    }

}
