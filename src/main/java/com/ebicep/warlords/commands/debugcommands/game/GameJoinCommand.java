package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;

@CommandAlias("gamejoin")
@CommandPermission("group.administrator")
public class GameJoinCommand extends BaseCommand {

    @Description("Joins your current game if spectator")
    public void killGame(@Conditions("requireGame") Player player) {
        Game playerGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (GameManager.GameHolder gameHolder : Warlords.getGameManager().getGames()) {
            if (Objects.equals(gameHolder.getGame(), playerGame)) {
                Warlords.addPlayer(new WarlordsPlayer(
                        player,
                        gameHolder.getGame(),
                        Team.BLUE
                ));
                gameHolder.getGame().addPlayer(player, false);
                Warlords.getInstance().hideAndUnhidePeople();
                break;
            }
        }
    }

}
