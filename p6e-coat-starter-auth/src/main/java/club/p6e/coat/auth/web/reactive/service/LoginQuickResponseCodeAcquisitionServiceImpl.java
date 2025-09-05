package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.cache.LoginQuickResponseCodeCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Login Quick Response Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = LoginQuickResponseCodeAcquisitionService.class,
        ignored = LoginQuickResponseCodeAcquisitionServiceImpl.class
)
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class LoginQuickResponseCodeAcquisitionServiceImpl implements LoginQuickResponseCodeAcquisitionService {

    /**
     * Login Quick Response Code Cache Object
     */
    private final LoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Login Quick Response Code Cache Object
     */
    public LoginQuickResponseCodeAcquisitionServiceImpl(LoginQuickResponseCodeCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<LoginContext.QuickResponseCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request param) {
        final String code = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        exchange.getRequest().getAttributes().put("QUICK_RESPONSE_CODE_LOGIN_MARK", code);
        return cache
                .set(code, LoginQuickResponseCodeCache.EMPTY_CONTENT)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request param)",
                        "login quick response code acquisition cache exception"
                )))
                .map(r -> new LoginContext.QuickResponseCodeAcquisition.Dto().setContent(code));
    }

}
