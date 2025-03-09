package club.p6e.coat.auth.client;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class AuthModel implements Serializable {

    @Data
    @Accessors(chain = true)
    public static class BaseResultModel implements Serializable {
        private Integer code;
        private String message;
    }

    @Data
    @Accessors(chain = true)
    public static class TokenResultModel implements Serializable {
        private Integer code;
        private String message;
        private TokenDataResultModel data;
    }

    @Data
    @Accessors(chain = true)
    public static class TokenDataResultModel implements Serializable {
        private String user;
        private String type;
        private Long expire;
        private String accessToken;
        private String refreshToken;
    }

    @Data
    @Accessors(chain = true)
    public static class UserModel implements Serializable {
        private Integer id;
    }

}
