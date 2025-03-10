package club.p6e.coat.common.utils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * CopyUtil
 * VO <-> DTO <-> DB
 *
 * @author lidashuang
 * @version 2.0
 */
public final class CopyUtil {

    /**
     * 默认 LIST 参数
     */
    private static final List<?> DEFAULT_LIST = new ArrayList<>();

    /**
     * 对象转换
     *
     * @param sourceObject 源数据对象
     * @param targetClass  转换目标类型
     * @param <T>          转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <T> T run(Object sourceObject, Class<T> targetClass) {
        return run(sourceObject, targetClass, null, false);
    }

    /**
     * 对象转换
     *
     * @param sourceObject 源数据对象
     * @param targetClass  转换目标类型
     * @param isDeepClone  是否深度复制
     * @param <T>          转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <T> T run(Object sourceObject, Class<T> targetClass, boolean isDeepClone) {
        return run(sourceObject, targetClass, null, isDeepClone);
    }

    /**
     * 对象转换
     *
     * @param sourceObject        源数据对象
     * @param targetClass         转换目标类型
     * @param defaultTargetObject 默认目标数据对象
     * @param <T>                 转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <T> T run(Object sourceObject, Class<T> targetClass, T defaultTargetObject) {
        return run(sourceObject, targetClass, defaultTargetObject, false);
    }

    /**
     * 对象转换
     *
     * @param sourceObject        源数据对象
     * @param targetClass         转换目标类型
     * @param defaultTargetObject 默认目标数据对象
     * @param isDeepClone         是否深度复制
     * @param <T>                 转换的目标泛型
     * @return 目标数据对象
     */
    public static <T> T run(Object sourceObject, Class<T> targetClass, T defaultTargetObject, boolean isDeepClone) {
        try {
            if (sourceObject == null
                    || targetClass == null
                    || isMapType(targetClass)
                    || isIterableType(targetClass)
                    || isMapType(sourceObject.getClass())
                    || isIterableType(sourceObject.getClass())) {
                return defaultTargetObject;
            } else {
                final T targetObject = targetClass.getDeclaredConstructor().newInstance();
                return run(sourceObject, targetObject, defaultTargetObject, isDeepClone);
            }
        } catch (Exception e) {
            return defaultTargetObject;
        }
    }

    /**
     * 对象转换
     *
     * @param sourceObject 源数据对象
     * @param targetObject 目标数据对象
     * @param <T>          转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <T> T run(Object sourceObject, T targetObject) {
        return run(sourceObject, targetObject, null, false);
    }

    /**
     * 对象转换
     *
     * @param sourceObject 源数据对象
     * @param targetObject 目标数据对象
     * @param isDeepClone  是否深度复制
     * @param <T>          转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <T> T run(Object sourceObject, T targetObject, boolean isDeepClone) {
        return run(sourceObject, targetObject, null, isDeepClone);
    }

    /**
     * 对象转换
     *
     * @param sourceObject        源数据对象
     * @param targetObject        目标数据对象
     * @param defaultTargetObject 默认目标数据对象
     * @param <T>                 转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <T> T run(Object sourceObject, T targetObject, T defaultTargetObject) {
        return run(sourceObject, targetObject, defaultTargetObject, false);
    }

    /**
     * 对象转换
     *
     * @param sourceObject        源数据对象
     * @param targetObject        目标数据对象
     * @param defaultTargetObject 默认目标数据对象
     * @param isDeepClone         是否深度复制
     * @param <T>                 转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <T> T run(Object sourceObject, T targetObject, T defaultTargetObject, boolean isDeepClone) {
        if (sourceObject == null
                || isMapType(sourceObject.getClass())
                || isIterableType(sourceObject.getClass())
                || isMapType(targetObject.getClass())
                || isIterableType(targetObject.getClass())
                || !isSupportSerializable(sourceObject.getClass())
                || !isSupportSerializable(targetObject.getClass())) {
            return defaultTargetObject;
        }
        try {
            final Class<?> sourceClass = sourceObject.getClass();
            final Class<?> targetClass = targetObject.getClass();


            final Field[] sourceFields = getFields(sourceClass);
            final Field[] targetFields = getFields(targetClass);
            for (final Field sourceField : sourceFields) {
                for (final Field targetField : targetFields) {
                    if (sourceField.getName().equals(targetField.getName())) {
                        sourceField.setAccessible(true);
                        targetField.setAccessible(true);
                        final Object sourceFieldData = sourceField.get(sourceObject);
                        if (sourceFieldData != null) {
                            if (sourceField.getType() == targetField.getType()) {
                                if (isIterableType(sourceField.getType())
                                        && isCollectionType(targetField.getType())) {
                                    final List<List<Class<?>>> sourceFieldGeneric = getFieldGenericClass(sourceField);
                                    final List<List<Class<?>>> targetFieldGeneric = getFieldGenericClass(targetField);
                                    if (!sourceFieldGeneric.isEmpty()
                                            && !targetFieldGeneric.isEmpty()
                                            && sourceFieldGeneric.get(0).size() == 1
                                            && targetFieldGeneric.get(0).size() == 1
                                            && isSupportSerializable(sourceFieldGeneric.get(0).get(0))
                                            && isSupportSerializable(targetFieldGeneric.get(0).get(0))) {
                                        final List<Object> targetFieldDataIterable = new ArrayList<>();
                                        final Iterable<?> sourceFieldDataIterable = (Iterable<?>) sourceFieldData;
                                        for (final Object item : sourceFieldDataIterable) {
                                            Object targetFieldDataIterableItem = null;
                                            if (sourceFieldGeneric.size() > 1
                                                    && targetFieldGeneric.size() > 1
                                                    && isMapType(sourceFieldGeneric.get(0).get(0))
                                                    && isMapType(targetFieldGeneric.get(0).get(0))
                                                    && sourceFieldGeneric.get(1).size() == 2
                                                    && targetFieldGeneric.get(1).size() == 2
                                                    && isBaseType(sourceFieldGeneric.get(1).get(0))
                                                    && isBaseType(targetFieldGeneric.get(1).get(0))
                                                    && sourceFieldGeneric.get(1).get(0) == targetFieldGeneric.get(1).get(0)
                                                    && isIterableType(sourceFieldGeneric.get(1).get(1))
                                                    && isIterableType(targetFieldGeneric.get(1).get(1))
                                                    && sourceFieldGeneric.get(1).get(0) == targetFieldGeneric.get(1).get(0)) {
                                                targetFieldDataIterableItem = clone(item, isDeepClone);
                                            } else if (sourceFieldGeneric.size() > 1
                                                    && targetFieldGeneric.size() > 1
                                                    && isIterableType(sourceFieldGeneric.get(0).get(0))
                                                    && isIterableType(targetFieldGeneric.get(0).get(0))
                                                    && sourceFieldGeneric.get(1).size() == 1
                                                    && targetFieldGeneric.get(1).size() == 1
                                                    && isIterableType(sourceFieldGeneric.get(1).get(0))
                                                    && isIterableType(targetFieldGeneric.get(1).get(0))
                                                    && sourceFieldGeneric.get(1).get(0) == targetFieldGeneric.get(1).get(0)) {
                                                targetFieldDataIterableItem = clone(item, isDeepClone);
                                            } else if (item != null) {
                                                if (sourceFieldGeneric.get(0).get(0)
                                                        != targetFieldGeneric.get(0).get(0)) {
                                                    targetFieldDataIterableItem = run(
                                                            item,
                                                            targetFieldGeneric.get(0).get(0),
                                                            null,
                                                            isDeepClone
                                                    );
                                                } else {
                                                    targetFieldDataIterableItem = clone(item, isDeepClone);
                                                }
                                            }
                                            if (item == null || targetFieldDataIterableItem != null) {
                                                targetFieldDataIterable.add(targetFieldDataIterableItem);
                                            }
                                        }
                                        targetField.set(targetObject, targetFieldDataIterable);
                                    }
                                } else if (isMapType(sourceField.getType()) && isMapType(targetField.getType())) {
                                    final List<List<Class<?>>> sourceFieldGeneric = getFieldGenericClass(sourceField);
                                    final List<List<Class<?>>> targetFieldGeneric = getFieldGenericClass(targetField);
                                    if (!sourceFieldGeneric.isEmpty()
                                            && !targetFieldGeneric.isEmpty()
                                            && sourceFieldGeneric.get(0).size() == 2
                                            && targetFieldGeneric.get(0).size() == 2
                                            && isBaseType(sourceFieldGeneric.get(0).get(0))
                                            && isBaseType(targetFieldGeneric.get(0).get(0))
                                            && sourceFieldGeneric.get(0).get(0) == targetFieldGeneric.get(0).get(0)
                                            && isSupportSerializable(sourceFieldGeneric.get(0).get(1))
                                            && isSupportSerializable(targetFieldGeneric.get(0).get(1))
                                            && sourceFieldGeneric.get(0).get(1) == targetFieldGeneric.get(0).get(1)) {
                                        final Map<Object, Object> targetFieldDataMap = new HashMap<>();
                                        final Map<?, ?> sourceFieldDataMap = (Map<?, ?>) sourceFieldData;
                                        for (final Object key : sourceFieldDataMap.keySet()) {
                                            Object targetFieldDataMapKey = null;
                                            if (key != null) {
                                                if (sourceFieldGeneric.get(0).get(0) == targetFieldGeneric.get(0).get(0)) {
                                                    targetFieldDataMapKey = clone(key, isDeepClone);
                                                } else {
                                                    targetFieldDataMapKey = run(
                                                            key,
                                                            targetFieldGeneric.get(0).get(0),
                                                            null,
                                                            isDeepClone
                                                    );
                                                }
                                            }
                                            final Object value = sourceFieldDataMap.get(key);
                                            Object targetFieldDataMapValue = null;
                                            if (sourceFieldGeneric.size() > 1
                                                    && targetFieldGeneric.size() > 1
                                                    && isMapType(sourceFieldGeneric.get(0).get(1))
                                                    && isMapType(targetFieldGeneric.get(0).get(1))
                                                    && sourceFieldGeneric.get(1).size() == 2
                                                    && targetFieldGeneric.get(1).size() == 2
                                                    && isBaseType(sourceFieldGeneric.get(1).get(0))
                                                    && isBaseType(targetFieldGeneric.get(1).get(0))
                                                    && sourceFieldGeneric.get(1).get(0) == targetFieldGeneric.get(1).get(0)
                                                    && isIterableType(sourceFieldGeneric.get(1).get(1))
                                                    && isIterableType(targetFieldGeneric.get(1).get(1))
                                                    && sourceFieldGeneric.get(1).get(0) == targetFieldGeneric.get(1).get(0)) {
                                                targetFieldDataMapValue = clone(value, isDeepClone);
                                            } else if (sourceFieldGeneric.size() > 1
                                                    && targetFieldGeneric.size() > 1
                                                    && isIterableType(sourceFieldGeneric.get(0).get(1))
                                                    && isIterableType(targetFieldGeneric.get(0).get(1))
                                                    && sourceFieldGeneric.get(1).size() == 1
                                                    && targetFieldGeneric.get(1).size() == 1
                                                    && isIterableType(sourceFieldGeneric.get(1).get(0))
                                                    && isIterableType(targetFieldGeneric.get(1).get(0))
                                                    && sourceFieldGeneric.get(1).get(0) == targetFieldGeneric.get(1).get(0)) {
                                                targetFieldDataMapValue = clone(value, isDeepClone);
                                            } else if (value != null) {
                                                if (sourceFieldGeneric.get(0).get(1) == targetFieldGeneric.get(0).get(1)) {
                                                    targetFieldDataMapValue = clone(value, isDeepClone);
                                                } else {
                                                    targetFieldDataMapValue = run(
                                                            value,
                                                            targetFieldGeneric.get(0).get(1),
                                                            null,
                                                            isDeepClone
                                                    );
                                                }
                                            }
                                            if (value == null || targetFieldDataMapValue != null) {
                                                targetFieldDataMap.put(targetFieldDataMapKey, targetFieldDataMapValue);
                                            }
                                        }
                                        targetField.set(targetObject, targetFieldDataMap);
                                    }
                                } else {
                                    targetField.set(targetObject, clone(sourceFieldData, isDeepClone));
                                }
                            } else if (isSupportSerializable(sourceField.getType())
                                    && isSupportSerializable(targetField.getType())
                                    && !isMapType(sourceField.getType())
                                    && !isIterableType(sourceField.getType())
                                    && !isIterableType(targetField.getType())) {
                                if (isMapType(targetField.getType())) {
                                    final List<List<Class<?>>> generics = getFieldGenericClass(targetField);
                                    if (!generics.isEmpty()
                                            && generics.get(0).size() == 2
                                            && generics.get(0).get(0) == String.class) {
                                        targetField.set(targetObject, toMap(
                                                sourceFieldData,
                                                null,
                                                isDeepClone
                                        ));
                                    }
                                } else {
                                    targetField.set(targetObject, run(
                                            sourceFieldData,
                                            targetField.getType(),
                                            null,
                                            isDeepClone
                                    ));
                                }
                            } else if (isSupportSerializable(sourceField.getType())
                                    && isSupportSerializable(targetField.getType())
                                    && !isMapType(targetField.getType())
                                    && !isIterableType(sourceField.getType())
                                    && !isIterableType(targetField.getType())) {
                                if (isMapType(sourceField.getType())) {
                                    final List<List<Class<?>>> generics = getFieldGenericClass(sourceField);
                                    if (!generics.isEmpty()
                                            && generics.get(0).size() == 2
                                            && generics.get(0).get(0) == String.class) {
                                        targetField.set(targetObject, runMap(
                                                (Map<String, ?>) sourceFieldData,
                                                targetField.getType(),
                                                null,
                                                isDeepClone
                                        ));
                                    }
                                } else {
                                    targetField.set(targetObject, run(
                                            sourceFieldData,
                                            targetField.getType(),
                                            null,
                                            isDeepClone
                                    ));
                                }
                            }
                        }
                    }
                }
            }
            return targetObject;
        } catch (Exception e) {
            e.printStackTrace();
            return defaultTargetObject;
        }
    }

    /**
     * LIST 数据复制
     *
     * @param sourceObject 源数据对象
     * @param targetClass  目标数据类型
     * @param <E>          数据类型
     * @param <T>          结果类型
     * @return 结果类型的 LIST 对象
     */
    @SuppressWarnings("ALL")
    public static <E, T> List<T> runList(List<E> sourceObject, Class<T> targetClass) {
        return runList(sourceObject, targetClass, (List<T>) deepClone(DEFAULT_LIST), false);
    }

    /**
     * LIST 数据复制
     *
     * @param sourceObject 源数据对象
     * @param targetClass  目标数据类型
     * @param isDeepClone  是否深度复制
     * @param <E>          数据类型
     * @param <T>          结果类型
     * @return 结果类型的 LIST 对象
     */
    @SuppressWarnings("ALL")
    public static <E, T> List<T> runList(List<E> sourceObject, Class<T> targetClass, boolean isDeepClone) {
        return runList(sourceObject, targetClass, (List<T>) deepClone(DEFAULT_LIST), isDeepClone);
    }

    /**
     * LIST 数据复制
     *
     * @param sourceObject        源数据对象
     * @param targetClass         目标数据类型
     * @param defaultTargetObject 默认目标数据对象
     * @param <E>                 数据类型
     * @param <T>                 结果类型
     * @return 结果类型的 LIST 对象
     */
    @SuppressWarnings("ALL")
    public static <E, T> List<T> runList(List<E> sourceObject, Class<T> targetClass, List<T> defaultTargetObject) {
        return runList(sourceObject, targetClass, defaultTargetObject, false);
    }

    /**
     * LIST 数据复制
     *
     * @param sourceObject        源数据对象
     * @param targetClass         目标数据类型
     * @param defaultTargetObject 默认目标数据对象
     * @param isDeepClone         是否深度复制
     * @param <E>                 数据类型
     * @param <T>                 结果类型
     * @return 结果类型的 LIST 对象
     */
    @SuppressWarnings("ALL")
    public static <E, T> List<T> runList(
            Iterable<E> sourceObject, Class<T> targetClass, List<T> defaultTargetObject, boolean isDeepClone) {
        try {
            if (sourceObject == null
                    || targetClass == null
                    || isMapType(targetClass)
                    || isIterableType(targetClass)
                    || !isIterableType(sourceObject.getClass())) {
                return defaultTargetObject;
            } else {
                final List<T> result = new ArrayList<>();
                for (final E eSource : sourceObject) {
                    final Class<?> eSourceClass = eSource.getClass();
                    if (eSourceClass == targetClass) {
                        result.add((T) clone(eSource, isDeepClone));
                    } else if (isSupportSerializable(targetClass)
                            && isSupportSerializable(eSourceClass)
                            && !isIterableType(targetClass)
                            && !isIterableType(eSourceClass)
                    ) {
                        if (isMapType(eSourceClass)) {
                            final Map<?, ?> eSourceMap = (Map<?, ?>) eSource;
                            final T targetData = targetClass.getDeclaredConstructor().newInstance();
                            if (!eSourceMap.isEmpty()) {
                                for (final Object key : eSourceMap.keySet()) {
                                    if (key instanceof String keyString) {
                                        runMapKeyValue(keyString, eSourceMap.get(key), targetData, isDeepClone);
                                    }
                                }
                            }
                            result.add(targetData);
                        } else {
                            result.add(run(eSource, targetClass, null, isDeepClone));
                        }
                    }
                }
                return result;
            }
        } catch (Exception e) {
            return defaultTargetObject;
        }
    }

    /**
     * MAP 数据复制
     *
     * @param sourceObject 源数据对象
     * @param <V>          数据类型
     * @param <T>          转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <V, T> T runMap(Map<String, V> sourceObject, Class<T> targetClass) {
        return runMap(sourceObject, targetClass, null, false);
    }

    /**
     * MAP 数据复制
     *
     * @param sourceObject 源数据对象
     * @param targetClass  目标数据类型
     * @param isDeepClone  是否深度复制
     * @param <V>          数据类型
     * @param <T>          转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <V, T> T runMap(Map<String, V> sourceObject, Class<T> targetClass, boolean isDeepClone) {
        return runMap(sourceObject, targetClass, null, isDeepClone);
    }

    /**
     * MAP 数据复制
     *
     * @param sourceObject        源数据对象
     * @param targetClass         目标数据类型
     * @param defaultTargetObject 默认目标数据对象
     * @param <V>                 数据类型
     * @param <T>                 转换的目标泛型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <V, T> T runMap(Map<String, V> sourceObject, Class<T> targetClass, T defaultTargetObject) {
        return runMap(sourceObject, targetClass, defaultTargetObject, false);
    }


    /**
     * MAP 数据复制
     *
     * @param sourceObject        源数据对象
     * @param targetClass         目标数据类型
     * @param defaultTargetObject 默认目标数据对象
     * @param isDeepClone         是否深度复制
     * @param <V>                 数据类型
     * @param <T>                 转换的目标泛型
     * @return 目标数据对象
     */
    public static <V, T> T runMap(
            Map<String, V> sourceObject, Class<T> targetClass, T defaultTargetObject, boolean isDeepClone) {
        try {
            if (sourceObject == null || targetClass == null) {
                return defaultTargetObject;
            } else {
                final T targetObject = targetClass.getDeclaredConstructor().newInstance();
                return runMap(sourceObject, targetObject, defaultTargetObject, isDeepClone);
            }
        } catch (Exception e) {
            return defaultTargetObject;
        }
    }

    /**
     * MAP 数据复制
     *
     * @param sourceObject 源数据对象
     * @param targetObject 目标数据对象
     * @param <V>          数据类型
     * @param <T>          结果类型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <V, T> T runMap(Map<String, V> sourceObject, T targetObject) {
        return runMap(sourceObject, targetObject, null, false);
    }

    /**
     * MAP 数据复制
     *
     * @param sourceObject 源数据对象
     * @param targetObject 目标数据对象
     * @param isDeepClone  是否深度复制
     * @param <V>          数据类型
     * @param <T>          结果类型
     * @return 目标数据对象
     */
    @SuppressWarnings("ALL")
    public static <V, T> T runMap(Map<String, V> sourceObject, T targetObject, boolean isDeepClone) {
        return runMap(sourceObject, targetObject, null, isDeepClone);
    }

    /**
     * MAP 数据复制
     *
     * @param sourceObject        源数据对象
     * @param targetObject        目标数据对象
     * @param defaultTargetObject 默认目标数据对象
     * @param <V>                 数据类型
     * @param <T>                 结果类型
     * @return 结果类型的 LIST 对象
     */
    @SuppressWarnings("ALL")
    public static <V, T> T runMap(Map<String, V> sourceObject, T targetObject, T defaultTargetObject) {
        return runMap(sourceObject, targetObject, defaultTargetObject, false);
    }

    /**
     * MAP 数据复制
     *
     * @param sourceObject        源数据对象
     * @param targetObject        目标数据对象
     * @param defaultTargetObject 默认目标数据对象
     * @param isDeepClone         是否深度复制
     * @param <V>                 数据类型
     * @param <T>                 结果类型
     * @return 结果类型的 LIST 对象
     */
    public static <V, T> T runMap(
            Map<String, V> sourceObject, T targetObject, T defaultTargetObject, boolean isDeepClone) {
        if (sourceObject == null
                || targetObject == null
                || !isMapType(sourceObject.getClass())
                || isMapType(targetObject.getClass())
                || isIterableType(targetObject.getClass())
                || !isSupportSerializable(targetObject.getClass())) {
            return defaultTargetObject;
        } else {
            try {
                final Field[] targetFields = getFields(targetObject.getClass());
                for (final Field targetField : targetFields) {
                    final String key = targetField.getName();
                    final V value = sourceObject.get(key);
                    if (value != null) {
                        runMapKeyValue(key, value, targetObject, isDeepClone);
                    }
                }
                return targetObject;
            } catch (Exception e) {
                // ignore
            }
        }
        return defaultTargetObject;
    }

    /**
     * RUN MAP ITEM 复制
     *
     * @param key         源 KEY 名词
     * @param value       源 VALUE 内容
     * @param data        目标数据对象
     * @param isDeepClone 是否深度复制
     */
    private static void runMapKeyValue(String key, Object value, Object data, boolean isDeepClone) {
        if (value != null
                && !(value instanceof Map)
                && !(value instanceof Iterable)) {
            try {
                final Field field = data.getClass().getDeclaredField(key);
                field.setAccessible(true);
                if (value.getClass() == field.getType()) {
                    field.set(data, clone(value, isDeepClone));
                } else {
                    field.set(data, run(value, field.getType(), null, isDeepClone));
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * 将对象转换为 MAP 对象
     *
     * @param sourceObject 源数据对象
     * @return 转换的对象
     */
    @SuppressWarnings("ALL")
    public static Map<String, Object> toMap(Object sourceObject) {
        return (Map<String, Object>) toMap(sourceObject, null, false);
    }

    /**
     * 将对象转换为 MAP 对象
     *
     * @param sourceObject        源数据对象
     * @param defaultTargetObject 默认目标数据对象
     * @return 转换的对象
     */
    @SuppressWarnings("ALL")
    public static Map<String, ?> toMap(Object sourceObject, Map<String, ?> defaultTargetObject) {
        return toMap(sourceObject, defaultTargetObject, false);
    }

    /**
     * 将对象转换为 MAP 对象
     *
     * @param sourceObject 源数据对象
     * @param isDeepClone  是否深度复制
     * @return 转换的对象
     */
    @SuppressWarnings("ALL")
    public static Map<String, ?> toMap(Object sourceObject, boolean isDeepClone) {
        return toMap(sourceObject, null, isDeepClone);
    }

    /**
     * 将对象转换为 MAP 对象
     *
     * @param sourceObject        源数据对象
     * @param defaultTargetObject 默认目标数据对象
     * @param isDeepClone         是否深度复制
     * @return 转换的对象
     */
    public static Map<String, ?> toMap(Object sourceObject, Map<String, ?> defaultTargetObject, boolean isDeepClone) {
        if (sourceObject != null
                && !isIterableType(sourceObject.getClass())
                && isSupportSerializable(sourceObject.getClass())) {
            try {
                final Class<?> sourceClass = sourceObject.getClass();
                final Map<String, Object> result;
                if (isMapType(sourceClass)) {
                    final Map<?, ?> sourceMap = (Map<?, ?>) sourceObject;
                    result = new HashMap<>(sourceMap.size());
                    for (final Object key : sourceMap.keySet()) {
                        if (isBaseType(key.getClass())) {
                            final Object value = sourceMap.get(key);
                            if (isBaseType(value.getClass())) {
                                result.put(String.valueOf(clone(key, isDeepClone)), clone(value, isDeepClone));
                            } else {
                                if (isIterableType(value.getClass())) {
                                    result.put(
                                            String.valueOf(clone(key, isDeepClone)),
                                            toList(value, null, isDeepClone)
                                    );
                                } else {
                                    result.put(
                                            String.valueOf(clone(key, isDeepClone)),
                                            toMap(value, null, isDeepClone)
                                    );
                                }
                            }
                        }
                    }
                } else {
                    final Field[] sourceFields = getFields(sourceClass);
                    result = new HashMap<>(sourceFields.length);
                    for (Field sourceField : sourceFields) {
                        sourceField.setAccessible(true);
                        final Object sourceFieldObject = sourceField.get(sourceObject);
                        final Class<?> sourceFieldClass = sourceField.getType();
                        if (isBaseType(sourceFieldClass)) {
                            result.put(sourceField.getName(), clone(sourceFieldObject, isDeepClone));
                        } else {
                            if (isIterableType(sourceFieldClass)) {
                                result.put(
                                        sourceField.getName(),
                                        toList(sourceFieldObject, null, isDeepClone)
                                );
                            } else {
                                result.put(
                                        sourceField.getName(),
                                        toMap(sourceFieldObject, null, isDeepClone)
                                );
                            }
                        }
                    }
                }
                return result;
            } catch (Exception e) {
                // ignore
            }
        }
        return defaultTargetObject;
    }

    /**
     * 将 LIST<OBJECT> -> LIST<MAP<STRING, OBJECT> 对象
     *
     * @param sourceObject 源数据对象
     * @return 转换的对象
     */
    @SuppressWarnings("ALL")
    public static List<Object> toList(Object sourceObject) {
        return toList(sourceObject, null, false);
    }

    /**
     * 将 LIST<OBJECT> -> LIST<MAP<STRING, OBJECT> 对象
     *
     * @param sourceObject        源数据对象
     * @param defaultTargetObject 默认目标对象
     * @return 转换的对象
     */
    @SuppressWarnings("ALL")
    public static List<Object> toList(Object sourceObject, List<Object> defaultTargetObject) {
        return toList(sourceObject, defaultTargetObject, false);
    }

    /**
     * 将 LIST<OBJECT> -> LIST<MAP<STRING, OBJECT> 对象
     *
     * @param sourceObject 源数据对象
     * @param isDeepClone  是否深度复制
     * @return 转换的对象
     */
    @SuppressWarnings("ALL")
    public static List<Object> toList(Object sourceObject, boolean isDeepClone) {
        return toList(sourceObject, null, isDeepClone);
    }

    /**
     * 将 LIST<OBJECT> -> LIST<MAP<STRING, OBJECT> 对象
     *
     * @param sourceObject        源数据对象
     * @param defaultTargetObject 默认目标对象
     * @param isDeepClone         是否深度复制
     * @return 转换的对象
     */
    public static List<Object> toList(Object sourceObject, List<Object> defaultTargetObject, boolean isDeepClone) {
        try {
            if (sourceObject == null || isIterableType(sourceObject.getClass())) {
                return defaultTargetObject;
            } else {
                final List<Object> result = new ArrayList<>();
                final Iterable<?> iterable = (Iterable<?>) sourceObject;
                for (final Object item : iterable) {
                    final Class<?> itemClass = item.getClass();
                    if (isBaseType(itemClass)) {
                        result.add(clone(item, isDeepClone));
                    } else if (isIterableType(itemClass)) {
                        result.add(toList(item, null, isDeepClone));
                    } else {
                        result.add(toMap(item, null, isDeepClone));
                    }
                }
                return result;
            }
        } catch (Exception e) {
            // ignore
        }
        return defaultTargetObject;
    }

    public static Map<String, Object> objectToMap(Object source) {
        if (source == null
                || isMapType(source.getClass())
                || isCollectionType(source.getClass())) {
            return null;
        } else {
            try {
                final Map<String, Object> result = new HashMap<>();
                final Class<?> sourceClass = source.getClass();
                for (final Field field : getFields(sourceClass)) {
                    field.setAccessible(true);
                    result.put(field.getName(), field.get(source));
                }
                return result;
            } catch (Exception ignore) {
                // ignore
                return null;
            }
        }
    }

    public static Map<String, Object> objectToMap(Object source, Map<String, Object> defaultTargetObject) {
        if (source == null
                || isMapType(source.getClass())
                || isCollectionType(source.getClass())) {
            return defaultTargetObject;
        } else {
            try {
                final Map<String, Object> result = new HashMap<>();
                final Class<?> sourceClass = source.getClass();
                for (final Field field : getFields(sourceClass)) {
                    field.setAccessible(true);
                    result.put(field.getName(), field.get(source));
                }
                return result;
            } catch (Exception ignore) {
                // ignore
                return defaultTargetObject;
            }
        }
    }

    /**
     * 判断是否为基础类型
     *
     * @param clazz class 类型
     * @return 是否为基础类型
     */
    private static boolean isBaseType(Class<?> clazz) {
        final String clazzTypeName = clazz.getTypeName();
        return switch (clazzTypeName) {
            case "byte", "java.lang.Byte",
                 "short", "java.lang.Short",
                 "int", "java.lang.Integer",
                 "long", "java.lang.Long",
                 "float", "java.lang.Float",
                 "double", "java.lang.Double",
                 "char", "java.lang.Character",
                 "boolean", "java.lang.Boolean",
                 "java.lang.String", "java.lang.Object" -> true;
            default -> false;
        };
    }

    /**
     * 判断是否为 MAP 类型
     *
     * @param clazz class 类型
     * @return 是否为 MAP 类型
     */
    private static boolean isMapType(Class<?> clazz) {
        final Class<?>[] cls = getInterfaces(clazz);
        for (final Class<?> item : cls) {
            if (item == Map.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为 ITERABLE 类型
     *
     * @param clazz class 类型
     * @return 是否为 ITERABLE 类型
     */
    private static boolean isIterableType(Class<?> clazz) {
        final Class<?>[] cls = getInterfaces(clazz);
        for (final Class<?> item : cls) {
            if (item == Iterable.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为 COLLECTION 类型
     *
     * @param clazz class 类型
     * @return 是否为 COLLECTION 类型
     */
    private static boolean isCollectionType(Class<?> clazz) {
        final Class<?>[] cls = getInterfaces(clazz);
        for (final Class<?> item : cls) {
            if (item == Collection.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否接口 java.io.Serializable
     *
     * @param clazz class 类型
     * @return 是否接口 java.io.Serializable
     */
    public static boolean isSupportSerializable(Class<?> clazz) {
        final Class<?>[] cls = clazz.getInterfaces();
        if (clazz == Map.class || clazz == List.class) {
            return true;
        }
        for (final Class<?> item : cls) {
            if (item == Serializable.class) {
                return true;
            }
        }
        return false;
    }

    /**
     * 读取 field 数据
     *
     * @param clazz class 类型
     * @return field 数组数据
     */
    private static Field[] getFields(Class<?> clazz) {
        Class<?> cls = clazz;
        final List<Field> fields = new ArrayList<>();
        do {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        } while (cls != null && cls != Object.class && isSupportSerializable(cls));
        return fields.stream().filter(f ->
                !(Modifier.isStatic(f.getModifiers())
                        || Modifier.isTransient(f.getModifiers()))
        ).toArray(Field[]::new);
    }

    /**
     * 读取接口的类型
     *
     * @param clazz class 类型
     * @return 接口的 class 数组
     */
    private static Class<?>[] getInterfaces(Class<?> clazz) {
        final Class<?>[] cls = clazz.getInterfaces();
        final List<Class<?>> result = new ArrayList<>(List.of(cls));
        for (final Class<?> item : cls) {
            result.addAll(List.of(getInterfaces(item)));
        }
        return result.toArray(new Class<?>[0]);
    }

    /**
     * 读取字段的泛型类型
     *
     * @param field 字段
     * @return 泛型的类型
     */
    private static List<List<Class<?>>> getFieldGenericClass(Field field) {
        final List<List<Class<?>>> result = new ArrayList<>();
        try {
            int index = -1;
            StringBuilder content = null;
            final String typeName = field.getGenericType().getTypeName();
            for (int i = 0; i < typeName.length(); i++) {
                final char ch = typeName.charAt(i);
                if (ch == '<') {
                    if (content != null && !content.isEmpty()) {
                        result.get(index).add(Class.forName(content.toString().trim()));
                    }
                    index += 1;
                    if (index >= result.size()) {
                        result.add(new ArrayList<>());
                    }
                    content = new StringBuilder();
                } else if (ch == '>' && content != null) {
                    if (index >= result.size()) {
                        result.add(new ArrayList<>());
                    }
                    if (!content.isEmpty()) {
                        result.get(index).add(Class.forName(content.toString().trim()));
                    }
                    content = new StringBuilder();
                    index -= 1;
                } else if (ch == ',' && content != null) {
                    if (index >= result.size()) {
                        result.add(new ArrayList<>());
                    }
                    if (!content.isEmpty()) {
                        result.get(index).add(Class.forName(content.toString().trim()));
                    }
                    content = new StringBuilder();
                } else if (content != null) {
                    content.append(ch);
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return result;
    }

    /**
     * 复制
     *
     * @param source      需要复制的对象
     * @param isDeepClone 是否深度复制
     * @param <T>         泛型
     * @return 复制的对象
     */
    private static <T> T clone(T source, boolean isDeepClone) {
        return isDeepClone ? deepClone(source) : source;
    }

    /**
     * 深度复制
     *
     * @param source 需要复制的对象
     * @param <T>    泛型
     * @return 深度复制的对象
     */
    @SuppressWarnings("ALL")
    private static <T> T deepClone(T source) {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = null;
        try {
            if (source != null) {
                bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(source);
                bis = new ByteArrayInputStream(bos.toByteArray());
                ois = new ObjectInputStream(bis);
                return (T) ois.readObject();
            }
        } catch (Exception e) {
            // ignore
        } finally {
            close(ois);
            close(bis);
            close(oos);
            close(bos);
        }
        return null;
    }

    /**
     * 关闭字节流方法
     *
     * @param closeable 需要关闭的对象
     */
    private static void close(final Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}


