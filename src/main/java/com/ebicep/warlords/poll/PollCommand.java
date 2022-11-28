package com.ebicep.warlords.poll;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Private;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@CommandAlias("poll")
@Private
public class PollCommand extends BaseCommand {

    public static void sendPollMessage(Player player, String message) {
        ChatUtils.sendMessageToPlayer(player, message, ChatColor.GOLD, true);
    }

    @Subcommand("answer")
    public void answer(Player player, AbstractPoll<?> poll, Integer answer) {
        HashMap<UUID, Integer> playerAnsweredWithOption = poll.getPlayerAnsweredWithOption();
        if (playerAnsweredWithOption.containsKey(player.getUniqueId())) {
            if (Objects.equals(playerAnsweredWithOption.get(player.getUniqueId()), answer)) {
                sendPollMessage(player, ChatColor.RED + "You already voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.RED + "!");
            } else {
                playerAnsweredWithOption.put(player.getUniqueId(), answer);
                sendPollMessage(player, ChatColor.GREEN + "You changed your vote to " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!");
            }
        } else if (answer > 0 && answer <= poll.getOptions().size()) {
            playerAnsweredWithOption.put(player.getUniqueId(), answer);
            sendPollMessage(player, ChatColor.GREEN + "You voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!");
        } else {
            sendPollMessage(player, ChatColor.RED + "Invalid Arguments!");
        }

    }

    @Subcommand("end")
    @CommandPermission("minecraft.command.op|warlords.poll.end")
    public void end(Player player, AbstractPoll<?> poll) {
        poll.setTimeLeft(0);
    }
}
