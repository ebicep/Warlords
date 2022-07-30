package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("hotkeymode")
public class HotkeyModeCommand extends BaseCommand {

    @Default
    @Description("Toggles hotkey mode")
    public void hotkeyMode(Player player) {
        PlayerSettings settings = Warlords.getPlayerSettings(player.getUniqueId());
        settings.setHotKeyMode(!settings.getHotKeyMode());
        if (settings.getHotKeyMode()) {
            player.sendMessage(ChatColor.GREEN + "Hotkey Mode " + ChatColor.YELLOW + "NEW " + ChatColor.GREEN + "enabled.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Hotkey Mode " + ChatColor.AQUA + "Classic " + ChatColor.GREEN + "enabled.");
        }
        if (DatabaseManager.playerService != null) {
            DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
            databasePlayer.setHotkeyMode(settings.getHotKeyMode() ? Settings.HotkeyMode.NEW_MODE : Settings.HotkeyMode.CLASSIC_MODE);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
        }
    }

}
