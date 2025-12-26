package com.ebicep.warlords.util.java;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class JavaUtils {

    private static final Random RANDOM = new Random();

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = RANDOM.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public static <T> T randomFromList(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("List must not be empty");
        }
        return list.get(generateRandomIndexFromListSize(list.size()));
    }

    public static int generateRandomIndexFromListSize(int size) {
        return ThreadLocalRandom.current().nextInt(size);
    }

    public static <T> T[] pickRandom(T[] source, int x) {
        if (x < 0 || x > source.length) {
            throw new IllegalArgumentException("x must be between 0 and source.length");
        }

        T[] copy = source.clone();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        // Partial Fisher–Yates shuffle
        for (int i = 0; i < x; i++) {
            int j = rnd.nextInt(i, copy.length);
            T tmp = copy[i];
            copy[i] = copy[j];
            copy[j] = tmp;
        }

        return Arrays.copyOf(copy, x);
    }

    public static <T> Collector<T, ?, List<T>> lastN(int n) {
        return Collector.<T, Deque<T>, List<T>>of(ArrayDeque::new, (acc, t) -> {
                    if (acc.size() == n) {
                        acc.pollFirst();
                    }
                    acc.add(t);
                }, (acc1, acc2) -> {
                    while (acc2.size() < n && !acc1.isEmpty()) {
                        acc2.addFirst(acc1.pollLast());
                    }
                    return acc2;
                }, ArrayList::new
        );
    }


    /**
     * Checks if an <code>Iterable</code> contains an item matched by the given
     * predicate.
     *
     * @param <T>      The type of the items
     * @param iterable The list of items
     * @param matcher  The matcher
     *
     * @return return true if any item matches, false otherwise. Empty iterables return false.
     */
    public static <T> boolean collectionHasItem(@Nonnull Iterable<T> iterable, @Nonnull Predicate<? super T> matcher) {
        for (T item : iterable) {
            if (matcher.test(item)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static <T> T arrayGetItem(@Nonnull T[] iterable, @Nonnull Predicate<? super T> matcher) {
        for (T item : iterable) {
            if (matcher.test(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Allows a Stream to be used in a for-each loop, as they do not come out of the box with support for this.
     *
     * @param <T>    The type
     * @param stream The stream
     *
     * @return A one-time use <code>Iterable</code> for iterating over the stream
     */
    @Nonnull
    public static <T> Iterable<T> iterable(@Nonnull Stream<T> stream) {
        return stream::iterator;
    }

    @SafeVarargs
    public static <T> List<T> newArrayListOf(T... elements) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, elements);
        return list;
    }

    public static String throwableToString(Throwable throwable, int maxLines) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);

        String[] all = sw.toString().split("\\R");
        StringBuilder sb = new StringBuilder();

        int limit = Math.min(maxLines, all.length);
        for (int i = 0; i < limit; i++) {
            sb.append(all[i]).append(System.lineSeparator());
        }

        return sb.toString();
    }

}
