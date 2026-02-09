package club.p6e.coat.common.old.pageable.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lidashuang
 * @version 1.0
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Searchable {

    /**
     * 搜索的名称
     *
     * @return 搜索的名称
     */
    String name() default "";

    /**
     * 字段的名称
     *
     * @return 字段的名称
     */
    String column() default "";

}
