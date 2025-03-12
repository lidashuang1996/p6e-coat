package club.p6e.coat.auth.web.reactive.service;

import club.p6e.coat.auth.web.reactive.ServerHttpRequest;
import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.auth.web.reactive.cache.AccountPasswordLoginSignatureCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.RsaUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * Account Password Login Signature Service Impl
 *
 * @author lidashuang
 * @version 1.0
 */
public class AccountPasswordLoginSignatureServiceImpl implements AccountPasswordLoginSignatureService {

    /**
     * Account Password Login Signature Cache Object
     */
    private final AccountPasswordLoginSignatureCache cache;

    /**
     * Constructor Initialization
     *
     * @param cache Account Password Login Signature Cache Object
     */
    public AccountPasswordLoginSignatureServiceImpl(AccountPasswordLoginSignatureCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<LoginContext.AccountPasswordSignature.Dto> execute(
            ServerWebExchange exchange, LoginContext.AccountPasswordSignature.Request param) {
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
            put("publicKey", finalPublicKey);
            put("privateKey", finalPrivateKey);
        }});
        return cache
                .set(mark, content)
                .flatMap(b -> b ?
                        Mono.just(new LoginContext.AccountPasswordSignature.Dto().setContent(finalPublicKey))
                        : Mono.error(GlobalExceptionContext.exceptionCacheWriteException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, " +
                                "LoginContext.AccountPasswordSignature.Request param).",
                        "account password login signature cache exception."
                )));
    }

}
