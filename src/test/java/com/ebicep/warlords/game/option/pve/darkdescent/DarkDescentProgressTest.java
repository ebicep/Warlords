package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.util.java.DateUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class DarkDescentProgressTest {

    @Test
    void failedAttemptsLockTheWeek() {
        DarkDescentProgress progress = new DarkDescentProgress();
        for (int i = 0; i < DarkDescentProgress.MAX_ATTEMPTS; i++) {
            assertTrue(progress.canStart());
            progress.addFailedAttempt();
        }
        assertFalse(progress.canStart());
        assertEquals(0, progress.getAttemptsRemaining());
    }

    @Test
    void completingLocksTheWeek() {
        DarkDescentProgress progress = new DarkDescentProgress();
        progress.setCompleted();
        assertFalse(progress.canStart());
        assertTrue(progress.isCompleted());
    }

    @Test
    void earnedFloorsAreKeptAcrossAttempts() {
        DarkDescentProgress progress = new DarkDescentProgress();
        progress.earnFloor(3);
        progress.addFailedAttempt();
        assertTrue(progress.hasEarned(3));
        assertFalse(progress.hasEarned(4));
    }

    @Test
    void newWeekResetsProgress() throws ReflectiveOperationException {
        DarkDescentProgress progress = new DarkDescentProgress();
        progress.earnFloor(10);
        progress.addFailedAttempt();
        progress.setCompleted();

        Field week = DarkDescentProgress.class.getDeclaredField("week");
        week.setAccessible(true);
        week.setLong(progress, DateUtil.getCurrentWeekStartEpochDay() - 7);

        assertTrue(progress.canStart());
        assertFalse(progress.hasEarned(10));
        assertEquals(DarkDescentProgress.MAX_ATTEMPTS, progress.getAttemptsRemaining());
    }
}
