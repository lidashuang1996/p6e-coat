package club.p6e.coat.common;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
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
     * 版本号
     */
    private String version = "unknown";

    /**
     * Security
     */
    @Data
    @Accessors(chain = true)
    public static class Security implements Serializable {

        /**
         * 是否启用
         */
        private boolean enable = false;

        /**
         * 凭证
         */
        private String[] vouchers = new String[]{};

    }

    /**
     * 安全
     */
    private Security security = new Security();

    /**
     * Cross Domain
     */
    @Data
    @Accessors(chain = true)
    public static class CrossDomain implements Serializable {
        /**
         * 是否启动
         */
        private boolean enable = false;

        /**
         * 白名单
         */
        private String[] whiteList = new String[]{};
    }

    /**
     * 安全
     */
    private CrossDomain crossDomain = new CrossDomain();

    /**
     * Snowflake
     */
    @Data
    @Accessors(chain = true)
    public static class Snowflake implements Serializable {
        private Integer workerId;
        private Integer dataCenterId;
    }

    /**
     * 雪花
     */
    private Map<String, Snowflake> snowflake = new HashMap<>();

}
