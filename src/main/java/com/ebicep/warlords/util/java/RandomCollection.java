package com.ebicep.warlords.util.java;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private double total = 0;

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) {
            return this;
        }
        total += weight;
        map.put(total, result);
        return this;
    }

    @Nullable
    public E next() {
        double value = random.nextDouble() * total;
        Map.Entry<Double, E> entry = map.higherEntry(value);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getSize() {
        return map.size();
    }

    public NavigableMap<Double, E> getMap() {
        return map;
    }
}