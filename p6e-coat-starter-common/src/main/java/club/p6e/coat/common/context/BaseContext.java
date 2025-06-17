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
        private List<String> extension;
    }

    /**
     * Base Context Paging Param
     */
    @Data
    @Accessors(chain = true)
    public static class PagingParam implements Serializable {
        private String all;
        private Integer size;
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
        private String all;
        private Integer size;
        private Integer page;
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
        private Long total;
        private Integer size;
        private Integer page;
    }

    /**
     * Base Context Paging Extension Result
     */
    @Data
    @Accessors(chain = true)
    public static class ExtensionResult implements Serializable {
        private Map<String, Object> extension = new HashMap<>();
    }

}
