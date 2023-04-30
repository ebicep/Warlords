package com.ebicep.warlords.poll;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@CommandAlias("poll")
@Private
public class PollCommand extends BaseCommand {

    public static void sendPollMessage(Player player, Component message) {
        ChatUtils.sendMessageToPlayer(player, message, ChatColor.GOLD, true);
    }

    @Subcommand("answer")
    public void answer(Player player, AbstractPoll<?> poll, Integer answer) {
        HashMap<UUID, Integer> playerAnsweredWithOption = poll.getPlayerAnsweredWithOption();
        String pollAnswer = poll.getOptions().get(answer - 1);
        if (playerAnsweredWithOption.containsKey(player.getUniqueId())) {
            if (Objects.equals(playerAnsweredWithOption.get(player.getUniqueId()), answer)) {
                sendPollMessage(player, Component.text("You already voted for ", NamedTextColor.RED)
                                                 .append(Component.text(pollAnswer, NamedTextColor.GOLD))
                                                 .append(Component.text("!"))
                );
            } else {
                playerAnsweredWithOption.put(player.getUniqueId(), answer);
                sendPollMessage(player, Component.text("You changed your vote to ", NamedTextColor.GREEN)
                                                 .append(Component.text(pollAnswer, NamedTextColor.GOLD))
                                                 .append(Component.text("!"))
                );
            }
        } else if (answer > 0 && answer <= poll.getOptions().size()) {
            playerAnsweredWithOption.put(player.getUniqueId(), answer);
            sendPollMessage(player, Component.text("You voted for ", NamedTextColor.GREEN)
                                             .append(Component.text(pollAnswer, NamedTextColor.GOLD))
                                             .append(Component.text("!"))
            );
        } else {
            sendPollMessage(player, Component.text("Invalid Arguments!", NamedTextColor.RED));
        }

    }

    @Subcommand("end")
    @CommandPermission("warlords.poll.end")
    public void end(Player player, AbstractPoll<?> poll) {
        poll.setTimeLeft(0);
    }
}
