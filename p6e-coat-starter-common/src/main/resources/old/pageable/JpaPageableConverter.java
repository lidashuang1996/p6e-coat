package club.p6e.coat.common.old.pageable;

import org.springframework.data.domain.PageRequest;

/**
 * Jpa Pageable Converter
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public class JpaPageableConverter {

    /**
     * MIN PAGE
     */
    public static int MIN_PAGE = 1;

    /**
     * DEFAULT PAGE
     */
    public static int DEFAULT_PAGE = 1;

    /**
     * MIN SIZE
     */
    public static int MIN_SIZE = 1;

    /**
     * MAX SIZE
     */
    public static int MAX_SIZE = 200;

    /**
     * DEFAULT SIZE
     */
    public static int DEFAULT_SIZE = 16;

    /**
     * Pageable Context Object To Jpa Pageable Page Request Object
     *
     * @param context Pageable Context Object
     * @return Jpa Pageable Page Request Object
     */
    public static PageRequest execute(PageableContext context) {
        if (context != null) {
            if (context.getAll() == null) {
                return execute(context.getPage(), context.getSize());
            } else {
                return PageRequest.of(0, Integer.MAX_VALUE);
            }
        }
        return null;
    }

    /**
     * Execute Page/Size To Jpa Pageable Page Request Object
     *
     * @param page Page
     * @param size Size
     * @return Jpa Pageable Page Request Object
     */
    private static PageRequest execute(Integer page, Integer size) {
        if (page == null) {
            page = DEFAULT_PAGE;
        } else {
            page = page < MIN_PAGE ? MIN_PAGE : page;
        }
        if (size == null) {
            size = DEFAULT_SIZE;
        } else {
            size = size < MIN_SIZE ? DEFAULT_SIZE : (size > MAX_SIZE ? MAX_SIZE : size);
        }
        return PageRequest.of(page - 1, size);
    }
}
