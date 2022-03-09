package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.WarlordsDamageHealingFinalEvent;
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


    public void addDamageHealingEventAsSelf(WarlordsDamageHealingFinalEvent event) {
        current.eventsAsSelf.add(event);
    }

    public void addDamageHealingEventAsAttacker(WarlordsDamageHealingFinalEvent event) {
        current.eventsAsAttacker.add(event);
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public List<WarlordsDamageHealingFinalEvent> getEventsAsSelfFromLastSecond(int seconds) {
        List<WarlordsDamageHealingFinalEvent> events = new ArrayList<>();
        for (int i = 0; i < seconds; i++) {
            if(entries.size() > i) {
                events.addAll(entries.get(entries.size() - (i + 1)).getEventsAsSelf());
            }
        }
        return events;
    }

    public List<WarlordsDamageHealingFinalEvent> getEventsAsAttackerFromLastSecond(int seconds) {
        List<WarlordsDamageHealingFinalEvent> events = new ArrayList<>();
        for (int i = 0; i < seconds; i++) {
            if(entries.size() > i) {
                events.addAll(entries.get(entries.size() - (i + 1)).getEventsAsSelf());
            }
        }
        return events;
    }

    @NotNull
    @Override
    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public static class Entry {

        List<WarlordsDamageHealingFinalEvent> eventsAsSelf = new ArrayList<>();

        List<WarlordsDamageHealingFinalEvent> eventsAsAttacker = new ArrayList<>();

        public List<WarlordsDamageHealingFinalEvent> getEventsAsSelf() {
            return eventsAsSelf;
        }

        public List<WarlordsDamageHealingFinalEvent> getEventsAsAttacker() {
            return eventsAsAttacker;
        }
    }
}
