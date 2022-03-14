package com.ebicep.warlords.player;

import com.ebicep.warlords.events.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.state.PlayingState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerStatisticsSecond implements Iterable<PlayerStatisticsSecond.Entry> {

    private final PlayingState playingState;
    private final List<Entry> entries = new ArrayList<>();
    private Entry current = new Entry();

    public PlayerStatisticsSecond(PlayingState playingState) {
        this.playingState = playingState;
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

    public List<WarlordsDamageHealingFinalEvent> getAllEventsAsSelf() {
        List<WarlordsDamageHealingFinalEvent> events = new ArrayList<>();
        entries.forEach(entry -> events.addAll(entry.getEventsAsSelf()));
        return events;
    }

    public List<WarlordsDamageHealingFinalEvent> getAllEventsAsAttacker() {
        List<WarlordsDamageHealingFinalEvent> events = new ArrayList<>();
        entries.forEach(entry -> events.addAll(entry.getEventsAsAttacker()));
        return events;
    }

    public List<WarlordsDamageHealingFinalEvent> getEventsAsSelfFromLastSecond(int seconds) {
        int timeLimit = playingState.getTicksElapsed() - (seconds * 20);
        return getAllEventsAsSelf().stream()
                .filter(event -> timeLimit <= event.getInGameTick())
                .collect(Collectors.toList());
    }

    public List<WarlordsDamageHealingFinalEvent> getEventsAsAttackerFromLastSecond(int seconds) {
        int timeLimit = playingState.getTicksElapsed() - (seconds * 20);
        return getAllEventsAsAttacker().stream()
                .filter(event -> timeLimit <= event.getInGameTick())
                .collect(Collectors.toList());
    }

    public List<WarlordsDamageHealingFinalEvent> getLastEventsAsSelf(int amount, int timeLimitSeconds) {
        List<WarlordsDamageHealingFinalEvent> events = getEventsAsSelfFromLastSecond(timeLimitSeconds);
        return events.subList(Math.max(0, events.size() - amount), events.size());
    }

    public List<WarlordsDamageHealingFinalEvent> getLastEventsAsSelf(int amount) {
        return getLastEventsAsSelf(amount, 60); //max last 60 seconds
    }

    public List<WarlordsDamageHealingFinalEvent> getLastEventsAsAttacker(int amount, int timeLimitSeconds) {
        List<WarlordsDamageHealingFinalEvent> events = getEventsAsAttackerFromLastSecond(timeLimitSeconds);
        return events.subList(Math.max(0, events.size() - amount), events.size());
    }

    public List<WarlordsDamageHealingFinalEvent> getLastEventsAsAttacker(int amount) {
        return getLastEventsAsAttacker(amount, 60); //max last 60 seconds
    }

    public WarlordsDamageHealingFinalEvent getLastEventAsSelf() {
        return getLastEventsAsSelf(1, 1).get(0);
    }

    public WarlordsDamageHealingFinalEvent getLastEventAsAttacker() {
        return getLastEventsAsAttacker(1, 1).get(0);
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
