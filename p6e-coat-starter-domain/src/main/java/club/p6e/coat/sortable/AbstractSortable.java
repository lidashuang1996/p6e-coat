package club.p6e.coat.sortable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
@Getter
public abstract class AbstractSortable<I extends AbstractSortable.Option> extends ArrayList<I> implements Serializable {

    @Data
    @AllArgsConstructor
    public static class Mapper implements Serializable {
        /**
         * 名称
         */
        private String name;

        /**
         * 字段
         */
        private String column;
    }

    @Data
    public static class Option implements Serializable {

        /**
         * 请求的内容
         */
        private String content;

        /**
         * 请求的条件
         */
        private String condition;
    }

    /**
     * AES 升序排列
     */
    public static final String ASC = "ASC";

    /**
     * DESC 降序排列
     */
    public static final String DESC = "DESC";

    /**
     * 验证情况
     * 默认为验证不通过的值
     */
    private boolean validation = false;

    /**
     * 提取映射数据
     *
     * @param clazz 模型类型
     * @return 映射数据
     */
    public static List<Mapper> extractMappings(Class<?> clazz) {
        final List<Mapper> mappers = new ArrayList<>();
        if (clazz != null) {
            final Field[] fields = clazz.getDeclaredFields();
            for (final Field field : fields) {
                final Sortable sortable = field.getAnnotation(Sortable.class);
                if (sortable != null) {
                    mappers.add(new Mapper(
                            sortable.name() == null || sortable.name().isEmpty() ? field.getName() : sortable.name(),
                            sortable.column() == null || sortable.column().isEmpty() ? field.getName() : sortable.column()
                    ));
                }
            }
        }
        return mappers;
    }

    /**
     * 验证参数是否合法
     *
     * @param clazz 模型类型
     * @return 验证情况
     */
    public boolean validation(Class<?> clazz) {
        return validationOptionsToMappings(clazz, this);
    }

    public boolean isValidationFailure(Class<?> clazz) {
        return !validation(clazz);
    }

    /**
     * 验证参数是否合法
     *
     * @param clazz   模型类型
     * @param context 排序上下文对象
     * @return 参数是否合法
     */
    protected boolean validationOptionsToMappings(Class<?> clazz, AbstractSortable<?> context) {
        if (clazz == null || context == null) {
            return false;
        } else {
            if (validationOptionsToMappings(context, extractMappings(clazz))) {
                context.validation = true;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 验证参数是否合法以及格式化请求参数
     *
     * @param mappers 映射数据
     * @param options 请求参数
     * @return 参数是否合法
     */
    protected boolean validationOptionsToMappings(AbstractSortable<?> options, List<Mapper> mappers) {
        if (mappers == null || mappers.isEmpty()) {
            return false;
        } else if (options == null || options.isEmpty()) {
            return true;
        } else {
            for (final Option option : options) {
                boolean bool = false;
                String content = option.getContent();
                String condition = option.getCondition();
                if (content != null && !content.isEmpty()) {
                    content = content.toLowerCase();
                } else {
                    return false;
                }
                if (condition != null && !condition.isEmpty()) {
                    condition = condition.toUpperCase();
                    condition = condition.equals(DESC) ? DESC : ASC;
                } else {
                    return false;
                }
                for (final Mapper mapper : mappers) {
                    if (mapper.getName().toLowerCase().equals(content)) {
                        bool = true;
                        content = mapper.getColumn();
                        break;
                    }
                }
                if (bool) {
                    option.setContent(content);
                    option.setCondition(condition);
                } else {
                    return false;
                }
            }
            return true;
        }
    }

}
