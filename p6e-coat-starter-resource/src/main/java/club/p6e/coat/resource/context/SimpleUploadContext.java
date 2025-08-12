package club.p6e.coat.resource.context;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple Upload Context
 *
 * @author lidashuang
 * @version 1.0
 */
@Getter
public class SimpleUploadContext extends HashMap<String, Object> implements Serializable {


    /**
     * Simple Upload Context Request
     */
    @Data
    @Accessors(chain = true)
    public static class Request implements Serializable {

        /**
         * Node
         */
        private String node;

        /**
         * Voucher
         */
        private String voucher;

        /**
         * File
         */
        private MultipartFile file;

        /**
         * Other
         */
        private Map<String, Object> other = new HashMap<>();

    }

    /**
     * Simple Upload Context Request
     */
    @Data
    @Accessors(chain = true)
    public static class Dto implements Serializable {

        private Long size;
        private String name;
        private String storageType;
        private String storageLocation;

    }

}
