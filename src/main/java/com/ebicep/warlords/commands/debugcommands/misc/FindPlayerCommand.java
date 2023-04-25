package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("findplayer")
@CommandPermission("group.administrator")
public class FindPlayerCommand extends BaseCommand {

    @Default
    @CommandCompletion("@gameplayers")
    @Description("Finds a player by name")
    public void findPlayer(CommandIssuer issuer, @Conditions("requireGame") @Values("@gameplayers") @Flags("other") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        boolean isSpectator = game.spectators().toList().contains(player.getUniqueId());
        ChatChannels.sendDebugMessage(issuer,
                Component.text("Found player ", NamedTextColor.GREEN)
                         .append(Component.text(player.getName(), NamedTextColor.RED))
                         .append(Component.text(isSpectator ? " (Spectating)" : " (Playing)" + " in game ", NamedTextColor.GREEN))
                         .append(Component.text(game.getGameId().toString(), NamedTextColor.RED))
        );

    }

}
