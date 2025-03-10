package club.p6e.coat.common.pageable;

import org.springframework.data.domain.PageRequest;

/**
 * @author lidashuang
 * @version 1.0
 */
public class JpaPageableConverter {

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

    private static PageRequest execute(Integer page, Integer size) {
        if (page == null) {
            page = 1;
        } else {
            page = page < 1 ? 1 : page;
        }
        if (size == null) {
            size = 16;
        } else {
            size = size < 1 ? 16 : (size > 200 ? 200 : size);
        }
        return PageRequest.of(page - 1, size);
    }
}
