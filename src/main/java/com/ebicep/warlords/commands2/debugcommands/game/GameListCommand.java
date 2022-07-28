package com.ebicep.warlords.commands2.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.option.WinAfterTimeoutOption;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.ChatColor;

import java.util.EnumSet;
import java.util.OptionalInt;

import static com.ebicep.warlords.util.warlords.Utils.toTitleHumanCase;

@CommandAlias("gamelist")
@CommandPermission("warlords.game.list")
public class GameListCommand extends BaseCommand {

    @Default
    @Description("Lists all games")
    public void listGames(CommandIssuer issuer) {
        for (GameHolder holder : Warlords.getGameManager().getGames()) {
            StringBuilder message = new StringBuilder();
            message.append(ChatColor.GRAY).append("[")
                    .append(ChatColor.AQUA).append(holder.getName())
                    .append(ChatColor.GRAY).append("|")
                    .append(ChatColor.AQUA).append(toTitleHumanCase(holder.getMap().name()));
            Game game = holder.getGame();
            if (game == null) {
                message.append(']').append(ChatColor.GOLD).append(" <inactive>");
            } else {
                if (holder.getMap().getGameModes().size() > 1) {
                    message.append(ChatColor.GRAY).append("/").append(ChatColor.AQUA).append(toTitleHumanCase(game.getGameMode()));
                }
                message.append(ChatColor.GRAY).append("] ");
                //message.append('(').append(ChatColor.GOLD).append(game.getGameId()).append(ChatColor.GRAY).append(") ");
                EnumSet<GameAddon> addons = game.getAddons();
                if (!addons.isEmpty()) {
                    message.append(ChatColor.GRAY).append('(');
                    for (GameAddon addon : addons) {
                        message.append(ChatColor.GREEN).append(addon.name());
                        message.append(ChatColor.GRAY).append(',');
                    }
                    message.setLength(message.length() - 1);
                    message.append("] ");
                }
                message.append(ChatColor.GOLD).append(game.getState().getClass().getSimpleName())
                        .append(ChatColor.GRAY).append(" [ ")
                        .append(ChatColor.GREEN).append(game.getPlayers().size())
                        .append(ChatColor.GRAY).append("/")
                        .append(ChatColor.GREEN).append(game.getMinPlayers())
                        .append(ChatColor.GRAY).append("..")
                        .append(ChatColor.GREEN).append(game.getMaxPlayers())
                        .append(ChatColor.GRAY).append("] ");
                OptionalInt timeLeft = WinAfterTimeoutOption.getTimeRemaining(game);
                String time = Utils.formatTimeLeft(timeLeft.isPresent() ? timeLeft.getAsInt() : (System.currentTimeMillis() - game.createdAt()) / 1000);
                String word = timeLeft.isPresent() ? " Left" : " Elapsed";
                message.append(time).append(word);
            }
            issuer.sendMessage(message.toString());
        }
    }

}