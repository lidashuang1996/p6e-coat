package club.p6e.coat.common.old.pageable.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author lidashuang
 * @version 1.0
 */
@Getter
public abstract class SearchableAbstract<I extends SearchableAbstract.Option> extends ArrayList<I> implements Serializable {

    @Data
    @AllArgsConstructor
    public static class Mapper implements Serializable {
        private String name;
        private String column;
    }

    @Data
    public static class Option implements Serializable {
        private String key;
        private String value;
        private String condition;
        private String relationship;
        private String type;
    }

    @Data
    public static class Mixin implements Serializable {
        private Option data;
        private List<Mixin> list;

        public Mixin(Option data) {
            this.data = data;
        }

        public Mixin(Option data, List<Mixin> list) {
            this.data = data;
            this.list = list;
        }
    }

    public static final String LEFT_TYPE = "(";
    public static final String RIGHT_TYPE = ")";
    public static final String CONDITION_TYPE = "CONDITION";
    public static final String OR_RELATIONSHIP_TYPE = "OR";
    public static final String AND_RELATIONSHIP_TYPE = "AND";
    public static final String EQUAL_OPTION_CONDITION = "=";
    public static final String NOT_EQUAL_OPTION_CONDITION = "!=";
    public static final String GREATER_THAN_OPTION_CONDITION = ">";
    public static final String GREATER_THAN_OR_EQUAL_OPTION_CONDITION = ">=";
    public static final String LESS_THAN_OPTION_CONDITION = "<";
    public static final String LESS_THAN_OR_EQUAL_OPTION_CONDITION = "<=";
    public static final String IS_NULL_OPTION_CONDITION = "IS_NULL";
    public static final String IS_NOT_NULL_OPTION_CONDITION = "IS_NOT_NULL";
    public static final String LIKE_OPTION_CONDITION = "LIKE";
    public static final List<String> CONDITION_LIST = List.of(
            EQUAL_OPTION_CONDITION,
            NOT_EQUAL_OPTION_CONDITION,
            GREATER_THAN_OPTION_CONDITION,
            GREATER_THAN_OR_EQUAL_OPTION_CONDITION,
            LESS_THAN_OPTION_CONDITION,
            LESS_THAN_OR_EQUAL_OPTION_CONDITION,
            IS_NULL_OPTION_CONDITION,
            IS_NOT_NULL_OPTION_CONDITION,
            LIKE_OPTION_CONDITION
    );

    /**
     * 关联关系
     */
    private final String relationship;

    /**
     * 验证情况
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
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            final Searchable searchable = field.getAnnotation(Searchable.class);
            if (searchable != null) {
                mappers.add(new Mapper(
                        StringUtils.hasText(searchable.name()) ? searchable.name() : field.getName(),
                        StringUtils.hasText(searchable.column()) ? searchable.column() : field.getName())
                );
            }
        }
        return mappers;
    }

    /**
     * 提取选项参数
     *
     * @param options 请求参数
     * @return 选项参数
     */
    public static List<Mixin> extractOptions(SearchableAbstract<?> options) {
        if (options == null) {
            return new ArrayList<>();
        } else {
            int index = 0;
            List<Mixin> tmp = new ArrayList<>();
            final LinkedList<List<Mixin>> linked = new LinkedList<>();
            for (final Option option : options) {
                if (LEFT_TYPE.equalsIgnoreCase(option.getType())) {
                    index += 1;
                    linked.add(tmp);
                    tmp = new ArrayList<>();
                    linked.getLast().add(new Mixin(option, tmp));
                } else if (RIGHT_TYPE.equalsIgnoreCase(option.getType())) {
                    index -= 1;
                    tmp = linked.removeLast();
                } else if (CONDITION_TYPE.equalsIgnoreCase(option.getType())) {
                    tmp.add(new Mixin(option));
                } else {
                    return new ArrayList<>();
                }
            }
            return index == 0 ? tmp : new ArrayList<>();
        }
    }

    /**
     * 构造方法
     */
    public SearchableAbstract() {
        super();
        this.relationship = AND_RELATIONSHIP_TYPE;
    }

    /**
     * 构造方法
     *
     * @param relationship 关联关系
     */
    public SearchableAbstract(String relationship) {
        super();
        this.relationship = OR_RELATIONSHIP_TYPE.equalsIgnoreCase(
                relationship) ? OR_RELATIONSHIP_TYPE : AND_RELATIONSHIP_TYPE;
    }

    /**
     * 验证自身
     *
     * @param clazz 模型类型
     * @return 参数是否合法
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
    protected boolean validationOptionsToMappings(Class<?> clazz, SearchableAbstract<?> context) {
        if (clazz == null || context == null) {
            return false;
        } else {
            if (context.isEmpty()) {
                context.validation = true;
                return true;
            } else {
                if (validationOptionsToMappings(extractOptions(context), extractMappings(clazz))) {
                    context.validation = true;
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * 验证参数是否合法以及格式化请求参数
     *
     * @param mixins  请求参数
     * @param mappers 映射数据
     * @return 选项参数
     */
    protected boolean validationOptionsToMappings(List<Mixin> mixins, List<Mapper> mappers) {
        if (mixins == null || mappers == null) {
            return false;
        } else {
            for (final Mixin mixin : mixins) {
                final Option option = mixin.getData();
                final List<Mixin> list = mixin.getList();
                if (option != null && list == null) {
                    if (CONDITION_TYPE.equalsIgnoreCase(option.getType())) {
                        if (!(OR_RELATIONSHIP_TYPE.equalsIgnoreCase(option.getRelationship())
                                || AND_RELATIONSHIP_TYPE.equalsIgnoreCase(option.getRelationship()))) {
                            return false;
                        }
                        boolean bool = false;
                        for (final String condition : CONDITION_LIST) {
                            if (condition.equalsIgnoreCase(option.getCondition())) {
                                bool = true;
                                break;
                            }
                        }
                        if (bool) {
                            bool = false;
                        } else {
                            return false;
                        }
                        for (final Mapper mapper : mappers) {
                            if (mapper.getName().toLowerCase().equalsIgnoreCase(option.getKey())) {
                                bool = true;
                                option.setKey(mapper.getColumn());
                                break;
                            }
                        }
                        if (!bool) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                if (option != null
                        && list != null
                        && !list.isEmpty()
                        && !validationOptionsToMappings(list, mappers)) {
                    return false;
                }
            }
            return true;
        }
    }
}
