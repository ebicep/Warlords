package com.ebicep.warlords.commands2.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import com.ebicep.warlords.commands.debugcommands.misc.MuteCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MessageCommand extends BaseCommand {

    public static final LinkedHashMap<PlayerMessage, Instant> lastPlayerMessages = new LinkedHashMap<>();

    @CommandAlias("msg")
    @Description("Privately message a player")
    public void message(Player player, @Flags("other") Player target, String message) {
        if (MuteCommand.mutedPlayers.getOrDefault(player.getUniqueId(), false)) {
            return;
        }
        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot message yourself!");
            return;
        }

        player.sendMessage(ChatColor.DARK_PURPLE + "To " + ChatColor.AQUA + target.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
        target.sendMessage(ChatColor.DARK_PURPLE + "From " + ChatColor.AQUA + player.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
        PlayerMessage newPlayerMessage = new PlayerMessage(player.getUniqueId(), target.getUniqueId());
        lastPlayerMessages.put(newPlayerMessage, Instant.now());
    }

    @CommandAlias("r")
    @Description("Reply to a player")
    public void reply(Player player, String message) {
        Optional<PlayerMessage> playerMessage = lastPlayerMessages.entrySet().stream()
                .filter(playerMessageLongEntry -> playerMessageLongEntry.getKey().getTo().equals(player.getUniqueId()))
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .map(Map.Entry::getKey)
                .findFirst();
        if (playerMessage.isPresent() && Instant.now().isBefore(lastPlayerMessages.get(playerMessage.get()).plus(5, ChronoUnit.MINUTES))) {
            Player otherPlayer = Bukkit.getPlayer(playerMessage.get().getFrom());
            if (otherPlayer != null) {
                player.sendMessage(ChatColor.DARK_PURPLE + "To " + ChatColor.AQUA + otherPlayer.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
                otherPlayer.sendMessage(ChatColor.DARK_PURPLE + "From " + ChatColor.AQUA + player.getName() + ChatColor.WHITE + ": " + ChatColor.LIGHT_PURPLE + message);
                PlayerMessage newPlayerMessage = new PlayerMessage(player.getUniqueId(), otherPlayer.getUniqueId());
                lastPlayerMessages.put(newPlayerMessage, Instant.now());
            } else {
                player.sendMessage(ChatColor.RED + "That player is no longer online!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Nobody has messages you within the last 5 minutes.");
        }
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
