package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.ReactiveVoucherCache;
import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.TemplateParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Reactive Index Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.service.ReactiveIndexServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactiveIndexServiceImpl implements ReactiveIndexService {

    /**
     * Reactive Voucher Cache Object
     */
    private final ReactiveVoucherCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Reactive Voucher Cache Object
     */
    public ReactiveIndexServiceImpl(ReactiveVoucherCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<IndexContext.Dto> execute(ServerWebExchange exchange) {
        final Properties.Page page = Properties.getInstance().getLogin().getPage();
        final String voucher = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        return cache.set(voucher, new HashMap<>() {{
            put("type", "INDEX");
            put("time", String.valueOf(System.currentTimeMillis()));
        }}).flatMap(k -> Mono.just(new IndexContext.Dto().setType(page.getType()).setContent(TemplateParser.execute(page.getContent(), "VOUCHER", voucher))));
    }

}
