package com.ebicep.warlords.database.repositories.games.pojos.pve.events;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksPostGame;
import org.springframework.data.mongodb.core.mapping.Field;

public abstract class DatabaseGamePlayerPvEEvent extends DatabaseGamePlayerPvEWaveDefense {

    @Field("points")
    private long points;

    public DatabaseGamePlayerPvEEvent() {
    }

    public DatabaseGamePlayerPvEEvent(
            WarlordsPlayer warlordsPlayer,
            WarlordsGameTriggerWinEvent gameWinEvent,
            WaveDefenseOption waveDefenseOption,
            EventPointsOption eventPointsOption,
            boolean counted
    ) {
        super(warlordsPlayer, gameWinEvent, waveDefenseOption, counted);
        this.points = eventPointsOption.getPoints().getOrDefault(warlordsPlayer.getUuid(), 0);
        if (DatabaseGameEvent.eventIsActive()) {
            DatabaseManager.getPlayer(warlordsPlayer.getUuid(), databasePlayer -> {
                DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
                EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction
                        .apply(databasePlayer.getPveStats().getEventStats())
                        .get(currentGameEvent.getStartDateSecond());
                if (eventMode == null) {
                    return;
                }
                eventMode.getTrackableBounties()
                         .forEach(bounty -> {
                             if (bounty instanceof TracksPostGame tracksPostGame) {
                                 tracksPostGame.onGameEnd(waveDefenseOption.getGame(), warlordsPlayer, gameWinEvent);
                             } else if (bounty instanceof TracksDuringGame tracksDuringGame) {
                                 tracksDuringGame.apply(bounty);
                             }
                         });
            });
        }
    }

    public long getPoints() {
        return points;
    }
}
