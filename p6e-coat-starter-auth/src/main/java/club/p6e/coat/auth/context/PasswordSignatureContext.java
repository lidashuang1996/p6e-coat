package club.p6e.coat.auth.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author lidashuang
 * @version 1.0
 */
public class PasswordSignatureContext {
    /**
     * Account Password Signature
     */
    public static class AccountPasswordSignature implements Serializable {

        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {
        }

        @Data
        @Accessors(chain = true)
        public static class Vo implements Serializable {
            private String content;
        }


        @Data
        @Accessors(chain = true)
        public static class Dto implements Serializable {
            private String content;
        }

    }

}
