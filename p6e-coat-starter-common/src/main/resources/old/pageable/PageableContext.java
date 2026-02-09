package club.p6e.coat.common.old.pageable;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Hashtable;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class PageableContext {

    private String all;
    private Integer page;
    private Integer size;

    public static PageableContext all() {
        return new PageableContext("1", 1, Integer.MAX_VALUE, PageableContext.class);
    }

    public static PageableContext build(Integer page, Integer size) {
        return new PageableContext(null, page, size, null);
    }

    public static PageableContext build(String all, Integer page, Integer size, Class<?> source) {
        return new PageableContext(all, page, size, source);
    }

    private static final Map<Class<?>, String> ALL_CACHE = new Hashtable<>() {{
        put(PageableContext.class, "1");
    }};

    public static void register(Class<?> clazz) {
        ALL_CACHE.put(clazz, "1");
    }

    private PageableContext(String all, Integer page, Integer size, Class<?> source) {
        if (all == null) {
            this.all = null;
            this.page = page;
            this.size = size;
        } else if (ALL_CACHE.get(source) == null) {
            this.all = null;
            this.page = page;
            this.size = size;
        } else {
            this.all = "1";
            this.page = 1;
            this.size = Integer.MAX_VALUE;
        }
    }
}
