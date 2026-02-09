package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.cache.ReactiveLoginQuickResponseCodeCache;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Reactive Login Quick Response Code Acquisition Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveLoginQuickResponseCodeAcquisitionService.class,
        ignored = ReactiveLoginQuickResponseCodeAcquisitionServiceImpl.class
)
@Component("club.p6e.coat.auth.service.ReactiveLoginQuickResponseCodeAcquisitionServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveLoginQuickResponseCodeAcquisitionServiceImpl implements ReactiveLoginQuickResponseCodeAcquisitionService {

    /**
     * Reactive Login Quick Response Code Cache Object
     */
    private final ReactiveLoginQuickResponseCodeCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Reactive Login Quick Response Code Cache Object
     */
    public ReactiveLoginQuickResponseCodeAcquisitionServiceImpl(ReactiveLoginQuickResponseCodeCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<LoginContext.QuickResponseCodeAcquisition.Dto> execute(
            ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request param) {
        final String code = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        exchange.getRequest().getAttributes().put("QUICK_RESPONSE_CODE_LOGIN_MARK", code);
        return cache
                .set(code, ReactiveLoginQuickResponseCodeCache.EMPTY_CONTENT)
                .switchIfEmpty(Mono.error(new CacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.QuickResponseCodeAcquisition.Request param)",
                        "login quick response code acquisition cache exception"
                )))
                .map(r -> new LoginContext.QuickResponseCodeAcquisition.Dto().setContent(code));
    }

}
