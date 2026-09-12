package com.ebicep.warlords.tablist;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Fixed fake identity for one tab-grid cell. Slot identities are position-stable.
 */
public record TabSlot(
        int index,
        @Nonnull UUID uuid,
        @Nonnull String profileName,
        @Nonnull String teamName
) {

    public static final int POOL_SIZE = TabLayoutEngine.DEFAULT_ROWS * TabLayoutEngine.DEFAULT_COLUMNS;

    private static final List<TabSlot> POOL = createPool(POOL_SIZE);

    @Nonnull
    public static List<TabSlot> pool() {
        return POOL;
    }

    @Nonnull
    public static TabSlot get(int index) {
        return POOL.get(index);
    }

    private static List<TabSlot> createPool(int size) {
        List<TabSlot> slots = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String name = String.format("!%03d", i);
            String team = String.format("!t%03d", i);
            UUID uuid = UUID.nameUUIDFromBytes(("warlords-tab-slot-" + i).getBytes(StandardCharsets.UTF_8));
            slots.add(new TabSlot(i, uuid, name, team));
        }
        return Collections.unmodifiableList(slots);
    }
}
