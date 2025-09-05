package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.web.reactive.aspect.VoucherAspect;
import club.p6e.coat.auth.web.reactive.cache.PasswordSignatureCache;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.RsaUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Password Signature Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(PasswordSignatureService.class)
@ConditionalOnClass(name = "org.springframework.web.reactive.package-info")
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
    public Mono<PasswordSignatureContext.Dto> execute(ServerWebExchange exchange, PasswordSignatureContext.Request param) {
        String publicKey;
        String privateKey;
        try {
            final RsaUtil.KeyModel model = RsaUtil.generateKeyPair();
            publicKey = model.getPublicKey();
            privateKey = model.getPrivateKey();
        } catch (Exception e) {
            return Mono.error(GlobalExceptionContext.executeRasException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, PasswordSignatureContext.Request param) >>> " + e.getMessage(),
                    "password signature rsa exception"
            ));
        }
        final String finalPublicKey = publicKey;
        final String finalPrivateKey = privateKey;
        final String mark = GeneratorUtil.uuid() + GeneratorUtil.random();
        exchange.getRequest().getAttributes().put(VoucherAspect.MyServerHttpRequestDecorator.ACCOUNT_PASSWORD_SIGNATURE_MARK, mark);
        final String content = JsonUtil.toJson(new HashMap<>() {{
            put("mark", mark);
            put("public", finalPublicKey);
            put("private", finalPrivateKey);
        }});
        return cache
                .set(mark, content)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.executeCacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.AccountPasswordSignature.Request param)",
                        "password signature cache write exception"
                )))
                .map(b -> new PasswordSignatureContext.Dto().setContent(finalPublicKey));
    }

}
