package club.p6e.coat.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class TemplateParser {

    /**
     * 定义类
     */
    public interface Definition {

        /**
         * 执行模板解析
         *
         * @param content                      模板内容
         * @param data                         模板数据对象
         * @param isVariablesReplaceEmptyValue 是否用变量名替换空值的内容
         * @return 模板解析后的内容
         */
        public String execute(String content, Map<String, String> data, boolean isVariablesReplaceEmptyValue);

    }

    /**
     * 实现类
     */
    public static class Implementation implements Definition {

        /**
         * 标记开始的符号
         */
        private final String start;

        /**
         * 内容结束的符号
         */
        private final String contentEnd;

        /**
         * 内容开始的符号
         */
        private final String contentStart;

        /**
         * 内容默认值的符号
         */
        private final String contentDefaultValue;

        /**
         * 构造方法初始化
         */
        public Implementation() {
            this("@", "{", "}", ":");
        }

        /**
         * 构造方法初始化
         *
         * @param start               开始
         * @param contentStart        内容开始
         * @param contentEnd          内容结束
         * @param contentDefaultValue 内容默认值分割符
         */
        public Implementation(String start, String contentStart, String contentEnd, String contentDefaultValue) {
            this.start = start;
            this.contentEnd = contentEnd;
            this.contentStart = contentStart;
            this.contentDefaultValue = contentDefaultValue;
        }

        /**
         * 获取内容里面的 KEY 的内容
         *
         * @param content 内容
         * @return KEY
         */
        private String getKey(String content) {
            if (content != null && !content.isEmpty()) {
                final StringBuilder result = new StringBuilder();
                for (int i = 0; i < content.length(); i++) {
                    final String ch = String.valueOf(content.charAt(i));
                    if (this.contentDefaultValue.equals(ch)) {
                        break;
                    } else {
                        result.append(content.charAt(i));
                    }
                }
                return result.toString();
            }
            return "";
        }

        /**
         * 获取内容里面的默认值的内容
         *
         * @param content 内容
         * @return 默认值数据
         */
        private String getDefaultValue(String content) {
            if (content != null && !content.isEmpty()) {
                final StringBuilder result = new StringBuilder();
                for (int i = content.length() - 1; i >= 0; i--) {
                    final String ch = String.valueOf(content.charAt(i));
                    if (this.contentDefaultValue.equals(ch)) {
                        return result.length() > 0 ? result.toString() : null;
                    } else {
                        result.insert(0, content.charAt(i));
                    }
                }
            }
            return null;
        }

        @Override
        public String execute(String content, Map<String, String> data, boolean isVariablesReplaceEmptyValue) {
            if (content == null || content.isEmpty()) {
                return "";
            } else {
                StringBuilder key = null;
                final int length = content.length();
                final StringBuilder result = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    final String c = String.valueOf(content.charAt(i));
                    if (i + 1 < length
                            && this.start.equals(c)
                            && this.contentStart.equals(String.valueOf(content.charAt(i + 1)))) {
                        if (key != null) {
                            result.append(this.start).append(this.contentStart).append(key);
                        }
                        i++;
                        key = new StringBuilder();
                    } else if (key != null) {
                        if (this.contentEnd.equals(c)) {
                            final String ks = key.toString();
                            key = null;
                            if (data != null) {
                                final String value = data.get(getKey(ks));
                                if (value != null) {
                                    result.append(value);
                                    continue;
                                }
                            }
                            final String defaultValue = getDefaultValue(ks);
                            if (defaultValue != null) {
                                result.append(defaultValue);
                                continue;
                            }
                            if (isVariablesReplaceEmptyValue) {
                                result.append(ks);
                            } else {
                                result
                                        .append(this.start)
                                        .append(this.contentStart)
                                        .append(ks)
                                        .append(this.contentEnd);
                            }
                        } else {
                            key.append(c);
                        }
                    } else {
                        result.append(c);
                    }
                }
                if (key != null) {
                    result
                            .append(this.start)
                            .append(this.contentStart)
                            .append(key);
                }
                return result.toString();
            }
        }
    }

    /**
     * 默认的模板解析实现对象
     */
    private static Definition DEFINITION = new Implementation();

    /**
     * 设置模板解析实现对象
     *
     * @param implementation 模板解析实现对象
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * 执行解析模板的内容
     *
     * @param content 模板的内容
     * @param data    模板数据对象
     * @return 模板解析后的内容
     */
    public static String execute(String content, Map<String, String> data) {
        return execute(content, data, false);
    }

    /**
     * @param content 模板内容
     * @param data    模板数据对象数组
     * @return 模板解析后的内容
     */
    public static String execute(String content, String... data) {
        if (data.length % 2 != 0) {
            throw new RuntimeException("[ execute(" +
                    "String content, String... data) ] ==> data length not a multiple of 2 in length.");
        }
        final Map<String, String> map = new HashMap<>(data.length / 2);
        for (int i = 0; i < data.length; ) {
            map.put(data[i], data[i + 1]);
            i = i + 2;
        }
        return DEFINITION.execute(content, map, false);
    }

    /**
     * 执行模板解析
     *
     * @param content                      模板内容
     * @param data                         模板数据对象
     * @param isVariablesReplaceEmptyValue 是否用变量名替换空值的内容
     * @return 模板解析后的内容
     */
    public static String execute(String content, Map<String, String> data, boolean isVariablesReplaceEmptyValue) {
        return DEFINITION.execute(content, data, isVariablesReplaceEmptyValue);
    }
}
