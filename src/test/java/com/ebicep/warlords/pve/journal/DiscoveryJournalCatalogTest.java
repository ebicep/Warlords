package com.ebicep.warlords.pve.journal;

import com.ebicep.warlords.guilds.GuildSpendable;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.ExpSpendable;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.consumables.vials.Vial;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;
import com.ebicep.warlords.pve.items.types.fixeditems.FixedItems;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;
import com.ebicep.warlords.pve.newitems.gems.Gem;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscoveryJournalCatalogTest {

    @Test
    void everyJournalMobHasMechanicsEntry() {
        for (Mob.MobGroup group : MobDiscovery.JOURNAL_GROUPS) {
            for (Mob mob : group.mobs) {
                assertTrue(MobDiscovery.isCatalogued(mob), () -> "Missing journal mechanics for " + mob.name());
            }
        }
    }

    @Test
    void bossesIncludeAnomalyAndRaidBosses() {
        Set<Mob> bosses = new HashSet<>(Arrays.asList(Mob.BOSSES));
        assertTrue(bosses.contains(Mob.CHRONARCH));
        assertTrue(bosses.contains(Mob.CHESSKING));
        assertTrue(bosses.contains(Mob.PHYSIRA));
        assertTrue(bosses.contains(Mob.ENAVURIS));
        assertTrue(bosses.contains(Mob.RAID_MITHRA));
    }

    @Test
    void journalGroupsExcludeAll() {
        assertFalse(MobDiscovery.JOURNAL_GROUPS.contains(Mob.MobGroup.ALL));
    }

    @Test
    void everySpendableHasAResourceEntryWithSources() {
        Set<Spendable> catalogued = ResourceDiscovery.entries()
                                                     .stream()
                                                     .map(ResourceDiscovery.Entry::spendable)
                                                     .collect(Collectors.toSet());

        assertCovers(catalogued, Currencies.VALUES);
        assertCovers(catalogued, MobDrop.VALUES);
        assertCovers(catalogued, Gem.VALUES);
        assertCovers(catalogued, Vial.VALUES);
        assertCovers(catalogued, GuildSpendable.VALUES);
        assertCovers(catalogued, ExpSpendable.VALUES);
        assertCovers(catalogued, FixedItems.values());
        assertCovers(catalogued, SpendableRandomItem.VALUES);
        assertCovers(catalogued, SpendableRandomNewItem.VALUES);

        for (ResourceDiscovery.Entry entry : ResourceDiscovery.entries()) {
            assertFalse(entry.sources().isEmpty(), () -> "No sources for " + entry.spendable().getName());
        }
    }

    @Test
    void resourceCategoriesMatchEntryCount() {
        int counted = 0;
        for (ResourceDiscovery.Category category : ResourceDiscovery.Category.VALUES) {
            counted += ResourceDiscovery.entries(category).size();
        }
        assertEquals(ResourceDiscovery.entries().size(), counted);
        assertEquals(expectedSpendableCount(), ResourceDiscovery.entries().size());
    }

    private static int expectedSpendableCount() {
        return Currencies.VALUES.length
                + MobDrop.VALUES.length
                + Gem.VALUES.length
                + Vial.VALUES.length
                + GuildSpendable.VALUES.length
                + ExpSpendable.VALUES.length
                + FixedItems.values().length
                + SpendableRandomItem.VALUES.length
                + SpendableRandomNewItem.VALUES.length;
    }

    private static void assertCovers(Set<Spendable> catalogued, Spendable[] spendables) {
        for (Spendable spendable : spendables) {
            assertTrue(catalogued.contains(spendable), () -> "Missing resource journal entry for " + spendable.getName());
        }
    }
}
