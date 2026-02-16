package club.p6e.coat.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Json Util
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("ALL")
public final class JsonUtil {

    /**
     * Definition
     */
    public interface Definition {

        /**
         * To JSON
         *
         * @param o Object
         * @return Serialization String
         */
        String toJson(Object o);

        /**
         * From JSON To Object
         *
         * @param json   JSON Content
         * @param tClass Object Class Object
         * @param <T>    Object Object
         * @return Serialization String
         */
        <T> T fromJson(String json, Class<T> tClass);

        /**
         * From JSON To T
         *
         * @param inputStream Input Stream Object
         * @param tClass      Object Class Object
         * @param <T>         Object Object
         * @return Deserialization T
         */
        <T> T fromJson(InputStream inputStream, Class<T> tClass);

        /**
         * From JSON To List
         *
         * @param json   JSON Content
         * @param iClass List Item Class Object
         * @param <I>    List Item Object
         * @return Deserialization T
         */
        <I> List<I> fromJsonToList(String json, Class<I> iClass);

        /**
         * From JSON To Map
         *
         * @param json   JSON Content
         * @param kClass Map Key Class Object
         * @param vClass Map Value Class Object
         * @param <K>    Map Key Object
         * @param <V>    Map Value Object
         * @return Deserialization T
         */
        <K, V> Map<K, V> fromJsonToMap(String json, Class<K> kClass, Class<V> vClass);

    }

    /**
     * Implementation
     */
    private static class Implementation implements Definition {

        /**
         * OBJECT_MAPPER 对象
         */
        private final ObjectMapper om;

        /**
         * 构造方法初始化
         */
        public Implementation() {
            om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            om.registerModule(new JavaTimeModule());
            final SimpleModule sm = new SimpleModule();
            sm.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            sm.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            om.registerModule(sm);
        }

        @Override
        public String toJson(Object o) {
            try {
                return om.writeValueAsString(o);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public <T> T fromJson(String json, Class<T> tClass) {
            try {
                return json == null ? null : om.readValue(json, tClass);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public <T> T fromJson(InputStream inputStream, Class<T> tClass) {
            try {
                return om.readValue(inputStream, tClass);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public <I> List<I> fromJsonToList(String json, Class<I> iClass) {
            try {
                return om.readValue(json, om.getTypeFactory().constructParametricType(List.class, iClass));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public <K, V> Map<K, V> fromJsonToMap(String json, Class<K> kClass, Class<V> vClass) {
            try {
                return om.readValue(json, om.getTypeFactory().constructParametricType(Map.class, kClass, vClass));
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Default Definition Implementation Object
     */
    private static Definition DEFINITION = new Implementation();

    /**
     * Set Definition Implementation Object
     *
     * @param implementation Definition Implementation Object
     */
    public static void set(Definition implementation) {
        DEFINITION = implementation;
    }

    /**
     * To JSON
     *
     * @param o Object
     * @return Serialization String
     */
    public String toJson(Object o) {
        return DEFINITION.toJson(o);
    }

    /**
     * From JSON To Object
     *
     * @param json   JSON Content
     * @param tClass Object Class Object
     * @param <T>    Object Object
     * @return Serialization String
     */
    public <T> T fromJson(String json, Class<T> tClass) {
        return DEFINITION.fromJson(json, tClass);
    }

    /**
     * From JSON To T
     *
     * @param inputStream Input Stream Object
     * @param tClass      Object Class Object
     * @param <T>         Object Object
     * @return Deserialization T
     */
    public <T> T fromJson(InputStream inputStream, Class<T> tClass) {
        return DEFINITION.fromJson(inputStream, tClass);
    }

    /**
     * From JSON To List
     *
     * @param json   JSON Content
     * @param iClass List Item Class Object
     * @param <I>    List Item Object
     * @return Deserialization T
     */
    public <I> List<I> fromJsonToList(String json, Class<I> iClass) {
        return DEFINITION.fromJsonToList(json, iClass);
    }

    /**
     * From JSON To Map
     *
     * @param json   JSON Content
     * @param kClass Map Key Class Object
     * @param vClass Map Value Class Object
     * @param <K>    Map Key Object
     * @param <V>    Map Value Object
     * @return Deserialization T
     */
    public <K, V> Map<K, V> fromJsonToMap(String json, Class<K> kClass, Class<V> vClass) {
        return DEFINITION.fromJsonToMap(json, kClass, vClass);
    }

}
