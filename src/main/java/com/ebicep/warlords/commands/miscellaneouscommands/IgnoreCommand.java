package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@CommandAlias("ignore")
public class IgnoreCommand extends BaseCommand {

    @Default
    @Description("Toggling ignoring a player")
    public void ignore(Player player, @Flags("other") Player toIgnore) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            UUID toIgnoreUUID = toIgnore.getUniqueId();
            List<UUID> ignored = databasePlayer.getIgnored();
            if (player.getUniqueId().equals(toIgnoreUUID)) {
                player.sendMessage(Component.text("You cant ignore yourself!", NamedTextColor.RED));
                return;
            }
            if (ignored.contains(toIgnoreUUID)) {
                ignored.remove(toIgnoreUUID);
                player.sendMessage(Component.text("You unignored " + toIgnore.getName() + ".", NamedTextColor.RED));
            } else {
                ignored.add(toIgnoreUUID);
                player.sendMessage(Component.text("You ignored " + toIgnore.getName() + ".", NamedTextColor.RED));
            }
        });
    }

    @Subcommand("list")
    @Description("Shows a list of ignored players")
    public void ignoreList(Player player) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            List<UUID> ignored = databasePlayer.getIgnored();
            TextComponent.Builder textBuilder = Component.text("Ignored List", NamedTextColor.RED, TextDecoration.UNDERLINED)
                                                         .toBuilder();
            for (int i = 0; i < ignored.size(); i++) {
                UUID uuid = ignored.get(i);
                String name = Bukkit.getOfflinePlayer(uuid).getName();
                if (name == null) {
                    name = "???";
                }
                textBuilder.append(Component.newline()
                                            .append(Component.text(" " + i + ". ", NamedTextColor.YELLOW))
                                            .append(Component.text(name, NamedTextColor.AQUA)));
            }
            player.sendMessage(textBuilder);
        });
    }

}
