package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class MessageCommand implements CommandExecutor {

    public static final LinkedHashMap<PlayerMessage, Long> lastPlayerMessages = new LinkedHashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        switch (s) {
            case "tell":
            case "msg": {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Invalid Parameters! /msg (player) (message)");
                    return true;
                }
                String targetPlayer = args[0];
                Player otherPlayer = Bukkit.getPlayer(targetPlayer);
                if (otherPlayer != null) {
                    if (otherPlayer.equals(player)) {
                        sender.sendMessage(ChatColor.RED + "You cannot message yourself");
                        return true;
                    }
                    StringBuilder message = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        message.append(args[i]).append(" ");

                    }
                    sender.sendMessage(ChatColor.DARK_PURPLE + "To " + ChatColor.AQUA + otherPlayer.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
                    otherPlayer.sendMessage(ChatColor.DARK_PURPLE + "From " + ChatColor.AQUA + sender.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
                    PlayerMessage newPlayerMessage = new PlayerMessage(player.getUniqueId(), otherPlayer.getUniqueId());
                    lastPlayerMessages.put(newPlayerMessage, System.currentTimeMillis());
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Cannot find that player");
                return true;
            }
            case "r": {
                if (args.length < 1) {
                    sender.sendMessage(ChatColor.RED + "Invalid Parameters! /r (message)");
                    return true;
                }
                Optional<PlayerMessage> playerMessage = lastPlayerMessages.entrySet().stream()
                        .filter(playerMessageLongEntry -> playerMessageLongEntry.getKey().getTo().equals(player.getUniqueId()))
                        .sorted((o1, o2) -> Long.compare(o2.getValue(), o1.getValue()))
                        .map(Map.Entry::getKey)
                        .findFirst();
                if (playerMessage.isPresent()) {
                    StringBuilder message = new StringBuilder();
                    for (String arg : args) {
                        message.append(arg).append(" ");
                    }
                    Player otherPlayer = Bukkit.getPlayer(playerMessage.get().getFrom());
                    if (otherPlayer != null) {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "To " + ChatColor.AQUA + otherPlayer.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
                        otherPlayer.sendMessage(ChatColor.DARK_PURPLE + "From " + ChatColor.AQUA + sender.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
                        PlayerMessage newPlayerMessage = new PlayerMessage(player.getUniqueId(), otherPlayer.getUniqueId());
                        lastPlayerMessages.put(newPlayerMessage, System.currentTimeMillis());
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "That player is no longer online");
                    return true;
                }
                sender.sendMessage(ChatColor.RED + "Nobody has messages you within the last 5 minutes");
                return true;
            }
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("msg").setExecutor(this);
        new BukkitRunnable() {

            @Override
            public void run() {
                lastPlayerMessages.entrySet().removeIf(playerMessageLongEntry -> System.currentTimeMillis() - playerMessageLongEntry.getValue() >= 300000);
            }
        }.runTaskTimer(Warlords.getInstance(), 40, 20 * 60 * 5);
    }

}

class PlayerMessage {
    private final UUID from;
    private final UUID to;

    public PlayerMessage(UUID from, UUID to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "PlayerMessage{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    public UUID getFrom() {
        return from;
    }

    public UUID getTo() {
        return to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerMessage that = (PlayerMessage) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
