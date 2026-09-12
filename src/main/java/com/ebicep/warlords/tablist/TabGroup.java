package com.ebicep.warlords.tablist;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Named column band with required entry/column budgets and ordered subgroups.
 */
public class TabGroup {

    private final String name;
    private final int priority;
    private final int minColumns;
    private final int maxColumns;
    private final int maxEntries;
    private final List<TabSubgroup> subgroups = new ArrayList<>();

    public TabGroup(
            @Nonnull String name,
            int priority,
            int minColumns,
            int maxColumns,
            int maxEntries
    ) {
        if (minColumns < 0 || maxColumns < 1 || minColumns > maxColumns) {
            throw new IllegalArgumentException("invalid column bounds: min=" + minColumns + " max=" + maxColumns);
        }
        if (maxEntries < 0) {
            throw new IllegalArgumentException("maxEntries must be >= 0");
        }
        this.name = Objects.requireNonNull(name, "name");
        this.priority = priority;
        this.minColumns = minColumns;
        this.maxColumns = maxColumns;
        this.maxEntries = maxEntries;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public int getMinColumns() {
        return minColumns;
    }

    public int getMaxColumns() {
        return maxColumns;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    @Nonnull
    public List<TabSubgroup> getSubgroups() {
        return Collections.unmodifiableList(subgroups);
    }

    @Nonnull
    public List<TabSubgroup> getSubgroupsByPriority() {
        List<TabSubgroup> sorted = new ArrayList<>(subgroups);
        sorted.sort(Comparator.comparingInt(TabSubgroup::getPriority));
        return sorted;
    }

    public TabGroup addSubgroup(@Nonnull TabSubgroup subgroup) {
        subgroups.add(Objects.requireNonNull(subgroup, "subgroup"));
        subgroup.markDirty();
        return this;
    }

    public boolean removeSubgroup(@Nonnull TabSubgroup subgroup) {
        boolean removed = subgroups.remove(subgroup);
        if (removed) {
            subgroup.markDirty();
        }
        return removed;
    }
}
