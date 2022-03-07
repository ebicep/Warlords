package com.ebicep.warlords.database.repositories.games.pojos.interception;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class DatabaseGamePlayersInterception {

    protected List<DatabaseGamePlayerInterception> blue = new ArrayList<>();
    protected List<DatabaseGamePlayerInterception> red = new ArrayList<>();

    public DatabaseGamePlayersInterception() {
    }

    public DatabaseGamePlayersInterception(@Nonnull Game game) {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            if (warlordsPlayer.getTeam() == Team.BLUE) {
                blue.add(new DatabaseGamePlayerInterception(warlordsPlayer));
            } else if (warlordsPlayer.getTeam() == Team.RED) {
                red.add(new DatabaseGamePlayerInterception(warlordsPlayer));
            }
        });
    }

    public List<DatabaseGamePlayerInterception> getBlue() {
        return blue;
    }

    public List<DatabaseGamePlayerInterception> getRed() {
        return red;
    }

    public static class DatabaseGamePlayerInterception extends DatabaseGamePlayerBase {

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

        public DatabaseGamePlayerInterception(WarlordsPlayer warlordsPlayer) {
            super(warlordsPlayer);
            this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
            this.secondsInRespawn = Math.round(warlordsPlayer.getMinuteStats().total().getRespawnTimeSpent());
            //TODO abstract warlordsplayer per gamemode
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
}
