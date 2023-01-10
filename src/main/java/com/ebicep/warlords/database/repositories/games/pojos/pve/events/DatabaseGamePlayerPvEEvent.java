package com.ebicep.warlords.database.repositories.games.pojos.pve.events;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvE;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

public abstract class DatabaseGamePlayerPvEEvent extends DatabaseGamePlayerPvE {

    @Field("points")
    private long points;

    public DatabaseGamePlayerPvEEvent() {
    }

    public DatabaseGamePlayerPvEEvent(
            WarlordsPlayer warlordsPlayer,
            WaveDefenseOption waveDefenseOption,
            EventPointsOption eventPointsOption
    ) {
        super(warlordsPlayer, waveDefenseOption);
        this.points = eventPointsOption.getPoints().getOrDefault(warlordsPlayer.getUuid(), 0);
    }

    public long getPoints() {
        return points;
    }
}