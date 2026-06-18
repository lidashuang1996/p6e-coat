package club.p6e.coat.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidashuang
 * @version 1.0
 */
public final class EntityUtil {

    public static String setJsonMapData(String data, String key, Object value) {
        Map<String, Object> map = JsonUtil.fromJsonToMap(data == null ? "{}" : data, String.class, Object.class);
        map = map == null ? new HashMap<>() : map;
        map.put(key, value);
        return JsonUtil.toJson(data);
    }

    public static <K, V> Map<K, V> getJsonMapData(String data, Class<K> kClass, Class<V> vClass) {
        final Map<K, V> map = JsonUtil.fromJsonToMap(data == null ? "{}" : data, kClass, vClass);
        return map == null ? new HashMap<>() : map;
    }

    public static String setJsonListData(String data, List<?> value) {
        List<Object> list = JsonUtil.fromJsonToList(data == null ? "[]" : data, Object.class);
        list = list == null ? new ArrayList<>() : list;
        list.addAll(value);
        return JsonUtil.toJson(list);
    }

    public static <I> List<I> getJsonListData(String data, Class<I> iClass) {
        final List<I> list = JsonUtil.fromJsonToList(data == null ? "[]" : data, iClass);
        return list == null ? new ArrayList<>() : list;
    }

}
