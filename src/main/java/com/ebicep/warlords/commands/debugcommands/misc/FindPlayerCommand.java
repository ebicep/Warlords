package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandAlias("findplayer")
public class FindPlayerCommand extends BaseCommand {

    @Default
    @CommandCompletion("@gameplayers")
    @Description("Finds a player by name")
    public void findPlayer(CommandIssuer issuer, @Conditions("requireGame") @Values("@gameplayers") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        boolean isSpectator = game.spectators().collect(Collectors.toList()).contains(player.getUniqueId());
        ChatCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Found player " + ChatColor.RED + player.getName() + (isSpectator ? ChatColor.GREEN + " (Spectating)" : " (Playing)") + ChatColor.GREEN + " in game " + ChatColor.RED + game.getGameId(), true);

    }

}
