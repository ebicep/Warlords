package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("flagmessagemode")
public class FlagMessageModeCommand extends BaseCommand {

    @Default
    @Description("Toggles flag message mode")
    public void flagMessage(Player player) {
        PlayerSettings settings = PlayerSettings.getPlayerSettings(player.getUniqueId());
        settings.setFlagMessageMode(settings.getFlagMessageMode() == Settings.FlagMessageMode.ABSOLUTE ? Settings.FlagMessageMode.RELATIVE : Settings.FlagMessageMode.ABSOLUTE);
        if (settings.getFlagMessageMode() == Settings.FlagMessageMode.ABSOLUTE) {
            player.sendMessage(ChatColor.GREEN + "Flag Message Mode " + ChatColor.YELLOW + "ABSOLUTE " + ChatColor.GREEN + "enabled.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Flag Message Mode " + ChatColor.AQUA + "RELATIVE " + ChatColor.GREEN + "enabled.");
        }
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.setFlagMessageMode(settings.getFlagMessageMode());
        });
    }
}
