package com.ebicep.warlords.commands.debugcommands.misc;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GetPlayerLastAbilityStatsCommand implements CommandExecutor {

    public static HashMap<UUID, List<TextComponent>> playerLastAbilityStats = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.isOp()) {
            return true;
        }

        Player player = BaseCommand.requirePlayer(sender);

        if (args.length > 0) {
            String name = args[0];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
            if (offlinePlayer == null || !playerLastAbilityStats.containsKey(offlinePlayer.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "Player not found");
                return true;
            }

            ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "--------------------------------------------------");
            ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "Last ability stats for " + ChatColor.AQUA + offlinePlayer.getName());
            ChatUtils.sendCenteredMessage(player, "");

            List<TextComponent> lastAbilityStats = playerLastAbilityStats.get(offlinePlayer.getUniqueId());
            ChatUtils.sendCenteredMessageWithEvents(player, Arrays.asList(lastAbilityStats.get(0), ChatUtils.SPACER, lastAbilityStats.get(1), ChatUtils.SPACER, lastAbilityStats.get(2)));
            ChatUtils.sendCenteredMessageWithEvents(player, Arrays.asList(lastAbilityStats.get(3), ChatUtils.SPACER, lastAbilityStats.get(4)));
            ChatUtils.sendCenteredMessage(player, ChatColor.GREEN + "--------------------------------------------------");

        } else {
            sender.sendMessage(ChatColor.RED + "Invalid arguments");
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("abilitystats").setExecutor(this);
    }

}
