package club.p6e.coat.common.pageable;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Hashtable;
import java.util.Map;

/**
 * Pageable Context
 *
 * @author lidashuang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class PageableContext {

    /**
     * All Source Cache Object
     */
    private static final Map<Class<?>, String> ALL_CACHE = new Hashtable<>() {{
        put(PageableContext.class, "1");
    }};

    /**
     * All
     */
    private String all;

    /**
     * Page
     */
    private Integer page;

    /**
     * Size
     */
    private Integer size;

    /**
     * Get All Pageable Context Object
     *
     * @return All Pageable Context Object
     */
    public static PageableContext all() {
        return new PageableContext("1", 1, Integer.MAX_VALUE, PageableContext.class);
    }

    /**
     * Register All Pageable Context Object
     *
     * @param clazz Source Class Object
     */
    public static void register(Class<?> clazz) {
        ALL_CACHE.put(clazz, "1");
    }

    /**
     * Build Pageable Context Object
     *
     * @param page Page
     * @param size Size
     * @return Pageable Context Object
     */
    public static PageableContext build(Integer page, Integer size) {
        return new PageableContext(null, page, size, null);
    }

    /**
     * Build Pageable Context Object
     *
     * @param all    All
     * @param page   Page
     * @param size   Size
     * @param source Source Class Object
     * @return Pageable Context Object
     */
    public static PageableContext build(String all, Integer page, Integer size, Class<?> source) {
        return new PageableContext(all, page, size, source);
    }

    /**
     * Build Pageable Context Object
     *
     * @param all    All
     * @param page   Page
     * @param size   Size
     * @param source Source Class Object
     */
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
