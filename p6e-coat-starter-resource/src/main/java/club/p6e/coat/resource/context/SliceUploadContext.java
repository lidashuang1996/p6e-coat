package club.p6e.coat.resource.context;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.codec.multipart.FilePart;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Slice Upload Context
 *
 * @author lidashuang
 * @version 1.0
 */
public class SliceUploadContext extends HashMap<String, Object> implements Serializable {

    /**
     * Slice Upload Context Open
     */
    public static class Open implements Serializable {

        /**
         * Slice Upload Context Open Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * Name
             */
            private String name;

            /**
             * Voucher
             */
            private String voucher;

            /**
             * Other
             */
            private Map<String, Object> other = new HashMap<>();

        }

        /**
         * Slice Upload Context Open Vo
         */
        @Data
        public static class Vo implements Serializable {

            /**
             * ID
             */
            private Integer id;

            /**
             * Name
             */
            private String name;

        }

        /**
         * Slice Upload Context Open Dto
         */
        @Data
        public static class Dto implements Serializable {

            /**
             * ID
             */
            private Integer id;

            /**
             * Name
             */
            private String name;

        }

    }

    /**
     * Slice Upload Context Close
     */
    public static class Close implements Serializable {

        /**
         * Slice Upload Context Close Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * ID
             */
            private Integer id;

            /**
             * Node
             */
            private String node;

            /**
             * Voucher
             */
            private String voucher;

            /**
             * Other
             */
            private Map<String, Object> other = new HashMap<>();

        }

        /**
         * Slice Upload Context Close Vo
         */
        @Data
        public static class Vo implements Serializable {
            private Integer id;
        }

        /**
         * Slice Upload Context Close Dto
         */
        @Data
        public static class Dto implements Serializable {
            private Integer id;
        }

    }

    /**
     * Slice Upload Context Chunk
     */
    public static class Chunk implements Serializable {

        /**
         * Slice Upload Context Chunk Request
         */
        @Data
        @Accessors(chain = true)
        public static class Request implements Serializable {

            /**
             * ID
             */
            private Integer id;

            /**
             * Node
             */
            private String node;

            /**
             * Voucher
             */
            private String voucher;

            /**
             * Index
             */
            private Integer index;

            /**
             * Signature
             */
            private String signature;

            /**
             * File Part
             */
            private FilePart filePart;

            /**
             * Other
             */
            private Map<String, Object> other = new HashMap<>();

        }

        /**
         * Slice Upload Context Chunk Vo
         */
        @Data
        public static class Vo implements Serializable {
            private Integer id;
        }

        /**
         * Slice Upload Context Chunk Dto
         */
        @Data
        public static class Dto implements Serializable {
            private Integer id;
        }

    }

}
