package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.context.PasswordSignatureContext;
import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.auth.web.reactive.cache.PasswordSignatureCache;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.RsaUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Account Password Login Signature Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
@ConditionalOnMissingBean(
        value = PasswordSignatureService.class,
        ignored = PasswordSignatureServiceImpl.class
)
public class PasswordSignatureServiceImpl implements PasswordSignatureService {

    /**
     * Account Password Login Signature Cache Object
     */
    private final PasswordSignatureCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Account Password Login Signature Cache Object
     */
    public PasswordSignatureServiceImpl(PasswordSignatureCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<PasswordSignatureContext.Dto> execute(ServerWebExchange exchange, PasswordSignatureContext.Request param) {
        String publicKey = null;
        String privateKey = null;
        try {
            final RsaUtil.KeyModel model = RsaUtil.generateKeyPair();
            publicKey = model.getPublicKey();
            privateKey = model.getPrivateKey();
        } catch (Exception e) {
            // ignore exception
        }
        final String finalPublicKey = publicKey;
        final String finalPrivateKey = privateKey;
        final String mark = GeneratorUtil.uuid() + GeneratorUtil.random();
        final ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();
        request.setAccountPasswordSignatureMark(mark);
        final String content = JsonUtil.toJson(new HashMap<>() {{
            put("mark", mark);
            put("public", finalPublicKey);
            put("private", finalPrivateKey);
        }});
        return cache
                .set(mark, content)
                .switchIfEmpty(Mono.error(GlobalExceptionContext.exceptionCacheWriteException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, " +
                                "LoginContext.AccountPasswordSignature.Request param).",
                        "account password login signature cache exception."
                )))
                .flatMap(b -> Mono.just(new PasswordSignatureContext.Dto().setContent(finalPublicKey)));
    }

}
