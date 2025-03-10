package club.p6e.coat.auth.service;

import club.p6e.coat.common.utils.GeneratorUtil;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.auth.cache.AccountPasswordLoginSignatureCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.common.utils.RsaUtil;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;

/**
 * 账号密码登录的密码签名服务的实现
 *
 * @author lidashuang
 * @version 1.0
 */
public class AccountPasswordLoginSignatureServiceImpl implements AccountPasswordLoginSignatureService {

    /**
     * 缓存对象
     */
    private final AccountPasswordLoginSignatureCache cache;

    /**
     * 构造方法初始化
     *
     * @param cache 缓存对象
     */
    public AccountPasswordLoginSignatureServiceImpl(AccountPasswordLoginSignatureCache cache) {
        this.cache = cache;
    }

    @Override
    public Mono<LoginContext.AccountPasswordSignature.Dto> execute(ServerWebExchange exchange, LoginContext.AccountPasswordSignature.Request param) {
        String publicKey = null;
        String privateKey = null;
        try {
            final RsaUtil.KeyModel model = RsaUtil.generateKeyPair();
            publicKey = model.getPrivateKey();
            privateKey = model.getPrivateKey();
        } catch (Exception e) {
            // ignore exception
        }
        final String finalPublicKey = publicKey;
        final String finalPrivateKey = privateKey;
        final String mark = GeneratorUtil.uuid() + GeneratorUtil.random(8, false, false);
        final String content = JsonUtil.toJson(new HashMap<>(){{
            put("mark", mark);
            put("publicKey", finalPublicKey);
            put("privateKey", finalPrivateKey);
        }});
        return cache
                .set(mark, content)
                .flatMap(b -> b ?
                        v.setAccountPasswordCodecMark(mark)
                        : Mono.error(GlobalExceptionContext.exceptionCacheWriteException(
                        this.getClass(),
                        "fun execute(ServerWebExchange exchange, " +
                                "LoginContext.AccountPasswordSignature.Request param).",
                        "Account password login signature cache write exception."
                )))
                .map(rv -> new LoginContext.AccountPasswordSignature.Dto().setContent(model.getPublicKey()));
    }

}
