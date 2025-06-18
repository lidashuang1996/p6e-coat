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
     * Channel Config List Object
     */
    private final List<Channel> channels = new ArrayList<>();
    /**
     * Manager Thread Pool Length
     */
    private int managerThreadPoolLength = 15;

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
         * Channel Auth Bean Name
         */
        private String auth = "club.p6e.coat.sse.AuthServiceImpl";

    }
}
