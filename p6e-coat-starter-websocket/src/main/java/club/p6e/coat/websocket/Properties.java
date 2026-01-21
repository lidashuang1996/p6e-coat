package club.p6e.coat.websocket;

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
@ConfigurationProperties(prefix = "club.p6e.coat.websocket")
public class Properties {

    /**
     * Boss Threads
     */
    private int bossThreads = 1;

    /**
     * Worker Threads
     */
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * Manager Thread Pool Length
     */
    private int managerThreadPoolLength = 15;

    /**
     * Channel List Object
     */
    private final List<Channel> channels = new ArrayList<>();

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
         * Channel Type
         */
        private String type;

        /**
         * Channel Frame
         */
        private int frame = 65536;

        /**
         * Channel Path
         */
        private String path = "/ws";

        /**
         * Channel Auth Bean Name
         */
        private String auth = "club.p6e.coat.websocket.AuthServiceImpl";

        /**
         * Channel Callback Bean Name List
         */
        private List<String> callbacks = new ArrayList<>();

    }

}
