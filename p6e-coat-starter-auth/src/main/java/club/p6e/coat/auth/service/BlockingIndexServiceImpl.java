package club.p6e.coat.auth.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.cache.BlockingVoucherCache;
import club.p6e.coat.auth.context.IndexContext;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.TemplateParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Index Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingIndexService.class,
        ignored = BlockingIndexServiceImpl.class
)
@Component("club.p6e.coat.auth.service.IndexServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingIndexServiceImpl implements BlockingIndexService {

    /**
     * Voucher Cache Object
     */
    private final BlockingVoucherCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Voucher Cache Object
     */
    public BlockingIndexServiceImpl(BlockingVoucherCache cache) {
        this.cache = cache;
    }

    @Override
    public IndexContext.Dto execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final Properties.Page page = Properties.getInstance().getLogin().getPage();
        final String voucher = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        cache.set(voucher, new HashMap<>() {{
            put("type", "INDEX");
            put("time", String.valueOf(System.currentTimeMillis()));
        }});
        return new IndexContext.Dto().setType(page.getType()).setContent(TemplateParser.execute(page.getContent(), "VOUCHER", voucher));
    }

}
