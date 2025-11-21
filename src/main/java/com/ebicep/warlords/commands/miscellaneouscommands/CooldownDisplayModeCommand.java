package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.settings.CooldownDisplaySettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("cooldowndisplaymode")
public class CooldownDisplayModeCommand extends BaseCommand {

    @Default
    @Description("Toggles cooldown display mode")
    public void toggle(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        databasePlayer.getCooldownDisplaySettings()
                      .setCooldownDisplayMode(databasePlayer.getCooldownDisplaySettings().getCooldownDisplayMode() == CooldownDisplaySettings.CooldownDisplayMode.ON ?
                                              CooldownDisplaySettings.CooldownDisplayMode.OFF :
                                              CooldownDisplaySettings.CooldownDisplayMode.ON);
        if (databasePlayer.getCooldownDisplaySettings().getCooldownDisplayMode() == CooldownDisplaySettings.CooldownDisplayMode.ON) {
            player.sendMessage(Component.text("Cooldown Display Mode ", NamedTextColor.GREEN)
                                        .append(Component.text("enabled."))
            );
        } else {
            player.sendMessage(Component.text("Cooldown Display Mode ", NamedTextColor.GREEN)
                                        .append(Component.text("disabled.", NamedTextColor.GREEN)));
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

}
