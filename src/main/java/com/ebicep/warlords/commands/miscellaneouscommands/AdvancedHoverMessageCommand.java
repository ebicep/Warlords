package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.general.settings.AdvancedHoverMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

@CommandAlias("advancedhovermessages")
public class AdvancedHoverMessageCommand extends BaseCommand {

    @Default
    @Description("Toggles advanced hover messages.")
    public void toggle(Player player) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        databasePlayer.setAdvancedHoverMessages(databasePlayer.getAdvancedHoverMessages() == AdvancedHoverMessages.ON ?
                                                AdvancedHoverMessages.OFF :
                                                AdvancedHoverMessages.ON);
        if (databasePlayer.getAdvancedHoverMessages() == AdvancedHoverMessages.ON) {
            player.sendMessage(Component.text("Advanced Hover Messages ", NamedTextColor.GREEN)
                                        .append(Component.text("enabled."))
            );
        } else {
            player.sendMessage(Component.text("Advanced Hover Messages ", NamedTextColor.GREEN)
                                        .append(Component.text("disabled.", NamedTextColor.GREEN)));
        }
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

}
