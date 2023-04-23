package com.ebicep.warlords.commands.debugcommands.game;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Objects;


@CommandAlias("gameinfo")
@CommandPermission("group.administrator")
public class GameInfoCommand extends BaseCommand {


    @Default
    @Description("Prints current game info")
    public void gameInfo(@Conditions("requireGame") Player player) {
        Game playerGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (GameManager.GameHolder game : Warlords.getGameManager().getGames()) {
            if (Objects.equals(game.getGame(), playerGame)) {
                ChatChannels.sendDebugMessage(player, Component.text("Game info: " + game.getGame().toString(), NamedTextColor.GREEN));
                break;
            }
        }
    }

    @Subcommand("allinfo")
    @Description("Prints all game info")
    public void allGameInfo(CommandIssuer issuer) {
        for (GameManager.GameHolder game : Warlords.getGameManager().getGames()) {
            ChatChannels.sendDebugMessage(issuer, game.getMap() + " - " + game.getGame());
        }
    }

}
