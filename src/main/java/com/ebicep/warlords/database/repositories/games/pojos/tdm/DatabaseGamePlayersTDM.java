package com.ebicep.warlords.database.repositories.games.pojos.tdm;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DatabaseGamePlayersTDM {

    protected List<DatabaseGamePlayerTDM> blue = new ArrayList<>();
    protected List<DatabaseGamePlayerTDM> red = new ArrayList<>();

    public DatabaseGamePlayersTDM() {
    }

    public DatabaseGamePlayersTDM(@Nonnull Game game) {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            if (warlordsPlayer.getTeam() == Team.BLUE) {
                blue.add(new DatabaseGamePlayerTDM(warlordsPlayer));
            } else if (warlordsPlayer.getTeam() == Team.RED) {
                red.add(new DatabaseGamePlayerTDM(warlordsPlayer));
            }
        });
    }

    public List<DatabaseGamePlayerTDM> getBlue() {
        return blue;
    }

    public List<DatabaseGamePlayerTDM> getRed() {
        return red;
    }

    public static class DatabaseGamePlayerTDM extends DatabaseGamePlayerBase {

        @Field("seconds_in_combat")
        private int secondsInCombat;
        @Field("seconds_in_respawn")
        private int secondsInRespawn;

        public DatabaseGamePlayerTDM() {
        }

        public DatabaseGamePlayerTDM(WarlordsPlayer warlordsPlayer) {
            super(warlordsPlayer);
            this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
            this.secondsInRespawn = Math.round(warlordsPlayer.getMinuteStats().total().getRespawnTimeSpent());
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
}
