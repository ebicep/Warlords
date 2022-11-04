package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("abilitystats")
@CommandPermission("group.administrator")
public class GetPlayerLastAbilityStatsCommand extends BaseCommand {

    public static final HashMap<UUID, List<BaseComponent[]>> PLAYER_LAST_ABILITY_STATS = new HashMap<>();

    @Default
    @CommandCompletion("@playerabilitystats")
    @Description("Prints the last ability stats of a player")
    public void getLastAbilityStats(Player player, @Values("@playerabilitystats") String name) {
        for (UUID uuid : PLAYER_LAST_ABILITY_STATS.keySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer != null && Objects.equals(offlinePlayer.getName(), name)) {
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "--------------------------------------------------");
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "Last ability stats for " + ChatColor.AQUA + offlinePlayer.getName());
                ChatUtils.sendCenteredMessage(player, "");

                List<BaseComponent> formattedData = new ArrayList<>();
                List<BaseComponent[]> components = PLAYER_LAST_ABILITY_STATS.get(uuid);
                for (int i = 0; i < components.size() && i < 3; i++) {
                    formattedData.addAll(List.of(components.get(i)));
                    if (i < 2) {
                        formattedData.add(ChatUtils.SPACER);
                    }
                }
                ChatUtils.sendCenteredMessageWithEvents(player, formattedData.toArray(new BaseComponent[0]));
                formattedData.clear();
                for (int i = 3; i < components.size(); i++) {
                    formattedData.addAll(List.of(components.get(i)));
                    if (i < 4) {
                        formattedData.add(ChatUtils.SPACER);
                    }
                }
                ChatUtils.sendCenteredMessageWithEvents(player, formattedData.toArray(new BaseComponent[0]));
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "Last ability stats for " + ChatColor.AQUA + offlinePlayer.getName());
                return;
            }
        }
    }
}
