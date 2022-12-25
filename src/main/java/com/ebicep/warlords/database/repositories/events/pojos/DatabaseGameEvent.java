package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "Game_Events")
public class DatabaseGameEvent {

    public static DatabaseGameEvent currentGameEvent = null;

    public static void startGameEvent() {
        long start = System.nanoTime();
        ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Scanning for game events...");
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.gameEventsService.findAll())
                .syncLast(gameEvents -> {
                    Instant now = Instant.now();
                    for (DatabaseGameEvent gameEvent : gameEvents) {
                        if (gameEvent.getStartDate().isBefore(now) && gameEvent.getEndDate().isAfter(now)) {
                            ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Found active game event: " + gameEvent.getEvent().name + " in " + (System.nanoTime() - start) / 1000000 + "ms");
                            ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Start: " + gameEvent.getStartDate());
                            ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("End: " + gameEvent.getEndDate());
                            currentGameEvent = gameEvent;
                            gameEvent.start();
                            break;
                        }
                    }
                })
                .execute();
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void start() {
        if (GameEvents.npc != null) {
            GameEvents.npc.destroy();
        }
        ChatUtils.MessageTypes.GAME_EVENTS.sendMessage("Creating new Game Event NPC...");
        getEvent().createNPC();
    }

    public GameEvents getEvent() {
        return event;
    }

    protected GameEvents event;
    @Field("start_date")
    protected Instant startDate;
    @Field("end_date")
    protected Instant endDate;
}
