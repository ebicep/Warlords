package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.settings.FlagMessageMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("flagmessagemode")
public class FlagMessageModeCommand extends BaseCommand {

    @Default
    @Description("Toggles flag message mode")
    public void flagMessage(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        databasePlayer.setFlagMessageMode(databasePlayer.getFlagMessageMode() == FlagMessageMode.ABSOLUTE ?
                                          FlagMessageMode.RELATIVE :
                                          FlagMessageMode.ABSOLUTE);
        if (databasePlayer.getFlagMessageMode() == FlagMessageMode.ABSOLUTE) {
            player.sendMessage(Component.text("Flag Message Mode ", NamedTextColor.GREEN)
                                        .append(Component.text("ABSOLUTE ", NamedTextColor.YELLOW))
                                        .append(Component.text("enabled."))
            );
        } else {
            player.sendMessage(Component.text("Flag Message Mode ", NamedTextColor.GREEN)
                                        .append(Component.text("RELATIVE ", NamedTextColor.AQUA))
                                        .append(Component.text("enabled.", NamedTextColor.GREEN)));
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

}
