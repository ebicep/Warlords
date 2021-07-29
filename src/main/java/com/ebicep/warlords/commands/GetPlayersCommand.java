package com.ebicep.warlords.commands;

import com.ebicep.warlords.Warlords;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetPlayersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = BaseCommand.requirePlayer(commandSender);
        if (player != null) {
            StringBuilder players = new StringBuilder();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                players.append(onlinePlayer.getName()).append(",");
            }
            players.setLength(players.length() - 1);
            TextComponent message = new TextComponent(ChatColor.GREEN.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "CLICK ME FOR PLAYERS");
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Copy the URL without (https://)").create()));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://" + players.toString()));
            player.spigot().sendMessage(message);
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("getplayers").setExecutor(this);
    }
}
