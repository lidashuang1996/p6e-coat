package club.p6e.coat.auth.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Password Signature Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class PasswordSignatureContext implements Serializable {


    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Custom Data
         */
        private Map<String, Object> data = new HashMap<>();

    }

    @Data
    @Accessors(chain = true)
    public static class Dto implements Serializable {

        /**
         * Content
         */
        private String content;

    }

}
