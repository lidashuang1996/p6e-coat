package club.p6e.coat.resource;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
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
@Component
@Accessors(chain = true)
@ConditionalOnMissingBean(Properties.class)
@ConfigurationProperties(prefix = "p6e.coat.resource")
public class Properties implements Serializable {

    /**
     * Uploads
     */
    private Map<String, Upload> uploads = new HashMap<>();

    /**
     * Downloads
     */
    private Map<String, Download> downloads = new HashMap<>();

    /**
     * Resources
     */
    private Map<String, Resource> resources = new HashMap<>();

    /**
     * Upload
     */
    @Data
    @Accessors(chain = true)
    public static class Upload implements Serializable {

        /**
         * Type
         * DISK
         * QI_NIU_CLOUD_OBJECT_STORAGE
         * ALIBABA_CLOUD_OBJECT_STORAGE
         * TENCENT_CLOUD_OBJECT_STORAGE
         */
        private String type = "DISK";

        /**
         * File Path
         */
        private String path;

        /**
         * File Max Size
         */
        private long max = 1024 * 1024 * 30;

        /**
         * Other Parameters
         */
        private Map<String, String> other = new HashMap<>();

        /**
         * Slice
         */
        private Slice slice = new Slice();

        /**
         * Slice
         */
        @Data
        @Accessors(chain = true)
        public static class Slice implements Serializable {

            /**
             * File Path
             */
            private String path;

        }

    }

    /**
     * Download
     */
    @Data
    @Accessors(chain = true)
    public static class Download implements Serializable {

        /**
         * Type
         * DISK
         * QI_NIU_CLOUD_OBJECT_STORAGE
         * ALIBABA_CLOUD_OBJECT_STORAGE
         * TENCENT_CLOUD_OBJECT_STORAGE
         */
        private String type = "DISK";

        /**
         * File Path
         */
        private String path;

        /**
         * Other Parameters
         */
        private Map<String, String> other = new HashMap<>();

    }

    /**
     * Resource
     */
    @Data
    @Accessors(chain = true)
    public static class Resource implements Serializable {

        /**
         * Type
         * DISK
         * QI_NIU_CLOUD_OBJECT_STORAGE
         * ALIBABA_CLOUD_OBJECT_STORAGE
         * TENCENT_CLOUD_OBJECT_STORAGE
         */
        private String type = "DISK";

        /**
         * File Path
         */
        private String path;

        /**
         * Other Parameters
         */
        private Map<String, String> other = new HashMap<>();

        /**
         * Suffixes / File Suffixes / File Media Types
         * file suffixes and corresponding media types allowed for preview
         */
        private Map<String, MediaType> suffixes = new HashMap<>();

    }

}