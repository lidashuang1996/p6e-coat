package club.p6e.coat.auth.token;

import club.p6e.coat.common.exception.JsonWebTokenSecretException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Json Web Token Codec
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public class JsonWebTokenCodec {

    /**
     * Inject Log Object
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonWebTokenCodec.class);

    /**
     * Is Init
     */
    private final boolean init;

    /**
     * Json Web Token Secret
     */
    private final String secret;

    /**
     * Init
     *
     * @param secret Json Web Token Secret
     */
    public JsonWebTokenCodec(String secret) {
        this.init = true;
        this.secret = secret;
    }

    /**
     * Json Web Token Algorithm
     *
     * @return Json Web Token Algorithm Object
     */
    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    /**
     * JSON Web Token Encryption
     *
     * @param id         ID
     * @param content    Content
     * @param expiration Encryption Expiration Date
     * @return Encryption Content
     */
    public String encryption(String id, String content, long expiration) {
        if (!init) {
            throw new JsonWebTokenSecretException(
                    this.getClass(),
                    "fun decryption(String token)",
                    "json web token encryption secret not init exception"
            );
        }
        final LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(expiration);
        return JWT.create()
                .withAudience(id)
                .withSubject(content)
                .withExpiresAt(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()))
                .sign(algorithm());
    }

    /**
     * JSON Web Token Decryption
     *
     * @param token Json Web Token
     * @return Decryption Content
     */
    public String decryption(String token) {
        if (!init) {
            throw new JsonWebTokenSecretException(
                    this.getClass(),
                    "fun decryption(String token)",
                    "json web token decryption secret not init exception"
            );
        }
        try {
            return JWT.require(algorithm()).build().verify(token).getSubject();
        } catch (Exception e) {
            LOGGER.warn("json web token decryption failed: {}", e.getMessage());
            return null;
        }
    }

}
