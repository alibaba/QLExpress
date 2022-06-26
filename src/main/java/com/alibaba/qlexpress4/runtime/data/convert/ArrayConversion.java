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
        Collection<?> collection = toCollection(object);
        Object array = Array.newInstance(elementType, collection.size());

        int i = 0;
        for (Object element : collection) {
            Array.set(array, i++, InstanceConversion.castObject(qlCaches, element, elementType));
        }

        return array;
    }

    public static Collection toCollection(final Object value) {
        if (value == null) {
            return Collections.EMPTY_LIST;
        } else if (value instanceof Collection) {
            return (Collection) value;
        } else if (value instanceof Map) {
            return ((Map) value).entrySet();
        } else if (value.getClass().isArray()) {
            return arrayToCollection(value);
        } else if (value instanceof BaseStream) {
            return toList((BaseStream) value);
        } else if (value instanceof String) {
            return toList((CharSequence) value);
        } else {
            return Collections.singletonList(value);
        }
    }

    public static <T> List<T> toList(final Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }

    public static <T> List<T> toList(final BaseStream<T, ? extends BaseStream> baseStream) {
        return SteamConversion.stream(baseStream.iterator()).collect(Collectors.toList());
    }


    public static List<String> toList(final CharSequence charSequence) {
        String s = charSequence.toString();
        final int n = s.length();
        List<String> answer = new ArrayList<>(n);
        for (int i = 0; i < n; i += 1) {
            answer.add(s.substring(i, i + 1));
        }
        return answer;
    }

    public static Collection arrayToCollection(Object value) {
        if (value.getClass().getComponentType().isPrimitive()) {
            return primitiveArrayToList(value);
        }
        return arrayToCollection((Object[]) value);
    }

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

    public static <T> Collection<T> arrayToCollection(T[] value) {
        return Arrays.asList(value);
    }

}
