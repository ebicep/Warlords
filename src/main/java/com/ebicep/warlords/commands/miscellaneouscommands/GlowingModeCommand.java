package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.general.settings.GlowingMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("glowingmode")
public class GlowingModeCommand extends BaseCommand {

    @Default
    @Description("Toggles glowing mode")
    public void glowingMode(Player player) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.setGlowingMode(databasePlayer.getGlowingMode() == GlowingMode.ON ?
                                          GlowingMode.OFF :
                                          GlowingMode.ON);
            if (databasePlayer.getGlowingMode() == GlowingMode.ON) {
                player.sendMessage(Component.text("Glowing Mode ", NamedTextColor.GREEN)
                                            .append(Component.text("enabled."))
                );
            } else {
                player.sendMessage(Component.text("Glowing Mode ", NamedTextColor.GREEN)
                                            .append(Component.text("disabled.", NamedTextColor.GREEN)));
            }
        });
    }
}
