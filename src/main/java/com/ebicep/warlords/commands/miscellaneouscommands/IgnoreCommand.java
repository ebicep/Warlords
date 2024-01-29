package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import com.ebicep.warlords.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@CommandAlias("ignore")
public class IgnoreCommand extends BaseCommand {

    @Default
    @Description("Toggling ignoring a player")
    public void ignore(Player player, @Flags("other") Player toIgnore) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            List<UUID> ignored = databasePlayer.getIgnored();
            UUID toIgnoreUUID = toIgnore.getUniqueId();
            if (ignored.contains(toIgnoreUUID)) {
                ignored.remove(toIgnoreUUID);
                player.sendMessage(Component.text("You unignored " + toIgnore.getName(), NamedTextColor.RED));
            } else {
                ignored.add(toIgnoreUUID);
                player.sendMessage(Component.text("You ignored " + toIgnore.getName(), NamedTextColor.RED));
            }
        });
    }

}
