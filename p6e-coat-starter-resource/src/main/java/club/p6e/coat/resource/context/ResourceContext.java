package club.p6e.coat.resource.context;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class ResourceContext implements Serializable {

    /**
     * Download Context Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Node
         */
        private String node;

        /**
         * Path
         */
        private String path;

        /**
         * Voucher
         */
        private String voucher;

        /**
         * Other
         */
        private Map<String, Object> other = new HashMap<>();

    }

    /**
     * Download Context Request
     */
    @Data
    @Accessors(chain = true)
    public static class Dto implements Serializable {

        /**
         * Node
         */
        private String node;

        /**
         * Path
         */
        private String path;

        /**
         * Voucher
         */
        private String voucher;

    }

}

