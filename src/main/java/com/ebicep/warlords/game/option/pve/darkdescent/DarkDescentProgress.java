package com.ebicep.warlords.game.option.pve.darkdescent;

import com.ebicep.warlords.util.java.DateUtil;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

public class DarkDescentProgress {

    public static final int MAX_ATTEMPTS = 3;

    @Field("week")
    private long week;
    @Field("attempts_used")
    private int attemptsUsed;
    @Field("floors_earned")
    private Set<Integer> floorsEarned = new HashSet<>();
    @Field("completed")
    private boolean completed;

    public boolean canStart() {
        refresh();
        return !completed && attemptsUsed < MAX_ATTEMPTS;
    }

    public int getAttemptsRemaining() {
        refresh();
        return MAX_ATTEMPTS - attemptsUsed;
    }

    public boolean isCompleted() {
        refresh();
        return completed;
    }

    public boolean hasEarned(int floor) {
        refresh();
        return floorsEarned.contains(floor);
    }

    public void earnFloor(int floor) {
        refresh();
        floorsEarned.add(floor);
    }

    public void addFailedAttempt() {
        refresh();
        attemptsUsed++;
    }

    public void setCompleted() {
        refresh();
        completed = true;
    }

    private void refresh() {
        long currentWeek = DateUtil.getCurrentWeekStartEpochDay();
        if (week == currentWeek) {
            return;
        }
        week = currentWeek;
        attemptsUsed = 0;
        floorsEarned = new HashSet<>();
        completed = false;
    }
}
