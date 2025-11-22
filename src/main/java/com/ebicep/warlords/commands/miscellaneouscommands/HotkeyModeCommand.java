package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.settings.HotkeyMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("hotkeymode")
public class HotkeyModeCommand extends BaseCommand {

    @Default
    @Description("Toggles hotkey mode")
    public void hotkeyMode(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        databasePlayer.setHotkeyMode(databasePlayer.getHotkeyMode() == HotkeyMode.NEW_MODE ? HotkeyMode.CLASSIC_MODE : HotkeyMode.NEW_MODE);
        if (databasePlayer.getHotkeyMode() == HotkeyMode.NEW_MODE) {
            player.sendMessage(Component.text("Hotkey Mode ", NamedTextColor.GREEN)
                                        .append(Component.text("NEW ", NamedTextColor.YELLOW))
                                        .append(Component.text("enabled.")));
        } else {
            player.sendMessage(Component.text("Hotkey Mode ", NamedTextColor.GREEN)
                                        .append(Component.text("CLASSIC ", NamedTextColor.AQUA))
                                        .append(Component.text("enabled.")));
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

}
