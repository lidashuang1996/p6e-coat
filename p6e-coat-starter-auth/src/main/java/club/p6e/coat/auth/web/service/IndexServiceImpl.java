package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.Properties;
import club.p6e.coat.auth.web.cache.VoucherCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.TemplateParser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * Index Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = IndexService.class,
        ignored = IndexServiceImpl.class
)
@Component("club.p6e.coat.auth.web.service.IndexServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class IndexServiceImpl implements IndexService {

    /**
     * Voucher Cache Object
     */
    private final VoucherCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Voucher Cache Object
     */
    public IndexServiceImpl(VoucherCache cache) {
        this.cache = cache;
    }

    @Override
    public String[] execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final Properties.Page page = Properties.getInstance().getLogin().getPage();
        final String voucher = GeneratorUtil.uuid() + GeneratorUtil.random(8, true, false);
        cache.set(voucher, new HashMap<>() {{
            put("type", "INDEX");
            put("time", String.valueOf(System.currentTimeMillis()));
        }});
        return new String[]{page.getType(), TemplateParser.execute(page.getContent(), "VOUCHER", voucher)};
    }

}
