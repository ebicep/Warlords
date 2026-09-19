package com.ebicep.warlords.tablist;

import com.ebicep.warlords.util.bukkit.packets.tablist.TabListEntry;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TabSlotTest {

    @Test
    void poolNamesAreUniqueInvisibleAndLengthSafe() {
        Set<String> names = new HashSet<>();
        for (int i = 0; i < TabSlot.POOL_SIZE; i++) {
            TabSlot slot = TabSlot.get(i);
            String name = slot.profileName();
            assertTrue(names.add(name), "duplicate profile name at index " + i);
            assertTrue(name.length() <= 16, "name too long at index " + i);
            assertFalse(name.startsWith("!"), "legacy !000-style name at index " + i);
            assertEquals(TabSlot.invisibleProfileName(i), name);
            assertEquals(i, slot.index());
        }
        assertEquals(TabSlot.POOL_SIZE, names.size());
    }

    @Test
    void invisibleProfileNamesDifferAcrossPool() {
        assertFalse(TabSlot.invisibleProfileName(0).equals(TabSlot.invisibleProfileName(1)));
        assertFalse(TabSlot.invisibleProfileName(0).equals(TabSlot.invisibleProfileName(79)));
    }

    @Test
    void toPacketEntryUsesSlotIndexAsListOrderAndDecorativeProfileName() {
        for (int i : new int[]{0, 1, 19, 79}) {
            TabSlot slot = TabSlot.get(i);
            TabEntry entry = TabEntry.of(Component.text("row-" + i), 42);
            String profileName = TabViewerSession.resolveProfileName(slot, entry);
            TabListEntry packet = TabViewerSession.toPacketEntry(slot, entry, profileName);

            assertEquals(slot.profileName(), profileName);
            assertEquals(i, packet.listOrder());
            assertEquals(slot.uuid(), packet.uuid());
            assertEquals(slot.profileName(), packet.profileName());
            assertEquals(42, packet.latency());
        }
    }
}
