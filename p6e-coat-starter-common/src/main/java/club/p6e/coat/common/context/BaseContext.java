package club.p6e.coat.common.context;

import club.p6e.coat.common.pageable.PageableContext;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础的上下文对象
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class BaseContext implements Serializable {

    @Data
    @Accessors(chain = true)
    public static class ExtensionParam implements Serializable {
        private List<String> extension;
    }

    @Data
    @Accessors(chain = true)
    public static class PagingParam implements Serializable {
        private String all;
        private Integer size;
        private Integer page;

        public PageableContext getPageable() {
            return PageableContext.build(this.all, this.page, this.size, this.getClass());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class PagingExtensionParam implements Serializable {
        private String all;
        private Integer size;
        private Integer page;
        private List<String> extension;

        public PageableContext getPageable() {
            return PageableContext.build(this.all, this.page, this.size, this.getClass());
        }
    }

    @Data
    @Accessors(chain = true)
    public static class ListResult implements Serializable {
        private Integer size;
        private Integer page;
        private Long total;
    }

    @Data
    @Accessors(chain = true)
    public static class ExtensionResult implements Serializable {
        private Map<String, Object> extension = new HashMap<>();
    }

}
