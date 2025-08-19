package club.p6e.coat.resource.context;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;

import java.io.Serializable;
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
        private FilePart file;

        /**
         * Other
         */
        private Map<String, Object> other = new HashMap<>();

    }

    /**
     * Simple Upload Context Vo
     */
    @Data
    @Accessors(chain = true)
    public static class Vo implements Serializable {

        /**
         * Size
         */
        private Long size;

        /**
         * Name
         */
        private String name;

        /**
         * Storage Type
         */
        private String storageType;

        /**
         * Storage Location
         */
        private String storageLocation;

    }

    /**
     * Simple Upload Context Dto
     */
    @Data
    @Accessors(chain = true)
    public static class Dto implements Serializable {

        /**
         * Size
         */
        private Long size;

        /**
         * Name
         */
        private String name;

        /**
         * Storage Type
         */
        private String storageType;

        /**
         * Storage Location
         */
        private String storageLocation;

    }

}
