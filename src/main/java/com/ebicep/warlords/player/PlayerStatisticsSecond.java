package com.ebicep.warlords.player;

import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerStatisticsSecond implements Iterable<PlayerStatisticsSecond.Entry> {

    private final List<Entry> entries = new ArrayList<>();
    private Entry current = new Entry();

    public PlayerStatisticsSecond() {
        entries.add(current);
    }

    public void advanceSecond() {
        current = new Entry();
        entries.add(current);
    }

    public void setHealth(int health) {
        current.health = health;
    }

    public void addDamageHealingEvent(WarlordsDamageHealingEvent event) {
        current.events.add(event);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @NotNull
    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public static class Entry {
        @NonNegative
        private int health;

        List<WarlordsDamageHealingEvent> events;

        public int getHealth() {
            return health;
        }

        public List<WarlordsDamageHealingEvent> getEvents() {
            return events;
        }
    }
}
