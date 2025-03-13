package club.p6e.coat.auth;

import club.p6e.coat.auth.error.GlobalExceptionContext;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Getter;

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
     * Json Web Token Secret
     */
    private final String secret;

    /**
     * Is Init Status
     */
    private final boolean status;

    /**
     * Init
     *
     * @param secret Json Web Token Secret
     */
    public JsonWebTokenCodec(String secret) {
        this.status = true;
        this.secret = secret;
    }

    /**
     * Json Web Token Encryption
     *
     * @param id         ID
     * @param content    Content
     * @param expiration Encryption Expiration Date
     * @return Encryption Content
     */
    public String encryption(String id, String content, long expiration) {
        if (!status) {
            throw GlobalExceptionContext.exceptionJsonWebTokenSecretException(
                    this.getClass(),
                    "fun decryption(String token).",
                    "json web token encryption secret not init exception."
            );
        }
        return JWT.create().withAudience(id)
                .withExpiresAt(Date.from(LocalDateTime.now()
                        .plusSeconds(expiration).atZone(ZoneId.systemDefault()).toInstant()))
                .withSubject(content).sign(Algorithm.HMAC256(secret));
    }

    /**
     * Json Web Token Decryption
     *
     * @param token Json Web Token
     * @return Decryption Content
     */
    public String decryption(String token) {
        if (!status) {
            throw GlobalExceptionContext.exceptionJsonWebTokenSecretException(
                    this.getClass(),
                    "fun decryption(String token).",
                    "json web token decryption secret not init exception."
            );
        }
        try {
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

}
