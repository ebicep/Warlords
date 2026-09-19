package com.ebicep.warlords.tablist;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Fixed fake identity for one tab-grid cell. Slot identities are position-stable.
 * <p>
 * Profile names are unique invisible braille characters so decorative rows do not
 * pollute client chat Tab-complete with {@code !000}-style strings. Grid order comes
 * from packet {@code listOrder} (= {@link #index()}) via {@link TabViewerSession},
 * not scoreboard teams or GameProfile name lexicographics.
 */
public record TabSlot(
        int index,
        @Nonnull UUID uuid,
        @Nonnull String profileName
) {

    public static final int POOL_SIZE = TabLayoutEngine.DEFAULT_ROWS * TabLayoutEngine.DEFAULT_COLUMNS;

    /** Braille blank / pattern block — renders empty or near-empty in chat suggestions. */
    private static final char BRAILLE_BASE = '\u2800';

    private static final List<TabSlot> POOL = createPool(POOL_SIZE);

    @Nonnull
    public static List<TabSlot> pool() {
        return POOL;
    }

    @Nonnull
    public static TabSlot get(int index) {
        return POOL.get(index);
    }

    /**
     * Unique GameProfile name for a decorative slot (length 2, chars in U+2800..U+28FF).
     */
    @Nonnull
    static String invisibleProfileName(int index) {
        if (index < 0 || index > 0xFFFF) {
            throw new IllegalArgumentException("index out of range: " + index);
        }
        char high = (char) (BRAILLE_BASE + (index / 256));
        char low = (char) (BRAILLE_BASE + (index % 256));
        return new String(new char[]{high, low});
    }

    private static List<TabSlot> createPool(int size) {
        List<TabSlot> slots = new ArrayList<>(size);
        Set<String> names = new HashSet<>(size);
        for (int i = 0; i < size; i++) {
            String name = invisibleProfileName(i);
            if (!names.add(name)) {
                throw new IllegalStateException("duplicate invisible profile name at index " + i);
            }
            if (name.length() > 16) {
                throw new IllegalStateException("profile name longer than 16 at index " + i);
            }
            UUID uuid = UUID.nameUUIDFromBytes(("warlords-tab-slot-" + i).getBytes(StandardCharsets.UTF_8));
            slots.add(new TabSlot(i, uuid, name));
        }
        return Collections.unmodifiableList(slots);
    }
}
