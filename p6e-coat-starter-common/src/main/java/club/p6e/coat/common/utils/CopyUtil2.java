package club.p6e.coat.common.utils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Copy 的帮助类
 * 作用是的帮助我们 VO <-> DTO <-> DB 互相转换
 *
 * @author lidashuang
 * @version 1.0
 */
@SuppressWarnings("all")
public final class CopyUtil2 {

    /**
     * 默认 map 参数
     */
    private static final Map<?, ?> DEFAULT_MAP = new HashMap<>();

    /**
     * 默认 list 参数
     */
    private static final List<?> DEFAULT_LIST = new ArrayList<>();

    /**
     * 对象转换
     *
     * @param data   待转换的数据
     * @param tClass 转换的类型 Class 对象
     * @param <T>    转换的类型
     * @return 执行复制结果
     */
    public static <T> T run(Object data, Class<T> tClass) {
        return run(data, tClass, null);
    }

    /**
     * 对象转换
     *
     * @param data        待转换的数据
     * @param tClass      转换的类型 Class 对象
     * @param defaultData 默认的转换数据对象
     * @param <T>         转换的类型
     * @return 执行复制结果
     */
    public static <T> T run(Object data, Class<T> tClass, T defaultData) {
        if (data == null || tClass == null) {
            return deepClone(defaultData);
        } else {
            try {
                // 创建对象
                final T t = tClass.getDeclaredConstructor().newInstance();
                // 执行复制操作
                return run(data, t, defaultData);
            } catch (Exception e) {
                e.printStackTrace();
                return deepClone(defaultData);
            }
        }
    }

    /**
     * 对象转换
     *
     * @param data    待转换的数据
     * @param tObject 转换的对象
     * @param <T>     转换的类型
     * @return 执行结果
     */
    @SuppressWarnings("all")
    public static <T> T run(Object data, T tObject) {
        return run(data, tObject, null);
    }

    /**
     * 对象转换
     *
     * @param data        待转换的数据
     * @param tObject     转换的对象
     * @param defaultData 默认的转换数据对象
     * @param <T>         转换的类型
     * @return 执行结果
     */
    public static <T> T run(Object data, T tObject, T defaultData) {
        if (data == null || tObject == null) {
            return deepClone(defaultData);
        } else {
            try {
                final Class<?> a = data.getClass();
                final Class<?> b = tObject.getClass();
                if (!isSerializable(a)) {
                    throw new RuntimeException(CopyUtil2.class + " run() => "
                            + a.getName() + " copy operation does not implement interface " + Serializable.class);
                }
                if (!isSerializable(b)) {
                    throw new RuntimeException(CopyUtil2.class + " run() => "
                            + b.getName() + " copy operation does not implement interface " + Serializable.class);
                }
                // 获取自类和父类里面的 field
                final Field[] aFields = getFields(a);
                // 获取自类和父类里面的 field
                final Field[] bFields = getFields(b);
                for (final Field af : aFields) {
                    for (final Field bf : bFields) {
                        // 根据名字匹配
                        if (af.getName().equals(bf.getName())) {
                            // 赋予权限访问
                            af.setAccessible(true);
                            bf.setAccessible(true);
                            // 读取源数据对象
                            final Object o = af.get(data);
                            if (o != null) {
                                if (af.getType() == bf.getType()) {
                                    if (isListType(af.getType()) && isListType(bf.getType())) {
                                        final Class<?>[] g1 = getGenericParadigmClass(af.getGenericType());
                                        final Class<?>[] g2 = getGenericParadigmClass(bf.getGenericType());
                                        if (g1.length == 1 && g2.length == 1) {
                                            bf.set(tObject, runList((List<?>) o, g2[0], null));
                                        }
                                    } else if (isMapType(af.getType()) && isMapType(bf.getType())) {
                                        final Class<?>[] g1 = getGenericParadigmClass(af.getGenericType());
                                        final Class<?>[] g2 = getGenericParadigmClass(bf.getGenericType());
                                        if (g1.length == 2 && g2.length == 2 && g1[0] == g2[0]
                                                && isSerializable(g1[1]) && isSerializable(g2[1])) {
                                            bf.set(tObject, runMap((Map<?, ?>) o, g2[1], null));
                                        }
                                    } else {
                                        bf.set(tObject, deepClone(o));
                                    }
                                } else if (isSerializable(af.getType())
                                        && isSerializable(bf.getType())
                                        && !isMapType(af.getType())
                                        && !isMapType(bf.getType())
                                        && !isListType(af.getType())
                                        && !isListType(bf.getType())) {
                                    bf.set(tObject, run(o, bf.getType(), null));
                                }
                            }
                        }
                    }
                }
                return tObject;
            } catch (Exception e) {
                e.printStackTrace();
                return deepClone(defaultData);
            }
        }
    }

    /**
     * list 数据复制
     *
     * @param list   列表数据
     * @param tClass 转换的类型 Class 对象
     * @param <E>    待转换的类型
     * @param <T>    转换的类型
     * @return 执行结果
     */
    @SuppressWarnings("all")
    public static <E, T> List<T> runList(List<E> list, Class<T> tClass) {
        return runList(list, tClass, (List<T>) DEFAULT_LIST);
    }

    /**
     * list 数据复制
     *
     * @param list        列表数据
     * @param tClass      转换的类型 Class 对象
     * @param defaultData 默认的转换数据对象
     * @param <E>         待转换的类型
     * @param <T>         转换的类型
     * @return 复制 map 对象
     */
    @SuppressWarnings("all")
    public static <E, T> List<T> runList(List<E> list, Class<T> tClass, List<T> defaultData) {
        if (list == null || tClass == null || !isListType(list.getClass())) {
            return deepClone(defaultData);
        } else {
            try {
                final List<T> result = new ArrayList<>();
                final Iterator<E> iterator = list.iterator();
                while (iterator.hasNext()) {
                    final E e = iterator.next();
                    final Class<?> eClass = e.getClass();
                    if (eClass == tClass) {
                        if (isListType(eClass) && isListType(tClass)) {
                            final Class<?>[] g1 = getGenericParadigmClass(eClass);
                            final Class<?>[] g2 = getGenericParadigmClass(tClass);
                            if (g1.length == 1 && g2.length == 1) {
                                result.add((T) runList((List<?>) e, g2[0], null));
                            }
                        } else if (isMapType(eClass) && isMapType(tClass)) {
                            final Class<?>[] g1 = getGenericParadigmClass(eClass);
                            final Class<?>[] g2 = getGenericParadigmClass(tClass);
                            if (g1.length == 2 && g2.length == 2 && g1[0] == g2[0]
                                    && isSerializable(g1[1]) && isSerializable(g2[1])) {
                                result.add((T) runMap((Map<?, ?>) e, g2[1], null));
                            }
                        } else {
                            result.add((T) deepClone(e));
                        }
                    } else if (isSerializable(eClass)
                            && isSerializable(tClass)
                            && !isMapType(eClass)
                            && !isMapType(tClass)
                            && !isListType(eClass)
                            && !isListType(tClass)) {
                        result.add(run(e, tClass, null));
                    }
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return deepClone(defaultData);
            }
        }
    }

    /**
     * map 数据复制
     *
     * @param data   数据
     * @param wClass 转换的类型 Class 对象
     * @param <K>    KEY 的类型
     * @param <V>    VALUE 的类型
     * @param <W>    VALUE 的转换类型
     * @return 复制的 map 对象
     */
    @SuppressWarnings("all")
    public static <K, V, W> Map<K, W> runMap(Map<K, V> data, Class<W> wClass) {
        return runMap(data, wClass, (Map<K, W>) DEFAULT_MAP);
    }

    /**
     * map 数据复制
     *
     * @param data        数据
     * @param wClass      转换的类型 Class 对象
     * @param defaultData 默认的转换数据对象
     * @param <K>         KEY 的类型
     * @param <V>         VALUE 的类型
     * @param <W>         VALUE 的转换类型
     * @return 复制的 map 对象
     */
    @SuppressWarnings("all")
    public static <K, V, W> Map<K, W> runMap(Map<K, V> data, Class<W> wClass, Map<K, W> defaultData) {
        if (data == null || wClass == null || !isMapType(data.getClass())) {
            return deepClone(defaultData);
        } else {
            try {
                final Map<K, W> result = new HashMap<>();
                for (K key : data.keySet()) {
                    final V value = data.get(key);
                    final Class<?> vClass = value.getClass();
                    if (vClass == wClass) {
                        if (isListType(vClass) && isListType(wClass)) {
                            final Class<?>[] g1 = getGenericParadigmClass(vClass);
                            final Class<?>[] g2 = getGenericParadigmClass(wClass);
                            if (g1.length == 1 && g2.length == 1) {
                                result.put(key, (W) runList((List<?>) value, g2[0], null));
                            }
                        } else if (isMapType(vClass) && isMapType(wClass)) {
                            final Class<?>[] g1 = getGenericParadigmClass(vClass);
                            final Class<?>[] g2 = getGenericParadigmClass(wClass);
                            if (g1.length == 2 && g2.length == 2 && g1[0] == g2[0]
                                    && isSerializable(g1[1]) && isSerializable(g2[1])) {
                                result.put(key, (W) runMap((Map<?, ?>) value, g2[1], null));
                            }
                        } else {
                            result.put(key, (W) deepClone(value));
                        }
                    } else if (isSerializable(vClass)
                            && isSerializable(wClass)
                            && !isMapType(vClass) && !isMapType(wClass)
                            && !isListType(vClass) && !isListType(wClass)) {
                        result.put(key, run(value, wClass, null));
                    }
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return deepClone(defaultData);
            }
        }
    }

    /**
     * 将对象转换为 map 对象
     *
     * @param data 源数据对象
     * @return 转换的对象
     */
    @SuppressWarnings("all")
    public static Map<String, Object> toMap(Object data) {
        return toMap(data, null);
    }

    /**
     * 将对象转换为 map 对象
     *
     * @param data        源数据对象
     * @param defaultData 默认的转换数据对象
     * @return 转换的对象
     */
    public static Map<String, Object> toMap(Object data, Map<String, Object> defaultData) {
        if (data == null) {
            return deepClone(defaultData);
        } else {
            try {
                // 读取的参数的类型
                final Class<?> oClass = data.getClass();
                // 判断是否接口于 java.io.Serializable
                if (isSerializable(oClass)) {
                    // 创建返回对象
                    final Map<String, Object> rMap;
                    if (isMapType(oClass)) {
                        final Map<?, ?> dataMap = (Map<?, ?>) data;
                        final Class<?>[] dataGenericParadigmClass = getGenericParadigmClass(oClass.getTypeName());
                        // 只处理 key 为基础变量且不为 object 的 map 数据
                        if (dataGenericParadigmClass.length == 2
                                && isBaseType(dataGenericParadigmClass[0])
                                && dataGenericParadigmClass[0] != Object.class) {
                            rMap = new HashMap<>(dataMap.size());
                            for (final Object key : dataMap.keySet()) {
                                // 读取需要复制的值
                                final Object value = dataMap.get(key);
                                // 判断是否为基础类型
                                if (isBaseType(value.getClass())) {
                                    rMap.put(String.valueOf(key), deepClone(value));
                                } else {
                                    // 判断是否为 list 类型
                                    if (isListType(value.getClass())) {
                                        rMap.put(String.valueOf(key), toList(value, null));
                                    } else {
                                        rMap.put(String.valueOf(key), toMap(value, null));
                                    }
                                }
                            }
                        } else {
                            // 错误情况下赋值
                            // 泛型规则不合法的情况
                            rMap = deepClone(defaultData);
                        }
                    } else {
                        final Field[] fields = getFields(oClass);
                        rMap = new HashMap<>(fields.length);
                        for (final Field field : fields) {
                            field.setAccessible(true);
                            final Object fieldValue = field.get(data);
                            final Class<?> fieldClass = field.getType();
                            if (isBaseType(fieldClass)) {
                                // 判断是否为基础类型
                                rMap.put(field.getName(), deepClone(fieldValue));
                            } else if (isListType(fieldClass)) {
                                // 判断是否为 list 类型
                                rMap.put(field.getName(), toList(fieldValue, null));
                            } else {
                                // 不是上面的类型就走 map 类型
                                rMap.put(field.getName(), toMap(fieldValue, null));
                            }
                        }
                    }
                    return rMap;
                } else {
                    // 如果参数没有接口 java.io.Serializable 就抛出运行异常
                    throw new RuntimeException(CopyUtil2.class + " toMap() => "
                            + oClass.getName() + " copy operation does not implement interface " + Serializable.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return deepClone(defaultData);
            }
        }
    }

    /**
     * 将 list<object> 转换为 list<map<string, object> 对象
     *
     * @param data 源数据对象
     * @return 转换的对象
     */
    @SuppressWarnings("all")
    public static List<Object> toList(Object data) {
        return toList(data, null);
    }

    /**
     * 将 list<object> 转换为 list<map<string, object> 对象
     *
     * @param data        源数据对象
     * @param defaultData 默认的转换数据对象
     * @return 转换的对象
     */
    @SuppressWarnings("all")
    public static List<Object> toList(Object data, List<Object> defaultData) {
        if (data == null || !isListType(data.getClass())) {
            return deepClone(defaultData);
        } else {
            try {
                // 创建结果返回对象
                final List<Object> result = new ArrayList<>();
                final List<Object> dataList = (List<Object>) data;
                final Iterator<Object> iterator = dataList.iterator();
                while (iterator.hasNext()) {
                    final Object item = iterator.next();
                    final Class<?> itemClass = item.getClass();
                    if (isBaseType(itemClass)) {
                        result.add(deepClone(item));
                    } else if (isListType(itemClass)) {
                        result.add(toList(item, null));
                    } else {
                        result.add(toMap(item, null));
                    }
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return deepClone(defaultData);
            }
        }

    }


    /**
     * 判断是否为基础类型
     *
     * @param c 类型 Class 对象
     * @return 是否为基础类型
     */
    @SuppressWarnings("all")
    private static boolean isBaseType(Class<?> c) {
        final String cName = c.getTypeName();
        switch (cName) {
            case "java.lang.Object":
            case "java.lang.String":
            case "java.lang.Character":
            case "char":
            case "java.lang.Short":
            case "short":
            case "java.lang.Integer":
            case "int":
            case "java.lang.Double":
            case "double":
            case "java.lang.Long":
            case "long":
            case "java.lang.Float":
            case "float":
            case "java.lang.Boolean":
            case "boolean":
            case "java.lang.Byte":
            case "byte":
                return true;
            default:
                return false;
        }
    }

    /**
     * 判断是否为 list 类型
     *
     * @param c 类型 Class 对象
     * @return 是否为 list 类型
     */
    private static boolean isListType(Class<?> c) {
        return getAllInterfaces(c).contains(Iterable.class);
    }

    /**
     * 判断是否为 map 类型
     *
     * @param c 类型 Class 对象
     * @return 是否为 map 类型
     */
    private static boolean isMapType(Class<?> c) {
        return getAllInterfaces(c).contains(Map.class);
    }

    /**
     * 获取类型的全部接口类型
     *
     * @param c 类型 Class 对象
     * @return 接口类型数组
     */
    private static List<Class<?>> getAllInterfaces(Class<?> c) {
        final Set<Class<?>> result = new HashSet<>();
        for (final Class<?> clazz : c.getInterfaces()) {
            result.add(clazz);
            result.addAll(getAllInterfaces(clazz));
        }
        final Class<?> superClass = c.getSuperclass();
        if (superClass != null) {
            for (final Class<?> clazz : superClass.getInterfaces()) {
                result.add(clazz);
                result.addAll(getAllInterfaces(clazz));
            }
        }
        return new ArrayList<>(result);
    }

    /**
     * 判断是否接口 java.io.Serializable
     *
     * @param c 类型 Class 对象
     * @return 是否接口 java.io.Serializable
     */
    private static boolean isSerializable(Class<?> c) {
        final Class<?>[] cls = c.getInterfaces();
        if (cls.length > 0) {
            for (Class<?> clazz : cls) {
                if (clazz == Serializable.class) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 深度复制
     *
     * @return 深度复制的对象
     */
    @SuppressWarnings("all")
    public static <T> T deepClone(final T t) {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = null;
        try {
            if (t == null) {
                return null;
            } else {
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(t);
                bis = new ByteArrayInputStream(bos.toByteArray());
                ois = new ObjectInputStream(bis);
                return (T) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            close(ois);
            close(bis);
            close(oos);
            close(bos);
        }
    }

    /**
     * 关闭字节流方法
     *
     * @param closeable 需要关闭的对象
     */
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取 field 数据
     *
     * @param c 类型 Class 对象
     * @return field 数据
     */
    private static Field[] getFields(Class<?> c) {
        Class<?> cls = c;
        final List<Field> fields = new ArrayList<>();
        do {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            // 获取父类对象
            cls = cls.getSuperclass();
        } while (cls != null && cls != Object.class && isSerializable(cls));
        return fields.stream().filter(f ->
                !(Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers()))
        ).toArray(Field[]::new);
    }

    /**
     * 读取泛型的类型
     *
     * @param type 类型
     * @return 泛型的类型
     */
    private static Class<?>[] getGenericParadigmClass(Type type) {
        return getGenericParadigmClass(type.getTypeName());
    }

    /**
     * 读取泛型的类型
     *
     * @param typeName 类型名称
     * @return 泛型的类型
     */
    private static Class<?>[] getGenericParadigmClass(String typeName) {
        try {
            final int a = typeName.indexOf("<");
            final int b = typeName.lastIndexOf(">");
            if (a != -1 && b != -1 && a < b) {
                final String[] genericNames = typeName.substring(a + 1, b).split(",");
                final Class<?>[] genericClass = new Class<?>[genericNames.length];
                for (int i = 0; i < genericNames.length; i++) {
                    genericClass[i] = Class.forName(genericNames[i].trim());
                }
                return genericClass;
            } else {
                return new Class<?>[0];
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Class<?>[0];
        }
    }
}


