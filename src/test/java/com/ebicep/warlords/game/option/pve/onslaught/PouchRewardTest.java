package com.ebicep.warlords.game.option.pve.onslaught;

import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PouchRewardTest {

    private static final Set<SpendableRandomNewItem> ASCENDANT_ITEMS = EnumSet.of(
            SpendableRandomNewItem.EPIC,
            SpendableRandomNewItem.SOVEREIGN,
            SpendableRandomNewItem.LEGENDARY
    );

    @Test
    void eachAscendantPouchAddsItsOwnSpendableItem() {
        LinkedHashMap<Spendable, Long> rewards = new LinkedHashMap<>();
        rewards.put(Currencies.ASCENDANT_SHARD, 1L);
        rewards.merge(Currencies.ASCENDANT_SHARD, 1L, Long::sum);

        SpendableRandomNewItem first = PouchReward.addRolledAscendantItem(rewards);
        SpendableRandomNewItem second = PouchReward.addRolledAscendantItem(rewards);

        assertTrue(ASCENDANT_ITEMS.contains(first));
        assertTrue(ASCENDANT_ITEMS.contains(second));
        assertEquals(2L, itemCount(rewards));
        assertEquals(2L, rewards.get(Currencies.ASCENDANT_SHARD));

        PouchReward pouchReward = new PouchReward(rewards, PouchReward.PouchType.ASCENDANT);
        assertEquals(2L, itemCount(pouchReward.getRewards()));
        assertEquals(2L, pouchReward.getRewards().get(Currencies.ASCENDANT_SHARD));
    }

    @Test
    void matchingAscendantItemsStack() {
        Map<Spendable, Long> rewards = new LinkedHashMap<>();
        int rolls = 0;
        while (rewards.getOrDefault(SpendableRandomNewItem.EPIC, 0L) < 2 && rolls < 1000) {
            PouchReward.addRolledAscendantItem(rewards);
            rolls++;
        }
        assertTrue(rewards.getOrDefault(SpendableRandomNewItem.EPIC, 0L) >= 2);
        assertEquals(rolls, itemCount(rewards));
    }

    @Test
    void ascendantItemOddsMatchThePreviousRoll() {
        Map<SpendableRandomNewItem, Integer> counts = new LinkedHashMap<>();
        int rolls = 20_000;
        for (int i = 0; i < rolls; i++) {
            Map<Spendable, Long> rewards = new LinkedHashMap<>();
            SpendableRandomNewItem item = PouchReward.addRolledAscendantItem(rewards);
            assertTrue(ASCENDANT_ITEMS.contains(item));
            counts.merge(item, 1, Integer::sum);
        }
        assertWithin(counts.getOrDefault(SpendableRandomNewItem.EPIC, 0), rolls, 0.80, 0.03);
        assertWithin(counts.getOrDefault(SpendableRandomNewItem.SOVEREIGN, 0), rolls, 0.10, 0.02);
        assertWithin(counts.getOrDefault(SpendableRandomNewItem.LEGENDARY, 0), rolls, 0.10, 0.02);
    }

    private static long itemCount(Map<Spendable, Long> rewards) {
        return rewards.entrySet()
                      .stream()
                      .filter(entry -> entry.getKey() instanceof SpendableRandomNewItem)
                      .mapToLong(Map.Entry::getValue)
                      .sum();
    }

    private static void assertWithin(int count, int rolls, double expectedRate, double tolerance) {
        double rate = count / (double) rolls;
        assertTrue(Math.abs(rate - expectedRate) <= tolerance,
                () -> "rate " + rate + " was outside " + expectedRate + " ± " + tolerance);
    }

}
