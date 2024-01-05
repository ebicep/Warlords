package com.ebicep.warlords.database.repositories.games.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

public class DatabaseGamePlayerInterception extends DatabaseGamePlayerBase {

    @Field("seconds_in_combat")
    private int secondsInCombat;
    @Field("seconds_in_respawn")
    private int secondsInRespawn;

    @Field("points_captured")
    private int pointsCaptured;
    @Field("points_defended")
    private int pointsDefended;

    public DatabaseGamePlayerInterception() {
    }

    public DatabaseGamePlayerInterception(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(warlordsPlayer, gameWinEvent, counted);
        this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
        this.secondsInRespawn = warlordsPlayer.getMinuteStats().total().getRespawnTimeSpent() / 20;
    }

    public int getSecondsInCombat() {
        return secondsInCombat;
    }

    public int getSecondsInRespawn() {
        return secondsInRespawn;
    }

    public int getPointsCaptured() {
        return pointsCaptured;
    }

    public int getPointsDefended() {
        return pointsDefended;
    }
}
