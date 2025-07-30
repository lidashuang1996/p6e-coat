package club.p6e.coat.sse;

import lombok.Data;
import lombok.experimental.Accessors;

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
public class Config {

    /**
     * Manager Thread Pool Length
     */
    private int managerThreadPoolLength = 15;

    /**
     * Channel Config List Object
     */
    private final List<Channel> channels = new ArrayList<>();

    /**
     * Boss Threads
     */
    private int bossThreads = 1;

    /**
     * Worker Threads
     */
    private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;

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
        private int frame = 65536;

        /**
         * Channel Auth Bean Name
         */
        private String auth = "club.p6e.coat.sse.AuthServiceImpl";

    }
}
