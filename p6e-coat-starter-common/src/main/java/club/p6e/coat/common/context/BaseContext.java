package club.p6e.coat.common.context;

import club.p6e.coat.common.pageable.PageableContext;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Context
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BaseContext implements Serializable {

    /**
     * Base Context Extension Param
     */
    @Data
    @Accessors(chain = true)
    public static class ExtensionParam implements Serializable {

        /**
         * Extension
         */
        private List<String> extension;

    }

    /**
     * Base Context Paging Param
     */
    @Data
    @Accessors(chain = true)
    public static class PagingParam implements Serializable {

        /**
         * All
         */
        private String all;

        /**
         * Size
         */
        private Integer size;

        /**
         * Page
         */
        private Integer page;

        /**
         * Get Pageable Context Object
         *
         * @return Pageable Context Object
         */
        public PageableContext getPageable() {
            return PageableContext.build(this.all, this.page, this.size, this.getClass());
        }
    }

    /**
     * Base Context Paging Extension Param
     */
    @Data
    @Accessors(chain = true)
    public static class PagingExtensionParam implements Serializable {

        /**
         * All
         */
        private String all;

        /**
         * Size
         */
        private Integer size;

        /**
         * Page
         */
        private Integer page;

        /**
         * Extension
         */
        private List<String> extension;

        /**
         * Get Pageable Context Object
         *
         * @return Pageable Context Object
         */
        public PageableContext getPageable() {
            return PageableContext.build(this.all, this.page, this.size, this.getClass());
        }
    }

    /**
     * Base Context Paging Extension List Result
     */
    @Data
    @Accessors(chain = true)
    public static class ListResult implements Serializable {

        /**
         * Total
         */
        private Long total;

        /**
         * Size
         */
        private Integer size;

        /**
         * Page
         */
        private Integer page;

    }

    /**
     * Base Context Paging Extension Result
     */
    @Data
    @Accessors(chain = true)
    public static class ExtensionResult implements Serializable {

        /**
         * Extension
         */
        private Map<String, Object> extension = new HashMap<>();

    }

}
