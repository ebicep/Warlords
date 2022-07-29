package com.ebicep.warlords.commands2.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("abilitystats")
public class GetPlayerLastAbilityStatsCommand extends BaseCommand {

    public static HashMap<UUID, List<TextComponent>> playerLastAbilityStats = new HashMap<>();

    @Default
    @CommandCompletion("@playerabilitystats")
    @Description("Prints the last ability stats of a player")
    public void getLastAbilityStats(Player player, @Values("@playerabilitystats") String name) {
        for (UUID uuid : playerLastAbilityStats.keySet()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer != null && Objects.equals(offlinePlayer.getName(), name)) {
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "--------------------------------------------------");
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "Last ability stats for " + ChatColor.AQUA + offlinePlayer.getName());
                ChatUtils.sendCenteredMessage(player, "");

                List<TextComponent> lastAbilityStats = playerLastAbilityStats.get(offlinePlayer.getUniqueId());
                ChatUtils.sendCenteredMessageWithEvents(player, Arrays.asList(lastAbilityStats.get(0), ChatUtils.SPACER, lastAbilityStats.get(1), ChatUtils.SPACER, lastAbilityStats.get(2)));
                ChatUtils.sendCenteredMessageWithEvents(player, Arrays.asList(lastAbilityStats.get(3), ChatUtils.SPACER, lastAbilityStats.get(4)));
                ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "--------------------------------------------------");
                return;
            }
        }
    }
}
