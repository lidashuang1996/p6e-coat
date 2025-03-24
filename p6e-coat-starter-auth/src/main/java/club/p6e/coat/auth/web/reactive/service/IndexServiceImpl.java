package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.web.reactive.cache.VoucherCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.SpringUtil;
import club.p6e.coat.common.utils.TemplateParser;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
@Service
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
