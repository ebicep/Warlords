package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Poll {

    private final Party party;
    private final String question;
    private final List<String> options;
    private int timeLeft = 30;
    private final boolean infiniteVotingTime;
    private final HashMap<UUID, Integer> playerAnsweredWithOption = new HashMap<>();
    private final List<UUID> excludedPlayers;
    private Runnable runnableAfterPollEnded;

    public Poll(Party party, String question, List<String> options, boolean infiniteVotingTime, List<UUID> excludedPlayers, Runnable runnableAfterPollEnded) {
        this.party = party;
        this.question = question;
        this.options = options;
        this.infiniteVotingTime = infiniteVotingTime;
        this.excludedPlayers = excludedPlayers;
        this.runnableAfterPollEnded = runnableAfterPollEnded;
        sendPollAnnouncement();
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                if (timeLeft <= 0 || getNumberOfPlayersThatCanVote() == playerAnsweredWithOption.size()) {
                    sendPollResults();
                    party.getPolls().remove(Poll.this);
                    this.cancel();
                } else {
                    if (!infiniteVotingTime) {
                        if (timeLeft == 15) {
                            sendPollAnnouncement();
                        }
                        timeLeft--;
                    } else {
                        if (counter % 15 == 0) {
                            sendPollAnnouncement();
                        }
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    private int getNumberOfPlayersThatCanVote() {
        return party.getPartyPlayers().size() - excludedPlayers.size();
    }

    private List<Player> getPlayersAllowedToVote() {
        return party.getAllPartyPeoplePlayerOnline().stream()
                .filter(player -> !excludedPlayers.contains(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    private void sendPollAnnouncement() {
        getPlayersAllowedToVote().forEach(player -> {
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
            if (!infiniteVotingTime) {
                player.sendMessage(ChatColor.YELLOW + "The poll will end in " + timeLeft + " seconds!");
            } else {
                player.sendMessage(ChatColor.YELLOW + "The poll will end in when everyone has voted!");
            }
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
        });
    }

    private void sendPollResults() {
        int[] numberOfVote = new int[options.size()];
        String[] squareRatio = new String[options.size()];
        for (int i = 0; i < options.size(); i++) {
            int finalI = i;
            numberOfVote[i] = (int) playerAnsweredWithOption.values().stream().filter(v -> v == finalI + 1).count();

            int counter = (int) Math.round((double) numberOfVote[i] / playerAnsweredWithOption.size() * 10);
            squareRatio[i] = ChatColor.GREEN.toString();
            for (int j = 0; j < counter; j++) {
                squareRatio[i] += "■";
            }
            squareRatio[i] += ChatColor.GRAY;
            for (int j = 0; j < 10 - counter; j++) {
                squareRatio[i] += "■";
            }
        }
        getPlayersAllowedToVote().forEach(player -> {
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            player.sendMessage(ChatColor.YELLOW + "Question: " + ChatColor.GREEN + question);
            for (int i = 0; i < options.size(); i++) {
                player.sendMessage(ChatColor.GOLD + options.get(i) + ChatColor.DARK_GRAY + " - " +
                        ChatColor.YELLOW + numberOfVote[i] +
                        " (" + (Math.round((double) numberOfVote[i] / playerAnsweredWithOption.size() * 100)) + "%) " +
                        ChatColor.GOLD + "[" + squareRatio[i] + ChatColor.GOLD + "]"
                );
            }
            Set<UUID> nonVoters = getPlayersAllowedToVote().stream().map(Entity::getUniqueId).collect(Collectors.toSet());
            nonVoters.removeAll(playerAnsweredWithOption.keySet());
            StringBuilder playersThatDidntVote = new StringBuilder(ChatColor.YELLOW + "Non Voters: " + ChatColor.AQUA);
            for (UUID nonVoter : nonVoters) {
                playersThatDidntVote.append(ChatColor.AQUA).append(Bukkit.getOfflinePlayer(nonVoter).getName())
                        .append(ChatColor.GRAY).append(", ");
            }
            playersThatDidntVote.setLength(playersThatDidntVote.length() - 2);
            if (getNumberOfPlayersThatCanVote() != playerAnsweredWithOption.size() && (party.getPartyLeader().getUuid().equals(player.getUniqueId()) || party.getPartyModerators().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId())))) {
                player.sendMessage(playersThatDidntVote.toString());
            }
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
        });
        runnableAfterPollEnded.run();
    }

    public HashMap<String, Integer> getOptionsWithVotes() {
        HashMap<String, Integer> votes = new HashMap<>();
        options.forEach(s -> {
            votes.put(s, 0);
        });
        playerAnsweredWithOption.forEach((uuid, integer) -> {
            String option = options.get(integer - 1);
            votes.put(option, votes.get(option) + 1);
        });
        return votes;
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

    public List<UUID> getExcludedPlayers() {
        return excludedPlayers;
    }

    public void setRunnableAfterPollEnded(Runnable runnableAfterPollEnded) {
        this.runnableAfterPollEnded = runnableAfterPollEnded;
    }
}
