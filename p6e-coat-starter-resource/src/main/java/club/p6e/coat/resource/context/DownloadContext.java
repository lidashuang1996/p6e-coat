package club.p6e.coat.resource.context;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Download Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class DownloadContext implements Serializable {

    /**
     * Download Context Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Node
         */
        private String node;

        /**
         * Path
         */
        private String path;

        /**
         * Voucher
         */
        private String voucher;

        /**
         * Other
         */
        private Map<String, Object> other = new HashMap<>();

    }

}
