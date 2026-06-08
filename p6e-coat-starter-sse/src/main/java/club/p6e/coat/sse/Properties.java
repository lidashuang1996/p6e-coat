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
         * Channel Port
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
             * [P1] 安全加固: 默认 origin 改为空列表，禁止通配符 "*"
             * 使用者必须显式配置允许跨域的来源域名
             */
            private List<String> origin = new ArrayList<>();

            /**
             * [P3] 代码质量: String[] 改为 List<String>
             * Channel Cross Domain Allow Headers
             */
            private List<String> headers = new ArrayList<>(List.of("Content-Type"));

            /**
             * [P3] 代码质量: String[] 改为 List<String>
             * Channel Cross Domain Allow Methods
             */
            private List<String> methods = new ArrayList<>(List.of("GET", "POST", "DELETE", "PUT", "OPTIONS"));

        }

    }

}