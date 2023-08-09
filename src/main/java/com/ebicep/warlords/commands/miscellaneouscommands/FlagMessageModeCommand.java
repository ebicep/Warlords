package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("flagmessagemode")
public class FlagMessageModeCommand extends BaseCommand {

    @Default
    @Description("Toggles flag message mode")
    public void flagMessage(Player player) {
        PlayerSettings settings = PlayerSettings.getPlayerSettings(player.getUniqueId());
        settings.setFlagMessageMode(settings.getFlagMessageMode() == Settings.FlagMessageMode.ABSOLUTE ? Settings.FlagMessageMode.RELATIVE : Settings.FlagMessageMode.ABSOLUTE);
        if (settings.getFlagMessageMode() == Settings.FlagMessageMode.ABSOLUTE) {
            player.sendMessage(Component.text("Flag Message Mode ", NamedTextColor.GREEN)
                                        .append(Component.text("ABSOLUTE ", NamedTextColor.YELLOW))
                                        .append(Component.text("enabled."))
            );
        } else {
            player.sendMessage(Component.text("Flag Message Mode ", NamedTextColor.GREEN)
                                        .append(Component.text("RELATIVE ", NamedTextColor.AQUA))
                                        .append(Component.text("enabled.", NamedTextColor.GREEN)));
        }
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.setFlagMessageMode(settings.getFlagMessageMode());
        });
    }
}
