package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.settings.AdvancedHoverMessages;
import com.ebicep.warlords.player.general.settings.FastWaveMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("advancedhovermessages")
public class AdvancedHoverMessageCommand extends BaseCommand {

    @Default
    @Description("Toggles advanced hover messages.")
    public void toggle(Player player) {
        DatabaseManager.updatePlayer(player.getUniqueId(), dbp -> {
            dbp.setAdvancedHoverMessages(dbp.getAdvancedHoverMessages() == AdvancedHoverMessages.ON ?
                    AdvancedHoverMessages.OFF :
                    AdvancedHoverMessages.ON);
            WarlordsEntity inGameWarlordsPlayer = Warlords.getPlayer(player);
            WarlordsPlayer warlordsPlayer = inGameWarlordsPlayer instanceof WarlordsPlayer wp ?
                    wp :
                    new WarlordsPlayer(
                            player,
                            PlayerSettings.getPlayerSettings(player).getSelectedSpec(),
                            ConfigManager.PVE_NAMESPACES
                    );
            if (dbp.getAdvancedHoverMessages() == AdvancedHoverMessages.ON) {
                player.sendMessage(Component.text("Advanced Hover Messages ", NamedTextColor.GREEN)
                        .append(Component.text("enabled."))
                );
                warlordsPlayer.setShowDebugMessage(true);
            } else {
                player.sendMessage(Component.text("Advanced Hover Messages ", NamedTextColor.GREEN)
                        .append(Component.text("disabled.", NamedTextColor.GREEN)));
                warlordsPlayer.setShowDebugMessage(false);
            }
        });
    }
}
