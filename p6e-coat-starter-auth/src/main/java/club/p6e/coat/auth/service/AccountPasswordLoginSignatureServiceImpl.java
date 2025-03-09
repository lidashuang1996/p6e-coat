package club.p6e.coat.auth.service;

import club.p6e.coat.auth.codec.PasswordTransmissionCodec;
import club.p6e.coat.common.utils.JsonUtil;
import club.p6e.coat.auth.AuthVoucher;
import club.p6e.coat.auth.cache.PasswordSignatureCache;
import club.p6e.coat.auth.context.LoginContext;
import club.p6e.coat.auth.error.GlobalExceptionContext;
import club.p6e.coat.auth.generator.AccountPasswordLoginSignatureGenerator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
    private final PasswordSignatureCache cache;

    /**
     * 传输编码解码器
     */
    private final PasswordTransmissionCodec codec;

    /**
     * 标记生成器
     */
    private final AccountPasswordLoginSignatureGenerator generator;

    /**
     * 构造方法初始化
     *
     * @param codec     传输编码解码器
     * @param cache     缓存对象
     * @param generator 标记生成器对象
     */
    public AccountPasswordLoginSignatureServiceImpl(
            PasswordSignatureCache cache,
            PasswordTransmissionCodec codec,
            AccountPasswordLoginSignatureGenerator generator) {
        this.cache = cache;
        this.codec = codec;
        this.generator = generator;
    }

    @Override
    public Mono<LoginContext.AccountPasswordSignature.Dto> execute(
            ServerWebExchange exchange, LoginContext.AccountPasswordSignature.Request param) {
        return AuthVoucher
                .init(exchange)
                .flatMap(v -> {
                    final PasswordTransmissionCodec.Model model = codec.generate();
                    final String json = JsonUtil.toJson(model);
                    if (json == null) {
                        return Mono.error(GlobalExceptionContext.exceptionAccountPasswordLoginTransmissionException(
                                this.getClass(),
                                "fun execute(ServerWebExchange exchange, " +
                                        "LoginContext.AccountPasswordSignature.Request param).",
                                "Account password login signature key generation exception."
                        ));
                    }
                    final String mark = generator.execute();
                    return cache
                            .set(mark, json)
                            .flatMap(b -> b ?
                                    v.setAccountPasswordCodecMark(mark)
                                    : Mono.error(GlobalExceptionContext.exceptionCacheWriteException(
                                    this.getClass(),
                                    "fun execute(ServerWebExchange exchange, " +
                                            "LoginContext.AccountPasswordSignature.Request param).",
                                    "Account password login signature cache write exception."
                            )))
                            .map(rv -> new LoginContext.AccountPasswordSignature.Dto().setContent(model.getPublicKey()));
                });
    }

}
