package club.p6e.cloud.gateway;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Properties
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "p6e.cloud.gateway")
public class Properties implements Serializable {

    /**
     * Log
     */
    private Log log = new Log();

    /**
     * Route Definition List
     */
    private List<RouteDefinition> routes = new ArrayList<>();

    /**
     * Log
     */
    @Data
    @Accessors(chain = true)
    public static class Log implements Serializable {

        /**
         * Enable
         */
        private boolean enable = false;

        /**
         * Enable Details
         */
        private boolean details = false;

    }

}