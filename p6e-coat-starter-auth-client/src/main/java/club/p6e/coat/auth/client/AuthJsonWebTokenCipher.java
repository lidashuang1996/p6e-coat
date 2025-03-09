package club.p6e.coat.auth.client;

import club.p6e.coat.common.utils.GeneratorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 认证 JWT 加密解密的密钥
 *
 * @author lidashuang
 * @version 1.0
 */
@Component
public class AuthJsonWebTokenCipher {

    /**
     * 注入日志对象
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthJsonWebTokenCipher.class);

    /**
     * 密钥
     */
    private String accessTokenSecret;

    /**
     * 密钥
     */
    private String refreshTokenSecret;

    /**
     * 构造方法初始化
     *
     * @param properties 配置文件对象
     */
    public AuthJsonWebTokenCipher(Properties properties) {
        this.accessTokenSecret = properties.getJwtAccessTokenSecret();
        this.refreshTokenSecret = properties.getJwtRefreshTokenSecret();
    }

    /**
     * 初始化
     */
    public void init() {
        if (this.accessTokenSecret == null) {
            this.accessTokenSecret = DigestUtils.md5DigestAsHex(
                    ("AS_" + GeneratorUtil.uuid() + GeneratorUtil.random()).getBytes(StandardCharsets.UTF_8)
            );
            LOGGER.info("[ JWT ( ACCESS_TOKEN ) ] INIT >>> {}", this.accessTokenSecret);
        }
        if (this.refreshTokenSecret == null) {
            this.refreshTokenSecret = DigestUtils.md5DigestAsHex(
                    ("RS_" + GeneratorUtil.uuid() + GeneratorUtil.random()).getBytes(StandardCharsets.UTF_8)
            );
            LOGGER.info("[ JWT ( REFRESH_TOKEN ) ] INIT >>> {}", this.refreshTokenSecret);
        }
    }

    /**
     * 获取 ACCESS TOKEN 密钥
     *
     * @return ACCESS TOKEN 密钥
     */
    public String getAccessTokenSecret() {
        init();
        return accessTokenSecret;
    }

    /**
     * 获取 REFRESH TOKEN 密钥
     *
     * @return REFRESH TOKEN 密钥
     */
    public String getRefreshTokenSecret() {
        init();
        return refreshTokenSecret;
    }

    /**
     * 设置 ACCESS TOKEN 密钥
     *
     * @param secret ACCESS TOKEN 密钥
     */
    public void setAccessTokenSecret(String secret) {
        this.accessTokenSecret = secret;
        LOGGER.info(this.accessTokenSecret);
    }

    /**
     * 设置 REFRESH TOKEN 密钥
     *
     * @param secret REFRESH TOKEN 密钥
     */
    public void setRefreshTokenSecret(String secret) {
        this.refreshTokenSecret = secret;
        LOGGER.info(this.refreshTokenSecret);
    }
}
