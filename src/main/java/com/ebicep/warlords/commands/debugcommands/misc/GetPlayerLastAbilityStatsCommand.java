package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@CommandAlias("abilitystats")
@CommandPermission("group.administrator")
public class GetPlayerLastAbilityStatsCommand extends BaseCommand {

    public static final HashMap<UUID, List<Component>> PLAYER_LAST_ABILITY_STATS = new HashMap<>();

    public static void sendLastAbilityStats(Player player, UUID uuid) {
        Component formattedData = Component.empty();
        List<Component> components = PLAYER_LAST_ABILITY_STATS.get(uuid);
        for (int i = 0; i < components.size() && i < 3; i++) {
            formattedData.append(components.get(i));
            if (i < 2) {
                formattedData.append(ChatUtils.SPACER);
            }
        }
        ChatUtils.sendCenteredMessage(player, formattedData);
        formattedData = Component.empty();
        for (int i = 3; i < components.size(); i++) {
            formattedData.append(components.get(i));
            if (i < 4) {
                formattedData.append(ChatUtils.SPACER);
            }
        }
        ChatUtils.sendCenteredMessage(player, formattedData);
    }

    @Default
    @CommandCompletion("@playerabilitystats")
    @Description("Prints the last ability stats of a player")
    public void getLastAbilityStats(Player player, @Values("@playerabilitystats") String name) {
        for (UUID uuid : PLAYER_LAST_ABILITY_STATS.keySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (Objects.equals(offlinePlayer.getName(), name)) {
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "--------------------------------------------------");
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "Last ability stats for " + ChatColor.AQUA + offlinePlayer.getName());
                ChatUtils.sendCenteredMessage(player, "");
                sendLastAbilityStats(player, uuid);
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "--------------------------------------------------");
                return;
            }
        }
    }

}
