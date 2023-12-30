package com.ebicep.warlords.database.repositories.games.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseGamePlayerTDM extends DatabaseGamePlayerBase {

    @Field("seconds_in_combat")
    private int secondsInCombat;
    @Field("seconds_in_respawn")
    private int secondsInRespawn;

    public DatabaseGamePlayerTDM() {
    }

    public DatabaseGamePlayerTDM(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent) {
        super(warlordsPlayer, gameWinEvent, counted);
        this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
        this.secondsInRespawn = warlordsPlayer.getMinuteStats().total().getRespawnTimeSpent() / 20;
    }


    public int getSecondsInCombat() {
        return secondsInCombat;
    }

    public void setSecondsInCombat(int secondsInCombat) {
        this.secondsInCombat = secondsInCombat;
    }

    public int getSecondsInRespawn() {
        return secondsInRespawn;
    }

    public void setSecondsInRespawn(int secondsInRespawn) {
        this.secondsInRespawn = secondsInRespawn;
    }

}
