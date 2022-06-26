package com.alibaba.qlexpress4.runtime.data.convert;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.*;

/**
 * @Author TaoKan
 * @Date 2022/6/26 下午3:33
 */
public class SteamConversion {

    //--------------------------------------------------------------------------

    /**
     * Returns a sequential {@link Stream} containing a single element.
     *
     * <pre class="TestCase">
     * def item = 'string'
     * assert item.stream().toList() == ['string']
     * assert item.stream().findFirst().isPresent()
     * </pre>
     *
     * @since 3.0.0
     */
    public static <T> Stream<T> stream(final T self) {
        return Stream.of(self);
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param <T>  The type of the array elements
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static <T> Stream<T> stream(final T[] self) {
        return Arrays.stream(self);
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static Stream<Integer> stream(final int[] self) {
        return Arrays.stream(self).boxed();
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static Stream<Long> stream(final long[] self) {
        return Arrays.stream(self).boxed();
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static Stream<Double> stream(final double[] self) {
        return Arrays.stream(self).boxed();
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static Stream<Character> stream(final char[] self) {
        return IntStream.range(0, self.length).mapToObj(i -> self[i]);
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static Stream<Byte> stream(final byte[] self) {
        return IntStream.range(0, self.length).mapToObj(i -> self[i]);
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static Stream<Short> stream(final short[] self) {
        return IntStream.range(0, self.length).mapToObj(i -> self[i]);
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static Stream<Boolean> stream(final boolean[] self) {
        return IntStream.range(0, self.length).mapToObj(i -> self[i]);
    }

    /**
     * Returns a sequential {@link Stream} with the specified array as its
     * source.
     *
     * @param self The array, assumed to be unmodified during use
     * @return a {@code Stream} for the array
     * @since 2.5.0
     */
    public static Stream<Float> stream(final float[] self) {
        return IntStream.range(0, self.length).mapToObj(i -> self[i]);
    }

    /**
     * Returns a sequential {@link Stream} with the specified element(s) as its
     * source.
     * <pre class="TestCase">
     * def tokens = new StringTokenizer('one two')
     * assert tokens.stream().toList() == ['one', 'two']
     * </pre>
     *
     * @since 3.0.0
     */
    public static <T> Stream<T> stream(final Enumeration<T> self) {
        return stream(new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
            @Override
            public void forEachRemaining(final Consumer<? super T> action) {
                while (self.hasMoreElements()) {
                    action.accept(self.nextElement());
                }
            }

            @Override
            public boolean tryAdvance(final Consumer<? super T> action) {
                if (self.hasMoreElements()) {
                    action.accept(self.nextElement());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Returns a sequential {@link Stream} with the specified element(s) as its
     * source.
     *
     * <pre class="TestCase">
     * class Items implements Iterable<String> {
     *   Iterator&lt;String&gt; iterator() {
     *     ['one', 'two'].iterator()
     *   }
     * }
     * def items = new Items()
     * assert items.stream().toList() == ['one', 'two']
     * </pre>
     *
     * @since 3.0.0
     */
    public static <T> Stream<T> stream(final Iterable<T> self) {
        return StreamSupport.stream(self.spliterator(), false);
    }

    /**
     * Returns a sequential {@link Stream} with the specified element(s) as its
     * source.
     *
     * <pre class="TestCase">
     * [].iterator().stream().toList().isEmpty()
     * ['one', 'two'].iterator().stream().toList() == ['one', 'two']
     * </pre>
     *
     * @since 3.0.0
     */
    public static <T> Stream<T> stream(final Iterator<T> self) {
        return stream(Spliterators.spliteratorUnknownSize(self, Spliterator.ORDERED));
    }

    /**
     * Returns a sequential {@link Stream} with the specified element(s) as its
     * source.
     *
     * <pre class="TestCase">
     * assert [].spliterator().stream().toList().isEmpty()
     * assert ['one', 'two'].spliterator().stream().toList() == ['one', 'two']
     * </pre>
     *
     * @since 3.0.0
     */
    public static <T> Stream<T> stream(final Spliterator<T> self) {
        return StreamSupport.stream(self, false);
    }


    /**
     * If a value is present in the {@link Optional}, returns a {@link Stream}
     * with the value as its source or else an empty stream.
     *
     * @since 3.0.0
     */
    public static <T> Stream<T> stream(final Optional<T> self) {
        return self.map(Stream::of).orElseGet(Stream::empty);
    }

    //

    /**
     * If a value is present in the {@link OptionalInt}, returns an {@link IntStream}
     * with the value as its source or else an empty stream.
     *
     * @since 3.0.0
     */
    public static IntStream stream(final OptionalInt self) {
        if (!self.isPresent()) {
            return IntStream.empty();
        }
        return IntStream.of(self.getAsInt());
    }

    /**
     * If a value is present in the {@link OptionalLong}, returns a {@link LongStream}
     * with the value as its source or else an empty stream.
     *
     * @since 3.0.0
     */
    public static LongStream stream(final OptionalLong self) {
        if (!self.isPresent()) {
            return LongStream.empty();
        }
        return LongStream.of(self.getAsLong());
    }

    /**
     * If a value is present in the {@link OptionalDouble}, returns a {@link DoubleStream}
     * with the value as its source or else an empty stream.
     *
     * @since 3.0.0
     */
    public static DoubleStream stream(final OptionalDouble self) {
        if (!self.isPresent()) {
            return DoubleStream.empty();
        }
        return DoubleStream.of(self.getAsDouble());
    }

}
