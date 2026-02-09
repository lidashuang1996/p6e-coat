package club.p6e.coat.auth.oauth2.service;

import club.p6e.coat.auth.cache.BlockingVoucherCache;
import club.p6e.coat.auth.oauth2.cache.BlockingCodeCache;
import club.p6e.coat.auth.oauth2.context.ReconfirmContext;
import club.p6e.coat.common.exception.*;
import club.p6e.coat.common.utils.GeneratorUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Blocking Authorize Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = BlockingReconfirmService.class,
        ignored = BlockingReconfirmServiceImpl.class
)
@Component("club.p6e.coat.auth.oauth2.service.BlockingReconfirmServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class BlockingReconfirmServiceImpl implements BlockingReconfirmService {

    /**
     * Blocking Code Cache Object
     */
    private final BlockingCodeCache codeCache;

    /**
     * Blocking Voucher Cache Object
     */
    private final BlockingVoucherCache voucherCache;

    /**
     * Constructor Initialization
     *
     * @param codeCache    Blocking Code Cache Object
     * @param voucherCache Blocking Voucher Cache Object
     */
    public BlockingReconfirmServiceImpl(BlockingCodeCache codeCache, BlockingVoucherCache voucherCache) {
        this.codeCache = codeCache;
        this.voucherCache = voucherCache;
    }

    @Override
    public Map<String, String> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ReconfirmContext.Request request) {
        final String voucher = request.getVoucher();
        final Map<String, String> data = voucherCache.get(voucher);
        if (data != null && "OAUTH2".equalsIgnoreCase(data.get("type"))) {
            try {
                final Map<String, String> result = new HashMap<>(data);
                final String code = GeneratorUtil.random(8, true, false);
                result.put("code", code);
                codeCache.set(code, result);
                return result;
            } finally {
                voucherCache.del(voucher);
            }
        }
        throw new CacheException(
                this.getClass(),
                "fun Map<String, String> execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ReconfirmContext.Request request)",
                "voucher does not exist or has expired"
        );
    }

}
