package club.p6e.coat.auth.token;

import club.p6e.coat.auth.UserBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;

/**
 * Blocking Local Storage JSON Web Token Cleaner
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BlockingLocalStorageJsonWebTokenCleaner implements BlockingTokenCleaner {

    /**
     * User Builder Object
     */
    protected final UserBuilder builder;

    /**
     * Json Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public BlockingLocalStorageJsonWebTokenCleaner(UserBuilder builder, JsonWebTokenCodec codec) {
        this.codec = codec;
        this.builder = builder;
    }

    @Override
    public Object execute(HttpServletRequest request, HttpServletResponse response) {
        return LocalDateTime.now();
    }

}
