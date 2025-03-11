package club.p6e.coat.auth.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 注册的上下文对象
 *
 * @author lidashuang
 * @version 1.0
 */
public class RegisterContext implements Serializable {

    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {
        private String code;
        private String password;
    }

    @Data
    @Accessors(chain = true)
    public static class Vo implements Serializable {
        private String account;
    }

    @Data
    @Accessors(chain = true)
    public static class Dto implements Serializable {
        private String account;
    }

    /**
     * 验证码获取上下文
     */
    public static class Acquisition implements Serializable {

        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
            private String account;
            private String language;
        }

        @Data
        @Accessors(chain = true)
        public static class Vo implements Serializable {
            private String account;
        }

        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private String account;
        }
        
    }

}
