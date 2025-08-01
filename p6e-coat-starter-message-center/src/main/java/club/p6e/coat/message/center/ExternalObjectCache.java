package club.p6e.coat.message.center;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * External Object Cache
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class ExternalObjectCache implements Serializable {

    /**
     * Cache Object
     */
    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Model>> CACHE = new ConcurrentHashMap<>();

    /**
     * Get Cache Object
     *
     * @param type Cache Type
     * @param key  Cache Key
     * @param <T>  Cache Value Class Type
     * @return Cache Value Class Type
     */
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
                if (model.getOverdue() == 0) {
                    return (T) model.getData();
                } else {
                    if (System.currentTimeMillis() > model.getDate() + model.getOverdue()) {
                        data.remove(key);
                        return null;
                    } else {
                        return (T) model.getData();
                    }
                }
            }
        }
    }

    /**
     * Set Cache Object
     *
     * @param type  Cache Type
     * @param key   Cache Key
     * @param value Cache Value
     */
    public static void set(String type, String key, Model value) {
        ConcurrentHashMap<String, Model> data = CACHE.get(type);
        if (data == null) {
            data = create(type);
        }
        data.put(key, value);
    }

    /**
     * Clean Cache Object
     */
    public synchronized static void clean() {
        for (final ConcurrentHashMap<String, Model> map : CACHE.values()) {
            map.keySet().forEach(k -> {
                final Model model = map.get(k);
                if (model != null) {
                    model.deleteFunction.apply(model.getData());
                    map.remove(k);
                }
            });
        }
        CACHE.clear();
    }

    /**
     * Create Cache Object
     *
     * @param type Type
     * @return ConcurrentHashMap Object
     */
    private synchronized static ConcurrentHashMap<String, Model> create(String type) {
        return CACHE.computeIfAbsent(type, k -> new ConcurrentHashMap<>());
    }

    /**
     * External Object Cache Model
     */
    @Data
    @Accessors(chain = true)
    public static class Model implements Serializable {
        private volatile long date;
        private volatile long overdue;
        private volatile Object data;
        private volatile Function<Void, Object> createFunction;
        private volatile Function<Object, Void> deleteFunction;

        public Model(Function<Void, Object> createFunction) {
            this(3600000L, createFunction, o -> null);
        }

        public Model(long overdue, Function<Void, Object> createFunction) {
            this(overdue, createFunction, o -> null);
        }

        public Model(long overdue, Function<Void, Object> createFunction, Function<Object, Void> deleteFunction) {
            this.overdue = overdue;
            this.createFunction = createFunction;
            this.deleteFunction = deleteFunction;
        }

    }

}
