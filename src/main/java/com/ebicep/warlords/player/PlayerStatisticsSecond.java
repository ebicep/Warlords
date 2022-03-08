package com.ebicep.warlords.player;

import com.ebicep.warlords.events.WarlordsDamageHealingFinalEvent;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

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


    public void addDamageHealingEvent(WarlordsDamageHealingFinalEvent event) {
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

        List<WarlordsDamageHealingFinalEvent> events = new ArrayList<>();

        public List<WarlordsDamageHealingFinalEvent> getEvents() {
            return events;
        }

    }
}
