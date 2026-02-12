package club.p6e.coat.auth.service;

import club.p6e.coat.auth.token.BlockingTokenCleaner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * Blocking Logout Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.service.BlockingLogoutServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingLogoutServiceImpl implements BlockingLogoutService {

    /**
     * Blocking Token Object
     */
    private final BlockingTokenCleaner cleaner;

    /**
     * Constructor Initialization
     *
     * @param cleaner Blocking Token Object
     */
    public BlockingLogoutServiceImpl(BlockingTokenCleaner cleaner) {
        this.cleaner = cleaner;
    }

    @Override
    public Object execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return cleaner.execute(httpServletRequest, httpServletResponse);
    }

}
