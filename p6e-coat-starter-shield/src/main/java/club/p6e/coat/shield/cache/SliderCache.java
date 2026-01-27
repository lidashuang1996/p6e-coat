package club.p6e.coat.shield.cache;

import java.util.List;

public interface SliderCache {
    /**
     * 缓存滑块验证码
     * @param key 缓存键
     * @param value 缓存值
     */
    void set(String client, String key, String value2, String shape, String value);
    /**
     * 获取滑块验证码
     * @param key 缓存键
     * @return 缓存值
     */
    String get(String key);
     /**
      * 删除滑块验证码
      * @param key 缓存键
      */
    void remove(String key);

    int warehouse();

    int warehouse(int index);

    String warehouse(int index, int chunk);

}
