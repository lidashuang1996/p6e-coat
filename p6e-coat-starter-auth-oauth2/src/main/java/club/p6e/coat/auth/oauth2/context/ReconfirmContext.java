package club.p6e.coat.auth.oauth2.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Reconfirm Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class ReconfirmContext implements Serializable {

    /**
     * Reconfirm Context / Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Voucher
         */
        private String voucher;

        /**
         * Custom Data
         */
        private Map<String, String> data = new HashMap<>();

    }

}
