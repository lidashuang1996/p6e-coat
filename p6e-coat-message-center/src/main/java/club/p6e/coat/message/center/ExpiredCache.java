package club.p6e.coat.message.center;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class ExpiredCache implements Serializable {

    /**
     * 是否启用缓存
     */
    private static boolean ENABLE = true;

    /**
     * 缓存数据对象
     */
    @Data
    @Accessors(chain = true)
    private static class Model implements Serializable {
        /**
         * 数据对象
         */
        private volatile Object data;

        /**
         * 缓存的时间
         */
        private volatile Long duration;

        /**
         * 保存的时间戳
         */
        private volatile Long date;

        /**
         * 构造方法初始化
         *
         * @param data     保存的时间戳
         * @param duration 缓存的时间
         */
        public Model(Object data, Long duration) {
            this.data = data;
            this.duration = duration;
            this.date = System.currentTimeMillis();
        }
    }

    /**
     * 缓存的对象
     */
    private static final Map<String, ConcurrentHashMap<String, Model>> CACHE = new ConcurrentHashMap<>();

    /**
     * 设置是否启用
     *
     * @param enable 启用的状态
     */
    public static void setEnable(boolean enable) {
        ExpiredCache.ENABLE = enable;
    }

    /**
     * 读取缓存对象
     *
     * @param type 类型
     * @param key  键
     * @param <T>  对象类型
     * @return 缓存对象
     */
    @SuppressWarnings("ALL")
    public static <T> T get(String type, String key) {
        final ConcurrentHashMap<String, Model> data = CACHE.get(type);
        if (data == null) {
            return null;
        } else {
            final Model model = data.get(key);
            if (model == null) {
                data.remove(key);
                return null;
            } else {
                if (model.getDuration() <= 0L) {
                    return (T) model.getData();
                } else {
                    if (ENABLE) {
                        if (System.currentTimeMillis() > model.getDate() + model.getDuration()) {
                            data.remove(key);
                            return null;
                        } else {
                            return (T) model.getData();
                        }
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    /**
     * 写入缓存对象
     *
     * @param type  类型
     * @param key   键
     * @param value 缓存对象
     */
    public static void set(String type, String key, Object value) {
        set(type, key, value, 0L);
    }

    /**
     * 写入缓存对象
     *
     * @param type     类型
     * @param key      键
     * @param value    缓存对象
     * @param duration 缓存时间
     */
    public static void set(String type, String key, Object value, Long duration) {
        ConcurrentHashMap<String, Model> data = CACHE.get(type);
        if (data == null) {
            data = CACHE.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
        }
        if (ENABLE || duration <= 0L) {
            data.put(key, new Model(value, duration));
        }
    }

    /**
     * 清除缓存
     */
    @SuppressWarnings("ALL")
    public static void clean() {
        for (final ConcurrentHashMap<String, Model> value : CACHE.values()) {
            value.clear();
        }
        CACHE.clear();
    }

}
