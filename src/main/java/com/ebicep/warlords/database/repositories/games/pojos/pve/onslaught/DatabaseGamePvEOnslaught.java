package com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught;

import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "Games_Information_Onslaught")
public class DatabaseGamePvEOnslaught extends DatabaseGamePvEBase {

    protected List<DatabaseGamePlayerPvEOnslaught> players = new ArrayList<>();

    public DatabaseGamePvEOnslaught() {

    }

    public DatabaseGamePvEOnslaught(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        //this.difficulty =
        super(game, gameWinEvent, counted);
        for (Option option : game.getOptions()) {
            if (option instanceof OnslaughtOption pveonslaughtOption) {
                game.warlordsPlayers().forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvEOnslaught(warlordsPlayer, gameWinEvent, pveonslaughtOption, counted)));
            }
        }
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        players.forEach(databaseGamePlayerPvE -> {
            DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame,
                    databaseGamePlayerPvE,
                    multiplier
            );
            GamesCommand.PLAYER_NAMES.add(databaseGamePlayerPvE.getName());
        });
    }

    @Override
    public Set<DatabaseGamePlayerPvEBase> getBasePlayers() {
        return new HashSet<>(players);
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return DatabaseGamePlayerResult.LOST;
    }

}
