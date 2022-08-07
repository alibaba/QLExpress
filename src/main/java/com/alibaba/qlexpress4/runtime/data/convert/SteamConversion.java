package com.alibaba.qlexpress4.runtime.data.convert;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:33
 */
public class SteamConversion {

    public static <T> Stream<T> stream(final T t) {
        return Stream.of(t);
    }

    public static <T> Stream<T> stream(final T[] t) {
        return Arrays.stream(t);
    }

    public static Stream<Integer> stream(final int[] ints) {
        return Arrays.stream(ints).boxed();
    }

    public static Stream<Long> stream(final long[] longs) {
        return Arrays.stream(longs).boxed();
    }

    public static Stream<Double> stream(final double[] doubles) {
        return Arrays.stream(doubles).boxed();
    }

    public static Stream<Character> stream(final char[] chars) {
        return IntStream.range(0, chars.length).mapToObj(i -> chars[i]);
    }

    public static Stream<Byte> stream(final byte[] bytes) {
        return IntStream.range(0, bytes.length).mapToObj(i -> bytes[i]);
    }

    public static Stream<Short> stream(final short[] shorts) {
        return IntStream.range(0, shorts.length).mapToObj(i -> shorts[i]);
    }

    public static Stream<Boolean> stream(final boolean[] booleans) {
        return IntStream.range(0, booleans.length).mapToObj(i -> booleans[i]);
    }

    public static Stream<Float> stream(final float[] floats) {
        return IntStream.range(0, floats.length).mapToObj(i -> floats[i]);
    }

    public static <T> Stream<T> stream(final Enumeration<T> enumeration) {
        return stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
            @Override
            public void forEachRemaining(final Consumer<? super T> action) {
                while (enumeration.hasMoreElements()) {
                    action.accept(enumeration.nextElement());
                }
            }
            @Override
            public boolean tryAdvance(final Consumer<? super T> consumer) {
                if (enumeration.hasMoreElements()) {
                    consumer.accept(enumeration.nextElement());
                    return true;
                }
                return false;
            }
        });
    }

    public static <T> Stream<T> stream(final Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T> Stream<T> stream(final Iterator<T> iterator) {
        return stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED));
    }

    public static <T> Stream<T> stream(final Spliterator<T> spliterator) {
        return StreamSupport.stream(spliterator, false);
    }

    public static <T> Stream<T> stream(final Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    public static IntStream stream(final OptionalInt optionalInt) {
        if (!optionalInt.isPresent()) {
            return IntStream.empty();
        }
        return IntStream.of(optionalInt.getAsInt());
    }

    public static LongStream stream(final OptionalLong optionalLong) {
        if (!optionalLong.isPresent()) {
            return LongStream.empty();
        }
        return LongStream.of(optionalLong.getAsLong());
    }

    public static DoubleStream stream(final OptionalDouble optionalDouble) {
        if (!optionalDouble.isPresent()) {
            return DoubleStream.empty();
        }
        return DoubleStream.of(optionalDouble.getAsDouble());
    }

}
