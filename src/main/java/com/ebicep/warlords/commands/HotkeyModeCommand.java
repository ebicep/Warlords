package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.Settings;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HotkeyModeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);
        if (player != null) {
            PlayerSettings settings = Warlords.getPlayerSettings(player.getUniqueId());
            if (settings.getHotKeyMode()) {
                sender.sendMessage(ChatColor.GREEN + "Hotkey Mode " + ChatColor.AQUA + "Classic " + ChatColor.GREEN + "enabled.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Hotkey Mode " + ChatColor.YELLOW + "NEW " + ChatColor.GREEN + "enabled.");
            }
            settings.setHotKeyMode(!settings.getHotKeyMode());
            DatabaseManager.updatePlayerInformation(player, "hotkeymode", settings.getHotKeyMode() ? Settings.HotkeyMode.NEW_MODE.name() : Settings.HotkeyMode.CLASSIC_MODE.name());
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("hotkeymode").setExecutor(this);
    }
}
