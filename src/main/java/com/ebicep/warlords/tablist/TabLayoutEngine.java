package com.ebicep.warlords.tablist;

import com.ebicep.warlords.util.bukkit.Colors;
import com.ebicep.warlords.util.bukkit.packets.tablist.TabListSkins;
import net.kyori.adventure.text.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Column-major packing of groups/subgroups into a fixed tab grid.
 * Pure function — no packets or Bukkit.
 * <p>
 * Groups with {@code minColumns > 0} are padded with blank entries so the client
 * receives enough listed rows for those columns to appear (entry count drives layout).
 */
public final class TabLayoutEngine {

    public static final int DEFAULT_ROWS = 20;
    public static final int DEFAULT_COLUMNS = 4;

    /** Empty display pad used to fill reserved min-columns. */
    public static final TabEntry BLANK = blankPadEntry();

    private static TabEntry blankPadEntry() {
        String[] skin = TabListSkins.textureAndSignature(Colors.DARK_GRAY);
        return TabEntry.of(Component.empty()).withSkin(skin[0], skin[1]);
    }

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
            int contentColumns = placed.isEmpty() ? 0 : (placed.size() + rows - 1) / rows;
            int minReserved = Math.min(group.getMinColumns(), bandWidth);
            int reserved = Math.max(contentColumns, minReserved);
            // Pad only to satisfy minColumns so the client lists enough entries for those columns
            int targetSize = Math.max(placed.size(), minReserved * rows);
            targetSize = Math.min(targetSize, bandWidth * rows);
            while (placed.size() < targetSize) {
                placed.add(BLANK);
            }
            writeBand(slots, placed, bandStart, rows, bandWidth);
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
        List<TabEntry> band = new ArrayList<>(Math.max(capacity, 0));

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
     * Writes band-local entries into absolute slots (column-major).
     */
    private static void writeBand(TabEntry[] slots, List<TabEntry> band, int bandStart, int rows, int bandWidth) {
        for (int i = 0; i < band.size(); i++) {
            int localCol = i / rows;
            int localRow = i % rows;
            if (localCol >= bandWidth) {
                break;
            }
            int absoluteCol = bandStart + localCol;
            slots[absoluteCol * rows + localRow] = band.get(i);
        }
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
