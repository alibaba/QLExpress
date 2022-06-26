package com.alibaba.qlexpress4.runtime.data.convert;

import com.alibaba.qlexpress4.cache.QLCaches;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.*;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:35
 */
public class ArrayConversion {
    public static Object trans(QLCaches qlCaches, final Object object, final Class type) {
        if (type.isAssignableFrom(object.getClass())) {
            return object;
        }

        if (object instanceof IntStream) {
            if (type.equals(int[].class)) {
                return ((IntStream) object).toArray();
            } else if (type.equals(long[].class)) {
                return ((IntStream) object).asLongStream().toArray();
            } else if (type.equals(double[].class)) {
                return ((IntStream) object).asDoubleStream().toArray();
            } else if (type.equals(Integer[].class)) {
                return ((IntStream) object).boxed().toArray(Integer[]::new);
            }
        } else if (object instanceof LongStream) {
            if (type.equals(long[].class)) {
                return ((LongStream) object).toArray();
            } else if (type.equals(double[].class)) {
                return ((LongStream) object).asDoubleStream().toArray();
            } else if (type.equals(Long[].class)) {
                return ((LongStream) object).boxed().toArray(Long[]::new);
            }
        } else if (object instanceof DoubleStream) {
            if (type.equals(double[].class)) {
                return ((DoubleStream) object).toArray();
            } else if (type.equals(Double[].class)) {
                return ((DoubleStream) object).boxed().toArray(Double[]::new);
            }
        }

        Class<?> elementType = type.getComponentType();
        Collection<?> collection = asCollection(object);
        Object array = Array.newInstance(elementType, collection.size());

        int i = 0;
        for (Object element : collection) {
            Array.set(array, i++, InstanceConversion.castObject(qlCaches, element, elementType));
        }

        return array;
    }

    public static Collection asCollection(final Object value) {
        if (value == null) {
            return Collections.EMPTY_LIST;
        } else if (value instanceof Collection) {
            return (Collection) value;
        } else if (value instanceof Map) {
            return ((Map) value).entrySet();
        } else if (value.getClass().isArray()) {
            return arrayAsCollection(value);
        } else if (value instanceof BaseStream) {
            return toList((BaseStream) value);
        } else if (value instanceof String) {
            return toList((CharSequence) value);
        } else {
            return Collections.singletonList(value);
        }
    }


    /**
     * Accumulates the elements of stream into a new List.
     *
     * @param self the stream
     * @param <T>  the type of element
     * @return a new {@code java.util.List} instance
     * @since 2.5.0
     */
    public static <T> List<T> toList(final Stream<T> self) {
        return self.collect(Collectors.toList());
    }

    /**
     * Accumulates the elements of stream into a new List.
     *
     * @param self the {@code java.util.stream.BaseStream}
     * @param <T>  the type of element
     * @return a new {@code java.util.List} instance
     * @since 2.5.0
     */
    public static <T> List<T> toList(final BaseStream<T, ? extends BaseStream> self) {
        return SteamConversion.stream(self.iterator()).collect(Collectors.toList());
    }


    public static List<String> toList(final CharSequence self) {
        String s = self.toString();
        final int n = s.length();
        List<String> answer = new ArrayList<>(n);
        for (int i = 0; i < n; i += 1) {
            answer.add(s.substring(i, i + 1));
        }
        return answer;
    }

    public static Collection arrayAsCollection(Object value) {
        if (value.getClass().getComponentType().isPrimitive()) {
            return primitiveArrayToList(value);
        }
        return arrayAsCollection((Object[]) value);
    }


    /**
     * Allows conversion of arrays into a mutable List
     *
     * @param array an array
     * @return the array as a List
     */
    public static List primitiveArrayToList(Object array) {
        int size = Array.getLength(array);
        List list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            Object item = Array.get(array, i);
            if (item != null && item.getClass().isArray() && item.getClass().getComponentType().isPrimitive()) {
                item = primitiveArrayToList(item);
            }
            list.add(item);
        }
        return list;
    }

    public static <T> Collection<T> arrayAsCollection(T[] value) {
        return Arrays.asList(value);
    }

}
