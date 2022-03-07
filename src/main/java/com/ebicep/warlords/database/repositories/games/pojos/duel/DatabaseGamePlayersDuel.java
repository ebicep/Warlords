package com.ebicep.warlords.database.repositories.games.pojos.duel;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DatabaseGamePlayersDuel {

    protected List<DatabaseGamePlayerDuel> blue = new ArrayList<>();
    protected List<DatabaseGamePlayerDuel> red = new ArrayList<>();

    public DatabaseGamePlayersDuel() {
    }

    public DatabaseGamePlayersDuel(@Nonnull Game game) {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            if (warlordsPlayer.getTeam() == Team.BLUE) {
                blue.add(new DatabaseGamePlayerDuel(warlordsPlayer));
            } else if (warlordsPlayer.getTeam() == Team.RED) {
                red.add(new DatabaseGamePlayerDuel(warlordsPlayer));
            }
        });
    }

    public static class DatabaseGamePlayerDuel extends DatabaseGamePlayerBase {

        @Field("seconds_in_combat")
        private int secondsInCombat;

        public DatabaseGamePlayerDuel() {
        }

        public DatabaseGamePlayerDuel(WarlordsPlayer warlordsPlayer) {
            super(warlordsPlayer);
            this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
            //TODO abstract warlordsplayer per gamemode
        }

    }

}
