package com.ebicep.warlords.poll;

import com.ebicep.warlords.Warlords;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractPoll<T extends AbstractPoll<T>> {

    public static final List<AbstractPoll<?>> POLLS = new ArrayList<>();

    public static Optional<AbstractPoll<?>> getPoll(String pollID) {
        return AbstractPoll.POLLS.stream().filter(p -> AbstractPoll.getPollID(p).equals(pollID)).findAny();
    }

    public static String getPollID(AbstractPoll<?> poll) {
        String toString = poll.toString();
        return toString.substring(toString.indexOf("@") + 1);
    }

    protected final HashMap<UUID, Integer> playerAnsweredWithOption = new HashMap<>();
    protected String id;
    protected String question;
    protected List<String> options;
    protected int timeLeft = 30;
    protected boolean infiniteVotingTime = false;
    protected List<UUID> excludedPlayers = new ArrayList<>();
    protected Consumer<T> onPollEnd;

    public AbstractPoll() {

    }

    public void init() {
        AbstractPoll.POLLS.add(this);
        id = AbstractPoll.getPollID(this);

        sendPollAnnouncement(true);
        new BukkitRunnable() {
            int counter = 0;

            @Override
            public void run() {
                counter++;
                if (timeLeft <= 0 || getNumberOfPlayersThatCanVote() == playerAnsweredWithOption.size()) {
                    sendPollResults();
                    onPollEnd();

                    AbstractPoll.POLLS.remove(AbstractPoll.this);
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

    public abstract List<UUID> getUUIDsAllowedToVote();

    public abstract boolean sendNonVoterMessage(Player player);

    public abstract void onPollEnd();

    public List<Player> getPlayersAllowedToVote() {
        List<UUID> uuids = getUUIDsAllowedToVote();
        return Bukkit.getOnlinePlayers()
                     .stream()
                     .filter(player -> uuids.contains(player.getUniqueId()))
                     .collect(Collectors.toList());
    }

    private void sendPollAnnouncement(boolean first) {
        getPlayersAllowedToVote().forEach(player -> {
            player.sendMessage(Component.text("------------------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD));
            if (first) {
                player.sendMessage(Component.text("There is a new poll! Answer it below by clicking on an option!", NamedTextColor.YELLOW));
            }
            player.sendMessage(Component.text("Question: ", NamedTextColor.YELLOW).append(Component.text(question, NamedTextColor.GREEN)));
            for (int i = 0; i < options.size(); i++) {
                player.sendMessage(Component.text(" - " + (i + 1) + ". ", NamedTextColor.YELLOW).append(Component.text(options.get(i), NamedTextColor.GOLD))
                                            .hoverEvent(HoverEvent.showText(Component.text("Click here to vote for " + options.get(i), NamedTextColor.GREEN)))
                                            .clickEvent(ClickEvent.runCommand("/poll answer " + id + " " + (i + 1)))
                );
            }
            if (!infiniteVotingTime) {
                player.sendMessage(Component.text("The poll will end in " + timeLeft + " seconds! - " + id, NamedTextColor.YELLOW)
                                            .append(Component.text(id).clickEvent(ClickEvent.copyToClipboard(id)))
                );
            } else {
                player.sendMessage(Component.text("The poll will end in when everyone has voted!", NamedTextColor.YELLOW));
            }
            player.sendMessage(Component.text("------------------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD));
        });
    }

    private void sendPollResults() {
        sendPollResultsToPlayers(getPlayersAllowedToVote());
        if (onPollEnd != null) {
            onPollEnd.accept((T) this);
        }
    }

    public void sendPollResultsToPlayers(List<Player> players) {
        int[] numberOfVote = new int[options.size()];
        Component[] squareRatio = new Component[options.size()];
        for (int i = 0; i < options.size(); i++) {
            int finalI = i;
            numberOfVote[i] = (int) playerAnsweredWithOption.values().stream().filter(v -> v == finalI + 1).count();

            int counter = (int) Math.round((double) numberOfVote[i] / playerAnsweredWithOption.size() * 10);
            squareRatio[i] = Component.text("■".repeat(counter), NamedTextColor.GREEN)
                                      .append(Component.text("■".repeat(10 - counter), NamedTextColor.GRAY));
        }
        players.forEach(player -> {
            player.sendMessage(Component.text("------------------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD));
            player.sendMessage(Component.text("Question: ", NamedTextColor.YELLOW).append(Component.text(question, NamedTextColor.GREEN)));
            for (int i = 0; i < options.size(); i++) {
                long percentageVoted = Math.round((double) numberOfVote[i] / playerAnsweredWithOption.size() * 100);
                player.sendMessage(Component.text(options.get(i), NamedTextColor.GOLD)
                                            .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                                            .append(Component.text(numberOfVote[i] + " (" + percentageVoted + "%) ", NamedTextColor.YELLOW))
                                            .append(Component.text("["))
                                            .append(squareRatio[i])
                                            .append(Component.text("]"))
                );
            }
            Set<UUID> nonVoters = new HashSet<>(getUUIDsAllowedToVote());
            nonVoters.removeAll(playerAnsweredWithOption.keySet());
            Component playersThatDidntVote = Component.text("Non Voters: ", NamedTextColor.YELLOW)
                                                      .append(nonVoters.stream()
                                                                       .map(uuid -> Component.text(Bukkit.getOfflinePlayer(uuid).getName(), NamedTextColor.AQUA))
                                                                       .collect(Component.toComponent(Component.text(", ", NamedTextColor.GRAY))));
            if (sendNonVoterMessage(player)) {
                player.sendMessage(playersThatDidntVote.toString());
            }
            player.sendMessage(Component.text("------------------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD));
        });
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

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public boolean isInfiniteVotingTime() {
        return infiniteVotingTime;
    }

    public void setInfiniteVotingTime(boolean infiniteVotingTime) {
        this.infiniteVotingTime = infiniteVotingTime;
    }

    public List<UUID> getExcludedPlayers() {
        return excludedPlayers;
    }

    public void setExcludedPlayers(List<UUID> excludedPlayers) {
        this.excludedPlayers = excludedPlayers;
    }

    public Consumer<T> getOnPollEnd() {
        return onPollEnd;
    }

    public void setOnPollEnd(Consumer<T> onPollEnd) {
        this.onPollEnd = onPollEnd;
    }

    public HashMap<UUID, Integer> getPlayerAnsweredWithOption() {
        return playerAnsweredWithOption;
    }

    protected static abstract class Builder<T extends AbstractPoll<T>, B extends Builder<T, B>> {

        protected T poll;
        protected B builder;

        public Builder() {
            poll = createPoll();
            builder = thisBuilder();
        }

        public abstract T createPoll();

        public abstract B thisBuilder();

        public T get() {
            poll.init();
            return poll;
        }

        public B setQuestion(String question) {
            poll.setQuestion(question);
            return builder;
        }

        public B setInfiniteVotingTime(boolean infiniteVotingTime) {
            poll.setInfiniteVotingTime(infiniteVotingTime);
            return builder;
        }

        public B setOptions(List<String> options) {
            poll.setOptions(options);
            return builder;
        }

        public B setTimeLeft(int timeLeft) {
            poll.setTimeLeft(timeLeft);
            return builder;
        }

        public B setExcludedPlayers(List<UUID> excludedPlayers) {
            poll.setExcludedPlayers(excludedPlayers);
            return builder;
        }

        public B setRunnableAfterPollEnded(Consumer<T> runnableAfterPollEnded) {
            poll.setOnPollEnd(runnableAfterPollEnded);
            return builder;
        }
    }
}
