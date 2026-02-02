package club.p6e.coat.auth.oauth2.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Info Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class InfoContext implements Serializable {

    /**
     * Info Context / Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Token
         */
        private String token;

        /**
         * Custom Data
         */
        private Map<String, String> data = new HashMap<>();

    }

}
