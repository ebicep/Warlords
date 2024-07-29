package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.player.general.settings.FastWaveMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("fastwavemode")
public class FastWaveModeCommand extends BaseCommand {

    @Default
    @Description("Toggles fast wave mode")
    public void toggle(Player player) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.setFastWaveMode(databasePlayer.getFastWaveMode() == FastWaveMode.ON ?
                                           FastWaveMode.OFF :
                                           FastWaveMode.ON);
            if (databasePlayer.getFastWaveMode() == FastWaveMode.ON) {
                player.sendMessage(Component.text("Fast Wave Mode ", NamedTextColor.GREEN)
                                            .append(Component.text("enabled."))
                );
            } else {
                player.sendMessage(Component.text("Fast Wave Mode ", NamedTextColor.GREEN)
                                            .append(Component.text("disabled.", NamedTextColor.GREEN)));
            }
        });
    }
}
