package club.p6e.coat.auth.token.web;

import club.p6e.coat.auth.User;
import club.p6e.coat.auth.UserBuilder;
import club.p6e.coat.auth.token.JsonWebTokenCodec;
import club.p6e.coat.common.utils.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Local Storage Json Web Token Validator
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class LocalStorageJsonWebTokenValidator implements TokenValidator {

    /**
     * Bearer Type
     */
    protected static final String BEARER_TYPE = "Bearer";

    /**
     * Request Parameter Name
     */
    protected static final String REQUEST_PARAMETER_NAME = "token";

    /**
     * Authorization Prefix
     */
    protected static final String AUTHORIZATION_PREFIX = BEARER_TYPE + " ";

    /**
     * Authorization Header Name
     */
    protected static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    /**
     * Json Web Token Codec Object
     */
    protected final JsonWebTokenCodec codec;

    /**
     * Constructor Initialization
     *
     * @param codec Json Web Token Codec Object
     */
    public LocalStorageJsonWebTokenValidator(JsonWebTokenCodec codec) {
        this.codec = codec;
    }

    @Override
    public User execute(HttpServletRequest request, HttpServletResponse response) {
        final String ht = request.getHeader(AUTHORIZATION_HEADER_NAME);
        final String qt = request.getParameter(REQUEST_PARAMETER_NAME);
        final List<String> list = new ArrayList<>();
        if (ht != null) {
            list.add(ht);
        }
        if (qt != null) {
            list.add(qt);
        }
        if (!list.isEmpty()) {
            String content;
            for (final String item : list) {
                if (item.startsWith(AUTHORIZATION_PREFIX)) {
                    content = codec.decryption(item.substring(AUTHORIZATION_PREFIX.length()));
                } else {
                    content = codec.decryption(item);
                }
                if (content != null) {
                    content = content.substring(content.indexOf("@") + 1);
                    return SpringUtil.getBean(UserBuilder.class).create(content);
                }
            }
        }
        return null;
    }

}
