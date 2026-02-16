package club.p6e.coat.auth.service;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.aspect.ReactiveVoucherAspect;
import club.p6e.coat.auth.cache.ReactivePasswordSignatureCache;
import club.p6e.coat.common.exception.CacheException;
import club.p6e.coat.common.exception.CodecException;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.common.utils.RsaUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Reactive Password Signature Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component("club.p6e.coat.auth.service.ReactivePasswordSignatureServiceImpl")
@ConditionalOnClass(name = "org.springframework.web.reactive.DispatcherHandler")
public class ReactivePasswordSignatureServiceImpl implements ReactivePasswordSignatureService {

    /**
     * Reactive Password Signature Cache Object
     */
    private final ReactivePasswordSignatureCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Reactive Password Signature Cache Object
     */
    public ReactivePasswordSignatureServiceImpl(ReactivePasswordSignatureCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<PasswordSignatureContext.Dto> execute(ServerWebExchange exchange, PasswordSignatureContext.Request param) {
        String publicKey;
        String privateKey;
        try {
            final RsaUtil.KeyModel model = RsaUtil.generateKeyPair();
            publicKey = model.publicKey();
            privateKey = model.privateKey();
        } catch (Exception e) {
            return Mono.error(new CodecException(
                    this.getClass(),
                    "fun execute(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, PasswordSignatureContext.Request param) >>> " + e.getMessage(),
                    "password signature rsa exception"
            ));
        }
        final String finalPublicKey = publicKey;
        final String finalPrivateKey = privateKey;
        final String mark = GeneratorUtil.uuid() + GeneratorUtil.random();
        exchange.getRequest().getAttributes().put(ReactiveVoucherAspect.MyServerHttpRequestDecorator.ACCOUNT_PASSWORD_SIGNATURE_MARK, mark);
        final String content = JsonUtil.toJson(new HashMap<>() {{
            put("mark", mark);
            put("public", finalPublicKey);
            put("private", finalPrivateKey);
        }});
        return cache
                .set(mark, content)
                .switchIfEmpty(Mono.error(new CacheException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, LoginContext.AccountPasswordSignature.Request param)",
                        "password signature cache write exception"
                )))
                .map(b -> new PasswordSignatureContext.Dto().setContent(finalPublicKey));
    }

}
