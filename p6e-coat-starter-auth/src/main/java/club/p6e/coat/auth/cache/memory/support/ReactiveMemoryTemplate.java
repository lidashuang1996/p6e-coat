package club.p6e.coat.auth.cache.memory.support;

import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Reactive Memory Template
 *
 * @author lidashuang
 * @version 1.0
 */
public class ReactiveMemoryTemplate {

    /**
     * 注入日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMemoryTemplate.class);

    /**
     * 存储缓存数据的对象
     */
    protected final Map<String, Model> CACHE = new ConcurrentHashMap<>();

    /**
     * 构造方法初始化
     * 启动定时任务的线程
     * 每隔一段时间清除掉过期的数据
     */
    public ReactiveMemoryTemplate() {
        this(5, 900, TimeUnit.SECONDS);
    }

    /**
     * 构造方法初始化
     * 启动定时任务的线程
     * 每隔一段时间清除掉过期的数据
     *
     * @param initialDelay 初始化延迟时间
     * @param period       轮询间隔时间
     * @param unit         时间单位
     */
    public ReactiveMemoryTemplate(long initialDelay, long period, TimeUnit unit) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            final long now = System.currentTimeMillis();
            for (final String key : CACHE.keySet()) {
                if (key == null) {
                    CACHE.remove(null);
                } else {
                    final Model value = CACHE.get(key);
                    if (value == null
                            || (value.getExpire() > 0 && now > value.getDate() + (value.getExpire() * 1000))) {
                        CACHE.remove(key);
                    }
                }
            }
        }, initialDelay, period, unit);
    }

    /**
     * 读取缓存全部名称
     *
     * @return 缓存全部名称
     */
    public List<String> names() {
        return new ArrayList<>(CACHE.keySet());
    }

    /**
     * 写入数据
     *
     * @param key   键
     * @param value 值
     * @return 写入数据是否成功的结果
     */
    public boolean set(String key, Object value) {
        CACHE.put(key, new Model(serialize(value), -1));
        return true;
    }

    /**
     * 写入数据并设置过期时间
     *
     * @param key    键
     * @param value  值
     * @param expire 过期时间
     * @return 写入数据是否成功的结果
     */
    public boolean set(String key, Object value, long expire) {
        CACHE.put(key, new Model(serialize(value), expire));
        return true;
    }

    /**
     * 读取数据
     *
     * @param key   键
     * @param clazz 结果的类
     * @param <T>   强制转换为结果的类型
     * @return 结果类型的对象
     */
    @SuppressWarnings("ALL")
    public <T> T get(String key, Class<T> clazz) {
        final Model model = CACHE.get(key);
        if (model == null) {
            return null;
        } else {
            if (model.getExpire() > 0
                    && System.currentTimeMillis() - model.getDate() > (model.getExpire() * 1000)) {
                CACHE.remove(key);
                return null;
            } else {
                return (T) deserialize(model.getBytes());
            }
        }
    }

    /**
     * 删除数据
     *
     * @param key 键
     * @return 删除的数据的条数
     */
    public long del(String key) {
        final Model model = CACHE.remove(key);
        if (model != null
                && model.getExpire() > 0
                && System.currentTimeMillis() - model.getDate() <= model.getExpire() * 1000) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 序列化
     *
     * @param o 待序列化的对象
     * @return 序列化完成后的字节数据
     */
    private static byte[] serialize(Object o) {
        ObjectOutputStream objectOutputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            objectOutputStream.flush();
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error(ReactiveMemoryTemplate.class
                    + " [ SERIALIZE ERROR ] fun serialize(Object o).", e);
        } finally {
            close(objectOutputStream);
            close(byteArrayOutputStream);
        }
        return new byte[0];
    }

    /**
     * 反序列化
     *
     * @param bytes 待反序列化的字节数据
     * @return 反序列化完成后的对象
     */
    private static Object deserialize(byte[] bytes) {
        ObjectInputStream objectInputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(ReactiveMemoryTemplate.class
                    + " [ DESERIALIZE ERROR ] fun deserialize(byte[] bytes).", e);
        } finally {
            close(objectInputStream);
            close(byteArrayInputStream);
        }
        return new byte[0];
    }

    /**
     * 资源回收
     *
     * @param closeable 需要关闭的对象
     */
    private static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            LOGGER.error(ReactiveMemoryTemplate.class
                    + " [ CLOSEABLE ERROR ] fun close(Closeable closeable).", e);
        }
    }


    /**
     * 缓存数据的模型对象
     */
    @Data
    @Accessors(chain = true)
    public static class Model implements Serializable {

        /**
         * 创建时间
         */
        private long date;

        /**
         * 过期时间
         */
        private long expire;

        /**
         * 内容序列化字节数组
         */
        private byte[] bytes;

        /**
         * 构造方法初始化
         *
         * @param bytes 字节数组
         */
        public Model(byte[] bytes) {
            this.bytes = bytes;
            this.expire = -1;
            this.date = System.currentTimeMillis();
        }

        /**
         * 构造方法初始化
         *
         * @param bytes  字节数组
         * @param expire 过期时间
         */
        public Model(byte[] bytes, long expire) {
            this.bytes = bytes;
            this.expire = expire;
            this.date = System.currentTimeMillis();
        }
    }

}
