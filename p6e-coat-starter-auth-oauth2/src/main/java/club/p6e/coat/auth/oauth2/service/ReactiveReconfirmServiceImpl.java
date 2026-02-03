package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.cache.ReactiveVoucherCache;
import club.p6e.coat.auth.oauth2.cache.ReactiveCodeCache;
import club.p6e.coat.auth.oauth2.context.ReconfirmContext;
import club.p6e.coat.common.error.CacheException;
import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Reactive Reconfirm Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = ReactiveReconfirmService.class,
        ignored = ReactiveReconfirmServiceImpl.class
)
@Component("club.p6e.coat.auth.oauth2.service.ReactiveReconfirmServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveReconfirmServiceImpl implements ReactiveReconfirmService {

    /**
     * Reactive Code Cache Object
     */
    private final ReactiveCodeCache codeCache;

    /**
     * Reactive Voucher Cache Object
     */
    private final ReactiveVoucherCache voucherCache;

    /**
     * Constructor Initialization
     *
     * @param codeCache    Reactive Code Cache Object
     * @param voucherCache Reactive Voucher Cache Object
     */
    public ReactiveReconfirmServiceImpl(ReactiveCodeCache codeCache, ReactiveVoucherCache voucherCache) {
        this.codeCache = codeCache;
        this.voucherCache = voucherCache;
    }

    @Override
    public Mono<Map<String, String>> execute(ServerWebExchange exchange, ReconfirmContext.Request request) {
        final String voucher = request.getVoucher();
        return voucherCache.get(voucher)
                .filter(data -> data != null && "OAUTH2".equalsIgnoreCase(data.get("type")))
                .flatMap(data -> {
                    final Map<String, String> result = new HashMap<>(data);
                    final String code = GeneratorUtil.random(8, true, false);
                    result.put("code", code);
                    return voucherCache.del(voucher).flatMap(k -> codeCache.set(code, result).map(kk -> result));
                })
                .switchIfEmpty(Mono.error(new CacheException(
                        this.getClass(),
                        "fun Mono<Map<String, String>> execute(ServerWebExchange exchange, ReconfirmContext.Request request)",
                        "voucher does not exist or has expired"
                )));
    }

}
