package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Poll {

    private Party party;
    private String question;
    private List<String> options;
    private int timeLeft = 30;
    private HashMap<UUID, Integer> playerAnsweredWithOption = new HashMap<>();

    public Poll(Party party, String question, List<String> options) {
        this.party = party;
        this.question = question;
        this.options = options;
        sendPollAnnouncement();
        new BukkitRunnable() {

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    sendPollResults();
                    party.getPolls().remove(Poll.this);
                    this.cancel();
                } else {
                    if (timeLeft == 15) {
                        sendPollAnnouncement();
                    }
                    timeLeft--;
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    private void sendPollAnnouncement() {
        party.getAllPartyPeoplePlayerOnline().forEach(player -> {
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            if (timeLeft == 30) {
                player.sendMessage(ChatColor.AQUA + party.getLeaderName() + ChatColor.YELLOW + " created a poll! Answer it below by clicking on an option!");
            }
            player.sendMessage(ChatColor.YELLOW + "Question: " + ChatColor.GREEN + question);
            for (int i = 0; i < options.size(); i++) {
                TextComponent message = new TextComponent(ChatColor.YELLOW + " - " + (i + 1) + ". " + ChatColor.GOLD + options.get(i));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click here to vote for " + options.get(i)).create()));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party pollanswer " + (i + 1)));
                player.spigot().sendMessage(message);
            }
            player.sendMessage(ChatColor.YELLOW + "The poll will end in " + timeLeft + " seconds!");
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
        });
    }

    private void sendPollResults() {
        int[] numberOfVote = new int[options.size()];
        String[] squareRatio = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            int finalI = i;
            numberOfVote[i] = (int) playerAnsweredWithOption.values().stream().filter(v -> v == finalI + 1).count();

            int counter = (int) Math.round((double) numberOfVote[i] / options.size() * 10);
            squareRatio[i] = ChatColor.GREEN.toString();
            for (int j = 0; j < counter; j++) {
                squareRatio[i] += "■";
            }
            squareRatio[i] += ChatColor.GRAY;
            for (int j = 0; j < 10 - counter; j++) {
                squareRatio[i] += "■";
            }
        }
        party.getAllPartyPeoplePlayerOnline().forEach(player -> {
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            player.sendMessage(ChatColor.YELLOW + "Question: " + ChatColor.GREEN + question);
            for (int i = 0; i < options.size(); i++) {
                player.sendMessage(ChatColor.GOLD + options.get(i) + ChatColor.DARK_GRAY + " - " +
                        ChatColor.YELLOW + numberOfVote[i] +
                        " (" + (Math.round((double) numberOfVote[i] / options.size() * 100)) + "%) " +
                        ChatColor.GOLD + "[" + squareRatio[i] + ChatColor.GOLD + "]"
                        );
            }
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
        });
    }

    public List<String> getOptions() {
        return options;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public HashMap<UUID, Integer> getPlayerAnsweredWithOption() {
        return playerAnsweredWithOption;
    }

}
