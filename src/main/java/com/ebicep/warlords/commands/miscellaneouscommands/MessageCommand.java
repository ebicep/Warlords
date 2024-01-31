package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.HelpCommand;
import com.ebicep.warlords.commands.debugcommands.misc.MuteCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class MessageCommand extends BaseCommand {

    private static final LinkedHashMap<PlayerMessage, Instant> LAST_PLAYER_MESSAGES = new LinkedHashMap<>();

    @CommandAlias("msg|tell")
    @Description("Privately message a player")
    public void message(Player player, @Flags("other") Player target, String message) {
        if (MuteCommand.MUTED_PLAYERS.getOrDefault(player.getUniqueId(), false)) {
            return;
        }
        if (player.getUniqueId().equals(target.getUniqueId())) {
            player.sendMessage(Component.text("You cannot message yourself!", NamedTextColor.RED));
            return;
        }

        player.sendMessage(Component.empty()
                                    .append(Component.text("To ", NamedTextColor.DARK_PURPLE))
                                    .append(Component.text(target.getName(), NamedTextColor.AQUA))
                                    .append(Component.text(": ", NamedTextColor.WHITE))
                                    .append(Component.text(message, NamedTextColor.LIGHT_PURPLE)));
        target.sendMessage(Component.empty()
                                    .append(Component.text("From ", NamedTextColor.DARK_PURPLE))
                                    .append(Component.text(player.getName(), NamedTextColor.AQUA))
                                    .append(Component.text(": ", NamedTextColor.WHITE))
                                    .append(Component.text(message, NamedTextColor.LIGHT_PURPLE)));
        PlayerMessage newPlayerMessage = new PlayerMessage(player.getUniqueId(), target.getUniqueId());
        LAST_PLAYER_MESSAGES.put(newPlayerMessage, Instant.now());
    }

    @CommandAlias("r")
    @Description("Reply to a player")
    public void reply(Player player, String message) {
        Optional<PlayerMessage> playerMessage = LAST_PLAYER_MESSAGES.entrySet().stream()
                                                                    .filter(playerMessageLongEntry -> playerMessageLongEntry.getKey()
                                                                                                                            .to()
                                                                                                                            .equals(player.getUniqueId()))
                                                                    .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                                                                    .map(Map.Entry::getKey)
                                                                    .findFirst();
        if (playerMessage.isPresent() && Instant.now().isBefore(LAST_PLAYER_MESSAGES.get(playerMessage.get()).plus(5, ChronoUnit.MINUTES))) {
            Player otherPlayer = Bukkit.getPlayer(playerMessage.get().from());
            if (otherPlayer != null) {
                player.sendMessage(Component.empty()
                                            .append(Component.text("To ", NamedTextColor.DARK_PURPLE))
                                            .append(Component.text(otherPlayer.getName(), NamedTextColor.AQUA))
                                            .append(Component.text(": ", NamedTextColor.WHITE))
                                            .append(Component.text(message, NamedTextColor.LIGHT_PURPLE)));
                otherPlayer.sendMessage(Component.empty()
                                                 .append(Component.text("From ", NamedTextColor.DARK_PURPLE))
                                                 .append(Component.text(player.getName(), NamedTextColor.AQUA))
                                                 .append(Component.text(": ", NamedTextColor.WHITE))
                                                 .append(Component.text(message, NamedTextColor.LIGHT_PURPLE)));
                PlayerMessage newPlayerMessage = new PlayerMessage(player.getUniqueId(), otherPlayer.getUniqueId());
                LAST_PLAYER_MESSAGES.put(newPlayerMessage, Instant.now());
            } else {
                player.sendMessage(Component.text("That player is no longer online!", NamedTextColor.RED));
            }
        } else {
            player.sendMessage(Component.text("Nobody has messages you within the last 5 minutes.", NamedTextColor.RED));
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }
}

record PlayerMessage(UUID from, UUID to) {

    @Override
    public String toString() {
        return "PlayerMessage{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlayerMessage that = (PlayerMessage) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to);
    }

}
