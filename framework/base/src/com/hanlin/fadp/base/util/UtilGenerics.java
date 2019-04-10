package com.hanlin.fadp.base.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
/**
 * @ClassName: UtilGenerics 
 * @Description: TODO
 * @author: 祖国
 * @date: 2015年12月21日 下午2:40:45
 */
public class UtilGenerics {

    public static final String module = UtilMisc.class.getName();

    /**
     * @Title: cast 
     * @Description: 将给定的Object转换为泛型V对应的具体型
     * @param object 待转换的Object
     * @return: V 转换后的型
     */
    @SuppressWarnings("unchecked")
    public static <V> V cast(Object object) {
        return (V) object;
    }

    /**
     * @Title: checkCollectionCast 
     * @Description: 将Object转换为指定类型
     * @param object 待转换的Object
     * @param clz 转换后的类型
     * @return: C 返回转换后的对象，否则抛出异常
     */
    private static <C extends Collection<?>> C checkCollectionCast(Object object, Class<C> clz) {
        return clz.cast(object);
    }

    public static <C extends Collection<?>> void checkCollectionContainment(Object object, Class<C> clz, Class<?> type) {
        if (object != null) {
            if (!(clz.isInstance(object))) throw new ClassCastException("Not a " + clz.getName());
            int i = 0;
            for (Object value: (Collection<?>) object) {
                if (value != null && !type.isInstance(value)) {
                    throw new IllegalArgumentException("Value(" + i + "), with value(" + value + ") is not a " + type.getName());
                }
                i++;
            }
        }
    }

    /**
     * @Title: checkCollection 
     * @Description: 将输入对象转换为某个对象，
     * @param object 待转换对象
     * @return: Collection<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> checkCollection(Object object) {
        return (Collection<T>) checkCollectionCast(object, Collection.class);
    }

    public static <T> Collection<T> checkCollection(Object object, Class<T> type) {
        checkCollectionContainment(object, Collection.class, type);
        return checkCollection(object);
    }

    /**
     * @Title: checkList 
     * @Description: 将Object转换为指定对象，放入list中
     * @param object 待转换的对象
     * @return: List<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> checkList(Object object) {
        return (List<T>) checkCollectionCast(object, List.class);
    }

    public static <T> List<T> checkList(Object object, Class<T> type) {
        checkCollectionContainment(object, List.class, type);
        return checkList(object);
    }

    /**
     * @Title: checkMap 
     * @Description: 将Object转化为Map实例
     * @param object
     * @return: Map<K,V>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> checkMap(Object object) {
        if (object != null && !(object instanceof Map)) throw new ClassCastException("Not a map");
        return (Map<K, V>) object;
    }

    /**
     * @Title: checkMap 
     * @Description: 通过给定的key和value的Class型获取map，确保key和value的class型为map中泛型，
     * 调用了checkMap(object)方法
     * @param object：map对象
     * @param keyType 给定的key的class型
     * @param valueType 给定的value的class型
     * @return: Map<K,V> 返回对应给定key和value的map
     */
    public static <K, V> Map<K, V> checkMap(Object object, Class<K> keyType, Class<V> valueType) {
        if (object != null) {
            if (!(object instanceof Map<?, ?>)) throw new ClassCastException("Not a map");
            Map<?, ?> map = (Map<?,?>) object;
            int i = 0;
            for (Map.Entry<?, ?> entry: map.entrySet()) {
                if (entry.getKey() != null && !keyType.isInstance(entry.getKey())) {
                    throw new IllegalArgumentException("Key(" + i + "), with value(" + entry.getKey() + ") is not a " + keyType);
                }
                if (entry.getValue() != null && !valueType.isInstance(entry.getValue())) {
                    throw new IllegalArgumentException("Value(" + i + "), with value(" + entry.getValue() + ") is not a " + valueType);
                }
                i++;
            }
        }
        return checkMap(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> Stack<T> checkStack(Object object) {
        return (Stack<T>) checkCollectionCast(object, Stack.class);
    }

    public static <T> Stack<T> checkStack(Object object, Class<T> type) {
        checkCollectionContainment(object, Stack.class, type);
        return checkStack(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<T> checkSet(Object object) {
        return (Set<T>) checkCollectionCast(object, Set.class);
    }

    public static <T> Set<T> checkSet(Object object, Class<T> type) {
        checkCollectionContainment(object, Set.class, type);
        return checkSet(object);
    }

    /** Returns the Object argument as a parameterized List if the Object argument
     * is an instance of List. Otherwise returns null.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> toList(Object object) {
        if (object != null && !(object instanceof List)) return null;
        return (List<T>) object;
    }

    /** Returns the Object argument as a parameterized Map if the Object argument
     * is an instance of Map. Otherwise returns null.
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> toMap(Object object) {
        if (object != null && !(object instanceof Map)) return null;
        return (Map<K, V>) object;
    }

    public static <K, V> Map<K, V> toMap(Class<K> keyType, Class<V> valueType, Object... data) {
        if (data == null) {
            return null;
        }
        if (data.length % 2 == 1) {
            throw new IllegalArgumentException("You must pass an even sized array to the toMap method");
        }
        Map<K, V> map = new LinkedHashMap<K, V>();
        for (int i = 0; i < data.length;) {
            Object key = data[i];
            if (key != null && !(keyType.isInstance(key))) throw new IllegalArgumentException("Key(" + i + ") is not a " + keyType.getName() + ", was(" + key.getClass().getName() + ")");
            i++;
            Object value = data[i];
            if (value != null && !(valueType.isInstance(value))) throw new IllegalArgumentException("Value(" + i + ") is not a " + keyType.getName() + ", was(" + key.getClass().getName() + ")");
            i++;
            map.put(keyType.cast(key), valueType.cast(value));
        }
        return map;
    }

    @SuppressWarnings("hiding")
    public static <K, Object> Map<K, Object> toMap(Class<K> keyType, Object... data) {
        if (data == null) {
            return null;
        }
        if (data.length % 2 == 1) {
            throw new IllegalArgumentException("You must pass an even sized array to the toMap method");
        }
        Map<K, Object> map = new LinkedHashMap<K, Object>();
        for (int i = 0; i < data.length;) {
            Object key = data[i];
            if (key != null && !(keyType.isInstance(key))) throw new IllegalArgumentException("Key(" + i + ") is not a " + keyType.getName() + ", was(" + key.getClass().getName() + ")");
            i++;
            Object value = data[i];
            map.put(keyType.cast(key), value);
        }
        return map;
    }
}
