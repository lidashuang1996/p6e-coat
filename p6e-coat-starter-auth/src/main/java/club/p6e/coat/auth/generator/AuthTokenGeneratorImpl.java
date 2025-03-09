package club.p6e.coat.auth.generator;

import club.p6e.coat.common.utils.GeneratorUtil;
import org.springframework.util.DigestUtils;

/**
 * 实现认证令牌
 *
 * @author lidashuang
 * @version 1.0
 */
public class AuthTokenGeneratorImpl implements
        // 生成认证令牌
        AuthAccessTokenGenerator,
        // 生成刷新令牌
        AuthRefreshTokenGenerator,
        // 生成令牌客户端认证令牌
        OAuth2TokenClientAuthAccessTokenGenerator,
        // 生成令牌客户端刷新令牌
        OAuth2TokenClientAuthRefreshTokenGenerator,
        // 生成令牌用户认证令牌
        OAuth2TokenUserAuthAccessTokenGenerator,
        // 生成令牌用户刷新令牌
        OAuth2TokenUserAuthRefreshTokenGenerator {

    @Override
    public String execute() {
        return GeneratorUtil.uuid() + DigestUtils.md5DigestAsHex(
                (GeneratorUtil.uuid() + GeneratorUtil.random(6, true, false)).toLowerCase().getBytes()
        ).toLowerCase();
    }

}
