package com.ebicep.warlords.poll;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractPoll {

    protected String question;
    protected List<String> options;
    protected int timeLeft = 30;
    protected boolean infiniteVotingTime = false;
    protected List<UUID> excludedPlayers = new ArrayList<>();
    protected Consumer<? extends AbstractPoll> onPollEnd;
    protected final HashMap<UUID, Integer> playerAnsweredWithOption = new HashMap<>();

    public AbstractPoll() {

    }

    public void init() {
        sendPollAnnouncement(true);
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                if (timeLeft <= 0 || getNumberOfPlayersThatCanVote() == playerAnsweredWithOption.size()) {
                    sendPollResults();
                    onPollEnd();
                    this.cancel();
                } else {
                    if (!infiniteVotingTime) {
                        if (timeLeft == 15) {
                            sendPollAnnouncement(false);
                        }
                        timeLeft--;
                    } else {
                        if (counter % 15 == 0) {
                            sendPollAnnouncement(false);
                        }
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    public abstract int getNumberOfPlayersThatCanVote();

    public abstract List<Player> getPlayersAllowedToVote();

    public abstract boolean sendNonVoterMessage(Player player);

    public abstract void onPollEnd();

    private void sendPollAnnouncement(boolean first) {
        getPlayersAllowedToVote().forEach(player -> {
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            if (first) {
                player.sendMessage(ChatColor.YELLOW + "There is a new poll! Answer it below by clicking on an option!");
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
            if (sendNonVoterMessage(player)) {
                player.sendMessage(playersThatDidntVote.toString());
            }
            player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
        });
        if (onPollEnd != null) {
           // onPollEnd.accept(this);
        }
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

    public String getQuestion() {
        return question;
    }

    public AbstractPoll setQuestion(String question) {
        this.question = question;
        return this;
    }

    public List<String> getOptions() {
        return options;
    }

    public AbstractPoll setOptions(List<String> options) {
        this.options = options;
        return this;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public AbstractPoll setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        return this;
    }

    public boolean isInfiniteVotingTime() {
        return infiniteVotingTime;
    }

    public AbstractPoll setInfiniteVotingTime(boolean infiniteVotingTime) {
        this.infiniteVotingTime = infiniteVotingTime;
        return this;
    }

    public List<UUID> getExcludedPlayers() {
        return excludedPlayers;
    }

    public AbstractPoll setExcludedPlayers(List<UUID> excludedPlayers) {
        this.excludedPlayers = excludedPlayers;
        return this;
    }

    public Consumer<? extends AbstractPoll> getOnPollEnd() {
        return onPollEnd;
    }

    public AbstractPoll setOnPollEnd(Consumer<? extends AbstractPoll> onPollEnd) {
        this.onPollEnd = onPollEnd;
        return this;
    }

    public HashMap<UUID, Integer> getPlayerAnsweredWithOption() {
        return playerAnsweredWithOption;
    }


    public static class PollBuilder<T extends AbstractPoll> {

        private T poll;

        public PollBuilder() {

        }

        public PollBuilder(T poll) {
            this.poll = poll;
        }

        public PollBuilder setPoll(T poll) {
            this.poll = poll;
            return this;
        }

        public PollBuilder setQuestion(String question) {
            poll.setQuestion(question);
            return this;
        }

        public PollBuilder setInfiniteVotingTime(boolean infiniteVotingTime) {
            poll.setInfiniteVotingTime(infiniteVotingTime);
            return this;
        }

        public PollBuilder setOptions(List<String> options) {
            poll.setOptions(options);
            return this;
        }

        public PollBuilder setTimeLeft(int timeLeft) {
            poll.setTimeLeft(timeLeft);
            return this;
        }

        public PollBuilder setExcludedPlayers(List<UUID> excludedPlayers) {
            poll.setExcludedPlayers(excludedPlayers);
            return this;
        }

        public PollBuilder setRunnableAfterPollEnded(Consumer<T> runnableAfterPollEnded) {
            poll.setOnPollEnd(runnableAfterPollEnded);
            return this;
        }

        public AbstractPoll get() {
            this.poll.init();
            return this.poll;
        }
    }
}
