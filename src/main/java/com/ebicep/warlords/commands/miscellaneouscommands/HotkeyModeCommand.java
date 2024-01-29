package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.general.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("hotkeymode")
public class HotkeyModeCommand extends BaseCommand {

    @Default
    @Description("Toggles hotkey mode")
    public void hotkeyMode(Player player) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.setHotkeyMode(databasePlayer.getHotkeyMode() == Settings.HotkeyMode.NEW_MODE ? Settings.HotkeyMode.CLASSIC_MODE : Settings.HotkeyMode.NEW_MODE);
            if (databasePlayer.getHotkeyMode() == Settings.HotkeyMode.NEW_MODE) {
                player.sendMessage(Component.text("Hotkey Mode ", NamedTextColor.GREEN)
                                            .append(Component.text("NEW ", NamedTextColor.YELLOW))
                                            .append(Component.text("enabled.")));
            } else {
                player.sendMessage(Component.text("Hotkey Mode ", NamedTextColor.GREEN)
                                            .append(Component.text("CLASSIC ", NamedTextColor.AQUA))
                                            .append(Component.text("enabled.")));
            }
        });
    }

}
