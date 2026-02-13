package club.p6e.coat.common;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Properties
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "p6e.coat.common")
@Component(value = "club.p6e.coat.common.Properties")
public class Properties implements Serializable {

    /**
     * Debug
     */
    private boolean debug = false;

    /**
     * Security
     */
    private Security security = new Security();

    /**
     * Cross Domain
     */
    private CrossDomain crossDomain = new CrossDomain();

    /**
     * Snowflake
     */
    private Map<String, Snowflake> snowflake = new HashMap<>();

    /**
     * Security
     */
    @Data
    @Accessors(chain = true)
    public static class Security implements Serializable {

        /**
         * Enable
         */
        private boolean enable = false;

        /**
         * Header
         */
        private String header = "P6e-Voucher";

        /**
         * Vouchers
         */
        private List<String> vouchers = new ArrayList<>();

    }

    /**
     * Cross Domain
     */
    @Data
    @Accessors(chain = true)
    public static class CrossDomain implements Serializable {

        /**
         * Enable
         */
        private boolean enable = false;

        /**
         * White List
         */
        private List<String> whiteList = new ArrayList<>();

    }

    /**
     * Snowflake
     */
    @Data
    @Accessors(chain = true)
    public static class Snowflake implements Serializable {

        /**
         * Worker ID
         */
        private Integer workerId;

        /**
         * Data Center ID
         */
        private Integer dataCenterId;

    }

}
