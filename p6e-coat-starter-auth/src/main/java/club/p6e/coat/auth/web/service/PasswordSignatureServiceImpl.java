package club.p6e.coat.auth.web.service;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.aspect.VoucherAspect;
import club.p6e.coat.auth.web.cache.PasswordSignatureCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.RsaUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Password Signature Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@ConditionalOnMissingBean(
        value = PasswordSignatureService.class,
        ignored = PasswordSignatureServiceImpl.class
)
@Component("club.p6e.coat.auth.web.service.PasswordSignatureServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.servlet.DispatcherServlet")
public class PasswordSignatureServiceImpl implements PasswordSignatureService {

    /**
     * Password Signature Cache Object
     */
    private final PasswordSignatureCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Password Signature Cache Object
     */
    public PasswordSignatureServiceImpl(PasswordSignatureCache cache) {
        this.cache = cache;
    }

    @Override
    public PasswordSignatureContext.Dto execute(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            PasswordSignatureContext.Request param
    ) {
        String publicKey;
        String privateKey;
        try {
            final RsaUtil.KeyModel model = RsaUtil.generateKeyPair();
            publicKey = model.getPublicKey();
            privateKey = model.getPrivateKey();
        } catch (Exception e) {
            throw GlobalExceptionContext.executeRasException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, PasswordSignatureContext.Request param) >>> " + e.getMessage(),
                    "password signature rsa exception"
            );
        }
        final String finalPublicKey = publicKey;
        final String finalPrivateKey = privateKey;
        final String mark = GeneratorUtil.uuid() + GeneratorUtil.random();
        httpServletRequest.setAttribute(VoucherAspect.MyHttpServletRequestWrapper.ACCOUNT_PASSWORD_SIGNATURE_MARK, mark);
        final String content = JsonUtil.toJson(new HashMap<>() {{
            put("mark", mark);
            put("public", finalPublicKey);
            put("private", finalPrivateKey);
        }});
        cache.set(mark, content);
        return new PasswordSignatureContext.Dto().setContent(finalPublicKey);
    }

}
