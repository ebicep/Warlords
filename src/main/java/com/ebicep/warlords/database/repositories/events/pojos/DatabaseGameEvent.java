package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.events.EventsLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Document(collection = "Game_Events")
public class DatabaseGameEvent {

    public static final HashMap<GameEvents, DatabaseGameEvent> PREVIOUS_GAME_EVENTS = new HashMap<>();
    public static final HashMap<GameEvents, List<Long>> ALL_GAME_EVENT_TIMES = new HashMap<>();
    public static DatabaseGameEvent currentGameEvent = null;

    public static void startGameEvent() {
        long start = System.nanoTime();
        ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Scanning for game events...");
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.gameEventsService.findAll())
                .syncLast(gameEvents -> {
                    Instant now = Instant.now();
                    for (DatabaseGameEvent gameEvent : gameEvents) {
                        ALL_GAME_EVENT_TIMES.computeIfAbsent(gameEvent.getEvent(), k -> new ArrayList<>()).add(gameEvent.getStartDateSecond());
                        if (gameEvent.getStartDate().isAfter(now)) {
                            continue;
                        }
                        if (gameEvent.getEndDate().isBefore(now)) {
                            PREVIOUS_GAME_EVENTS.put(gameEvent.getEvent(), gameEvent);
                        } else if (gameEvent.getEndDate().isAfter(now)) {
                            ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Found active game event: " + gameEvent.getEvent().name + " in " + (System.nanoTime() - start) / 1000000 + "ms");
                            ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Start: " + gameEvent.getStartDate());
                            ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("End: " + gameEvent.getEndDate());
                            currentGameEvent = gameEvent;
                            if (!gameEvent.getStarted()) {
                                ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("New Event Detected, clearing player currencies...");
                                Currencies currency = gameEvent.getEvent().currency;
                                for (DatabasePlayer databasePlayer : DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.LIFETIME).values()) {
                                    Long currencyValue = databasePlayer.getPveStats().getCurrencyValue(currency);
                                    if (currencyValue > 0) {
                                        databasePlayer.getPveStats().subtractCurrency(currency, currencyValue);
                                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    }
                                }
                                gameEvent.setStarted(true);
                                Warlords.newChain()
                                        .async(() -> DatabaseManager.gameEventsService.update(gameEvent)).execute();
                            }
                            gameEvent.start();
                            break;
                        }
                    }
                    if (currentGameEvent == null && !PREVIOUS_GAME_EVENTS.isEmpty()) {
                        currentGameEvent = PREVIOUS_GAME_EVENTS
                                .values()
                                .stream()
                                .min((o1, o2) -> o2.getEndDate().compareTo(o1.getEndDate()))
                                .get();
                        currentGameEvent.start();
                    }
                    EventsLeaderboardManager.create();
                })
                .execute();
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public GameEvents getEvent() {
        return event;
    }

    public Boolean getStarted() {
        return started;
    }

    public void setStarted(Boolean started) {
        this.started = started;
    }

    public void start() {
        if (GameEvents.npc != null) {
            GameEvents.npc.destroy();
        }
        ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Creating new Game Event NPC...");
        getEvent().createNPC();
    }

    @Id
    protected String id;
    private GameEvents event;
    @Field("start_date")
    private Instant startDate;
    @Field("end_date")
    private Instant endDate;
    private boolean started;

    public long getStartDateSecond() {
        return startDate.getEpochSecond();
    }

    @Override
    public String toString() {
        return "DatabaseGameEvent{" +
                "event=" + event +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", started=" + started +
                '}';
    }
}
