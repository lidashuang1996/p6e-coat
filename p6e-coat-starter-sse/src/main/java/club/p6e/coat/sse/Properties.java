package club.p6e.coat.sse;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Config
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@ConfigurationProperties(prefix = "club.p6e.coat.sse")
public class Properties {

    /**
     * Boss Threads
     */
    private Integer bossThreads = 1;

    /**
     * Worker Threads
     */
    private Integer workerThreads = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * Manager Thread Pool Length
     */
    private Integer managerThreadPoolLength = 15;

    /**
     * Channel List Object
     */
    private List<Channel> channels = new ArrayList<>();

    /**
     * Channel
     */
    @Data
    @Accessors(chain = true)
    public static class Channel implements Serializable {

        /**
         * Channel Name
         */
        private String name;

        /**
         * Channel Prot
         */
        private Integer port;

        /**
         * Channel Frame
         */
        private Integer frame = 65536;

        /**
         * Channel Auth Bean Name
         */
        private String auth = "club.p6e.coat.sse.AuthServiceImpl";

        /**
         * Channel Cross Domain
         */
        private CrossDomain crossDomain = new CrossDomain();

        /**
         * Channel Cross Domain
         */
        @Data
        @Accessors(chain = true)
        public static class CrossDomain implements Serializable {

            /**
             * Channel Cross Domain Enable
             */
            private Boolean enable = true;

            /**
             * Channel Cross Domain Max Age
             */
            private Integer maxAge = 3600;

            /**
             * Channel Cross Domain Origin
             */
            private String[] origin = new String[]{"*"};

            /**
             * Channel Cross Domain Allow Headers
             */
            private String[] headers = new String[]{"Content-Type"};

            /**
             * Channel Cross Domain Allow Methods
             */
            private String[] methods = new String[]{"GET", "POST", "DELETE", "PUT", "OPTIONS"};

        }

    }

}
