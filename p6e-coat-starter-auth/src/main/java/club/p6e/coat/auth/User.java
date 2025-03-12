package club.p6e.coat.auth;


import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface User {

    /**
     * 创建认证用户模型
     *
     * @param content 序列化的字符串内容
     * @return 认证用户模型
     */
    static User create(String content) {
        return new User() {
            @Override
            public String id() {
                return "";
            }

            @Override
            public String password() {
                return "";
            }

            @Override
            public String serialize() {
                return "";
            }

            @Override
            public Map<String, Object> toMap() {
                return Map.of();
            }
        };
    }

    static User create(Map<String, Object> data) {
        return new User() {
            @Override
            public String id() {
                return "";
            }

            @Override
            public String password() {
                return "";
            }

            @Override
            public String serialize() {
                return "";
            }

            @Override
            public Map<String, Object> toMap() {
                return Map.of();
            }
        };
    }

    /**
     * 获取序号
     *
     * @return 序号
     */
    String id();

    /**
     * 获取密码
     *
     * @return 密码
     */
    String password();

    /**
     * 序列化方法
     *
     * @return 序列化后的字符串内容
     */
    String serialize();

    /**
     * 转换为 MAP 对象
     *
     * @return MAP 对象
     */
    Map<String, Object> toMap();

}
