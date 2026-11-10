package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.pve.Spendable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DarkDescentChestTest {

    @Test
    void rewardsLandOnEvenFloorsWithTheSameTables() {
        assertFalse(DarkDescentChest.isRewardFloor(1));
        assertFalse(DarkDescentChest.isRewardFloor(19));
        assertTrue(DarkDescentChest.isRewardFloor(2));
        assertTrue(DarkDescentChest.isRewardFloor(20));

        assertEquals(DarkDescentChest.LOWER, DarkDescentChest.forFloor(8));
        assertEquals(DarkDescentChest.MILESTONE, DarkDescentChest.forFloor(10));
        assertEquals(DarkDescentChest.UPPER, DarkDescentChest.forFloor(18));
        assertEquals(DarkDescentChest.MILESTONE, DarkDescentChest.forFloor(20));

        List<Pair<Spendable, Long>> rewards = DarkDescentChest.getWeeklyRewards();
        assertEquals(DarkDescentOption.FLOORS, rewards.size());
        for (int floor = 1; floor <= DarkDescentOption.FLOORS; floor++) {
            if (DarkDescentChest.isRewardFloor(floor)) {
                assertNotNull(rewards.get(floor - 1));
            } else {
                assertNull(rewards.get(floor - 1));
            }
        }
    }
}
