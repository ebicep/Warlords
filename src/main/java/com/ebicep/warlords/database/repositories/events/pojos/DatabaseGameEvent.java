package com.ebicep.warlords.database.repositories.events.pojos;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "Game_Events")
public class DatabaseGameEvent {

    public static DatabaseGameEvent currentGameEvent = null;

    public static void startGameEvent() {
        Warlords.newChain()
                .asyncFirst(() -> DatabaseManager.gameEventsService.findAll())
                .syncLast(gameEvents -> {
                    Instant now = Instant.now();
                    for (DatabaseGameEvent gameEvent : gameEvents) {
                        if (gameEvent.getStarDate().isBefore(now) && gameEvent.getEndDate().isAfter(now)) {
                            currentGameEvent = gameEvent;
                            gameEvent.start();
                            break;
                        }
                    }
                })
                .execute();
    }

    public Instant getStarDate() {
        return starDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void start() {
        if (GameEvents.npc != null) {
            GameEvents.npc.destroy();
        }
        getEvent().createNPC();
    }

    public GameEvents getEvent() {
        return event;
    }

    protected GameEvents event;
    @Field("start_date")
    protected Instant starDate;
    @Field("end_date")
    protected Instant endDate;
}
