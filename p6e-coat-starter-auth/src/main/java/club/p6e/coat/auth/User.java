package club.p6e.coat.auth;


import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public interface User {

    /**
     * 获取序号
     *
     * @return 序号
     */
    public  String id();

    /**
     * 获取密码
     *
     * @return 密码
     */
    public  String password();

    public  User password(String password);

    /**
     * 序列化方法
     *
     * @return 序列化后的字符串内容
     */
    public  String serialize();

    /**
     * 转换为 MAP 对象
     *
     * @return MAP 对象
     */
    public  Map<String, Object> toMap();

}
