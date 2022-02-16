package com.ebicep.warlords.game.option;

import com.ebicep.warlords.commands.debugcommands.RecordGamesCommand;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;

public class CountGameOption implements Option {

    @Override
    public void onGameEnding(@Nonnull Game game) {
        assert game.getState() instanceof EndState;
        EndState endState = (EndState) game.getState();
        WarlordsGameTriggerWinEvent winEvent = endState.getWinEvent();


        if (!RecordGamesCommand.recordGames || game.getAddons().contains(GameAddon.IMPOSTER_MODE) || winEvent == null || game.playersCount() < 16) {
            return;
        }

        List<WarlordsPlayer> players = PlayerFilter.playingGame(game).toList();
        if (players.isEmpty()) {
            return;
        }
        float highestDamage = players.stream().max(Comparator.comparing((WarlordsPlayer wp) -> wp.getStats().total().getDamage())).get().getStats().total().getDamage();
        float highestHealing = players.stream().max(Comparator.comparing((WarlordsPlayer wp) -> wp.getStats().total().getHealing())).get().getStats().total().getHealing();

        if (highestDamage <= 750000 && highestHealing <= 750000) {
            DatabaseGame.addGame(game, winEvent, true);
            System.out.println(ChatColor.GREEN + "[Warlords] This COMP game was added to the database and player information was changed");
        } else {
            DatabaseGame.addGame(game, winEvent, false);
            System.out.println(ChatColor.GREEN + "[Warlords] This COMP game was added to the database (INVALID DAMAGE/HEALING) but player information remained the same");
        }
    }

}
