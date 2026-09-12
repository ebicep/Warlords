package com.ebicep.warlords.tablist;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Column-major packing of groups/subgroups into a fixed tab grid.
 * Pure function — no packets or Bukkit.
 */
public final class TabLayoutEngine {

    public static final int DEFAULT_ROWS = 20;
    public static final int DEFAULT_COLUMNS = 4;

    private TabLayoutEngine() {
    }

    @Nonnull
    public static LayoutResult pack(@Nonnull List<TabGroup> groups, @Nonnull UUID viewer) {
        return pack(groups, viewer, DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    @Nonnull
    public static LayoutResult pack(
            @Nonnull List<TabGroup> groups,
            @Nonnull UUID viewer,
            int rows,
            int totalColumns
    ) {
        Objects.requireNonNull(groups, "groups");
        Objects.requireNonNull(viewer, "viewer");
        if (rows < 1 || totalColumns < 1) {
            throw new IllegalArgumentException("rows and totalColumns must be >= 1");
        }

        List<TabGroup> ordered = new ArrayList<>(groups);
        ordered.sort(Comparator.comparingInt(TabGroup::getPriority));

        TabEntry[] slots = new TabEntry[rows * totalColumns];
        int globalColumn = 0;

        for (TabGroup group : ordered) {
            if (globalColumn >= totalColumns) {
                break;
            }
            int bandStart = globalColumn;
            int bandWidth = Math.min(group.getMaxColumns(), totalColumns - bandStart);
            if (bandWidth <= 0) {
                break;
            }

            List<TabEntry> placed = placeGroup(group, viewer, rows, bandWidth);
            int usedColumns = writeBand(slots, placed, bandStart, rows, bandWidth);

            // Reserve at least minColumns (clamped to band), or used columns if larger
            int reserved = Math.max(usedColumns, Math.min(group.getMinColumns(), bandWidth));
            globalColumn = bandStart + reserved;
        }

        List<PlacedEntry> placedEntries = new ArrayList<>();
        for (int col = 0; col < totalColumns; col++) {
            for (int row = 0; row < rows; row++) {
                int index = col * rows + row;
                TabEntry entry = slots[index];
                if (entry != null) {
                    placedEntries.add(new PlacedEntry(index, entry));
                }
            }
        }
        return new LayoutResult(placedEntries, rows, totalColumns);
    }

    /**
     * Resolve and pack one group's subgroups into a local band of {@code bandWidth} columns.
     * Higher-priority subgroups fill first; lower-priority ones continue in remaining cells
     * of a partial column. Clipped by band capacity and {@link TabGroup#getMaxEntries()}.
     */
    @Nonnull
    private static List<TabEntry> placeGroup(TabGroup group, UUID viewer, int rows, int bandWidth) {
        int capacity = Math.min(bandWidth * rows, group.getMaxEntries());
        List<TabEntry> band = new ArrayList<>(capacity);

        for (TabSubgroup subgroup : group.getSubgroupsByPriority()) {
            if (band.size() >= capacity) {
                break;
            }
            for (TabEntrySource source : subgroup.getSources()) {
                if (band.size() >= capacity) {
                    break;
                }
                TabEntry entry = source.resolve(viewer);
                if (entry == null) {
                    continue;
                }
                band.add(entry);
            }
        }
        return band;
    }

    /**
     * Writes band-local entries into absolute slots (column-major). Returns columns used.
     */
    private static int writeBand(TabEntry[] slots, List<TabEntry> band, int bandStart, int rows, int bandWidth) {
        if (band.isEmpty()) {
            return 0;
        }
        for (int i = 0; i < band.size(); i++) {
            int localCol = i / rows;
            int localRow = i % rows;
            if (localCol >= bandWidth) {
                break;
            }
            int absoluteCol = bandStart + localCol;
            slots[absoluteCol * rows + localRow] = band.get(i);
        }
        return (band.size() + rows - 1) / rows;
    }

    public record PlacedEntry(int slotIndex, @Nonnull TabEntry entry) {
    }

    public record LayoutResult(
            @Nonnull List<PlacedEntry> entries,
            int rows,
            int columns
    ) {
        public int size() {
            return entries.size();
        }
    }
}
