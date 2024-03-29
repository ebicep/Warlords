package com.ebicep.warlords.database.repositories.games.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseGamePlayerDuel extends DatabaseGamePlayerBase {

    @Field("seconds_in_combat")
    private int secondsInCombat;

    public DatabaseGamePlayerDuel() {
    }

    public DatabaseGamePlayerDuel(WarlordsPlayer warlordsPlayer) {
        super(warlordsPlayer);
        this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
    }

}
