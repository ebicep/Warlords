package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("lobby|l|hub")
public class LobbyCommand extends BaseCommand {

    @Default
    @Description("Teleports you to the lobby")
    public void lobby(@Conditions("requireGame") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        Team playerTeam = game.getPlayerTeam(player.getUniqueId());
        if (playerTeam != null && !game.acceptsPeople()) {
            player.sendMessage(
                    Component.text("This command is only enabled in public games. Did you mean to end your private game? Use the command: ", NamedTextColor.RED)
                             .append(Component.text("/endprivategame", NamedTextColor.GOLD))
                             .append(Component.text("."))
            );
        } else {
            game.removePlayer(player.getUniqueId());
        }
    }

}
