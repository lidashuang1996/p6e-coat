package club.p6e.coat.auth;

import club.p6e.coat.common.utils.GeneratorUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Json Web Token Codec
 *
 * @author lidashuang
 * @version 1.0
 */
public class JsonWebTokenCodec {

    /**
     * 注入日志对象
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(JsonWebTokenCodec.class);

    /**
     * 密钥
     */
    private String secret;

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

    public String encryption(String uid, String content, String secret) {
        final Date date = Date.from(LocalDateTime.now()
                .plusSeconds(EXPIRATION_TIME)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        );
        return JWT
                .create()
                .withAudience(uid)
                .withExpiresAt(date)
                .withSubject(content)
                .sign(Algorithm.HMAC256(secret));
    }

    public String decryption(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get Secret
     *
     * @return Secret
     */
    public String getSecret() {
        return this.secret;
    }

}
