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
 * 配置文件
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Component
@Accessors(chain = true)
@ConditionalOnMissingBean(
        value = Properties.class,
        ignored = Properties.class
)
@ConfigurationProperties(prefix = "p6e.coat.file")
public class Properties implements Serializable {

    /**
     * 分片上传
     */
    private SliceUpload sliceUpload = new SliceUpload();

    /**
     * 分片上传类
     */
    @Data
    @Accessors(chain = true)
    public static class SliceUpload implements Serializable {

        /**
         * 基础的文件路径
         */
        private String path = "/opt/data/p6e/file/slice";

        /**
         * 允许上传的文件大小的最大值
         */
        private long maxSize = 1024 * 1024 * 30;

    }

    /**
     * 上传
     */
    private Map<String, Upload> uploads = new HashMap<>();

    /**
     * 上传
     */
    @Data
    @Accessors(chain = true)
    public static class Upload implements Serializable {

        /**
         * 资源类型
         */
        private String type = "DISK";

        /**
         * 基础的文件路径
         */
        private String path;

        /**
         * 扩展参数
         */
        private Map<String, String> extend = new HashMap<>();

    }

    /**
     * 下载
     */
    private Map<String, Download> downloads = new HashMap<>();

    /**
     * 下载
     */
    @Data
    @Accessors(chain = true)
    public static class Download implements Serializable {

        /**
         * 资源类型
         */
        private String type = "DISK";

        /**
         * 基础的文件路径
         */
        private String path;

        /**
         * 扩展参数
         */
        private Map<String, String> extend = new HashMap<>();

    }

    /**
     * 资源配置
     */
    private Map<String, Resource> resources = new HashMap<>();

    /**
     * 资源
     */
    @Data
    @Accessors(chain = true)
    public static class Resource implements Serializable {

        /**
         * 资源类型
         */
        private String type = "DISK";

        /**
         * 基础的文件路径
         */
        private String path;

        /**
         * 扩展参数
         */
        private Map<String, String> extend = new HashMap<>();

        /**
         * 允许的文件后缀以及对应的媒体类型
         */
        private Map<String, MediaType> suffixes = new HashMap<>();

    }

}