package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.web.reactive.cache.VoucherCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TemplateParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Index Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(IndexService.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
public class IndexServiceImpl implements IndexService {

    @Override
    public Mono<String[]> execute(ServerWebExchange exchange) {
        final Properties.Page page = Properties.getInstance().getLogin().getPage();
        final String voucher = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        return SpringUtil.getBean(VoucherCache.class).set(voucher, new HashMap<>() {{
            put("time", String.valueOf(System.currentTimeMillis()));
        }}).flatMap(k -> Mono.just(new String[]{page.getType(), TemplateParser.execute(page.getContent(), "VOUCHER", voucher)}));
    }

}
