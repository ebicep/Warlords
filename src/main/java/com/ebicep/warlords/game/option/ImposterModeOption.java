package com.ebicep.warlords.game.option;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.Poll;
import com.ebicep.warlords.party.PollBuilder;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PacketUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ImposterModeOption implements Option {

    private final int numberOfImpostersPerTeam = 1;
    private final HashMap<Team, List<WarlordsPlayer>> imposters = new HashMap<>();
    private final HashMap<Team, List<WarlordsPlayer>> voters = new HashMap<>();
    private Game game;
    private Poll poll;


    @Override
    public void register(@Nonnull Game game) {
        this.game = game;

        for (Team team : TeamMarker.getTeams(game)) {
            imposters.put(team, new ArrayList<>());
        }

        game.registerScoreboardHandler(new SimpleScoreboardHandler(30, "imposter") {
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer warlordsPlayer) {
//                if (warlordsPlayer == null) {
//                    return Collections.emptyList();
//                } else if ((blueImposterName != null && blueImposterName.equalsIgnoreCase(warlordsPlayer.getName())) ||
//                        (redImposterName != null && redImposterName.equals(warlordsPlayer.getName()))
//                ) {
//                    return Arrays.asList(ChatColor.WHITE + "Role: " + ChatColor.RED + "IMPOSTER");
//                } else {
//                    if (blueImposterName != null && redImposterName != null) {
//                        return Arrays.asList(ChatColor.WHITE + "Role: " + ChatColor.GREEN + "INNOCENT");
//                    } else {
//                        return Collections.emptyList();
//                    }
//                }
                return Collections.emptyList();
            }
        });
    }

    @Override
    public void start(@Nonnull Game game) {
        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if (counter == 4) {
                    assignImposters(game);
                }
                game.onlinePlayersWithoutSpectators().forEach(playerTeamEntry -> {
                    Player player = playerTeamEntry.getKey();
                    Team team = playerTeamEntry.getValue();
                    String title = "";
                    switch (counter) {
                        case 0:
                            title = ChatColor.GREEN + "3";
                            break;
                        case 1:
                            title = ChatColor.YELLOW + "2";
                            break;
                        case 2:
                            title = ChatColor.RED + "1";
                            break;
                        case 3:
                            title = ChatColor.YELLOW + "You are...";
                            break;
                        case 4:
                            if (imposters.get(team).contains(Warlords.getPlayer(player))) {
                                title = ChatColor.RED + "The IMPOSTER";
                                Party.sendMessageToPlayer(player, ChatColor.RED + "You are the IMPOSTER", true, true);
                            } else {
                                title = ChatColor.GREEN + "INNOCENT";
                                Party.sendMessageToPlayer(player, ChatColor.GREEN + "You are INNOCENT", true, true);
                            }
                            break;
                    }
                    PacketUtils.sendTitle(player, title, "", 0, 100, 40);
                });
                counter++;
                if (counter == 5) {
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 20 * 12, 20);
    }

    private void assignImposters(Game game) {
        for (Team team : TeamMarker.getTeams(game)) {
            List<WarlordsPlayer> teamPlayers = game.warlordsPlayers().filter(warlordsPlayer -> warlordsPlayer.getTeam() == team).collect(Collectors.toList());
            if (teamPlayers.size() == 0) {
                continue;
            }
            for (int i = 0; i < numberOfImpostersPerTeam; i++) {
                imposters.get(team).add(teamPlayers.get(new Random().nextInt(teamPlayers.size())));
            }
        }
        System.out.println(" --- Assigned Imposters --- ");
        for (Team team : TeamMarker.getTeams(game)) {
            System.out.println(team.name + " - " + imposters.get(team).stream().map(WarlordsPlayer::getName).collect(Collectors.joining(", ")));
        }
    }

    public void sendPoll(Team team) {
        poll = new PollBuilder()
                .setQuestion("Who is the most SUS on your team?")
                .setTimeLeft(60)
                .setOptions(game.offlinePlayersWithoutSpectators()
                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() == team)
                        .map(offlinePlayerTeamEntry -> offlinePlayerTeamEntry.getKey().getName())
                        .collect(Collectors.toList()))
                .setExcludedPlayers(game.offlinePlayersWithoutSpectators()
                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() != team)
                        .map(offlinePlayerTeamEntry -> offlinePlayerTeamEntry.getKey().getUniqueId())
                        .collect(Collectors.toList()))
                .setRunnableAfterPollEnded(p -> {
                    int mostVotes = Collections.max(p.getOptionsWithVotes().entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getValue();
                    List<WarlordsPlayer> votedOut = p.getOptionsWithVotes().entrySet().stream()
                            .filter(stringIntegerEntry -> stringIntegerEntry.getValue() == mostVotes)
                            .map(Map.Entry::getKey)
                            .map(s -> game.warlordsPlayers()
                                    .filter(warlordsPlayer -> warlordsPlayer.getName().equals(s))
                                    .findFirst()
                                    .get())
                            .collect(Collectors.toList());
                    //If multiple top votes and one is imposter then voted wrong
                    boolean votedCorrectly = votedOut.size() == 1 && imposters.get(team).stream().anyMatch(warlordsPlayer -> warlordsPlayer == votedOut.get(0));

                    new BukkitRunnable() {
                        int counter = 0;

                        @Override
                        public void run() {
                            String title = "";
                            String subtitle = "";
                            switch (counter) {
                                case 0:
                                case 1:
                                    title = team.teamColor + team.name + " voted...";
                                    break;
                                case 2:
                                case 3:
                                    if (votedCorrectly) {
                                        title = ChatColor.GREEN + "Correctly!";
                                    } else {
                                        title = ChatColor.RED + "Incorrectly!";
                                    }
                                    subtitle = team.teamColor + imposters.get(team).get(0).getName() + ChatColor.YELLOW + " was the imposter";
                                    break;
                            }
                            counter++;
                            if (counter < 6) {
                                sendTitle(title, subtitle);
                            } else if (counter == 6) {
                                game.onlinePlayersWithoutSpectators()
                                        .forEach(playerTeamEntry -> {
                                            Player player = playerTeamEntry.getKey();
                                            player.removePotionEffect(PotionEffectType.BLINDNESS);
                                            showWinLossMessage(team, player, votedCorrectly, playerTeamEntry.getValue() == team);
                                        });
                            } else if (counter == 9) {
                                game.removeFrozenCause(team.teamColor + team.name + ChatColor.GREEN + " is voting!");
                                if (votedCorrectly) {
                                    int scoreNeededToEndGame = game.getOptions()
                                            .stream()
                                            .filter(e -> e instanceof WinByPointsOption)
                                            .mapToInt(e -> ((WinByPointsOption) e).getPointLimit())
                                            .sorted()
                                            .findFirst()
                                            .orElse(Integer.MAX_VALUE);
                                    game.setPoints(team, scoreNeededToEndGame);
                                } else {
                                    game.setPoints(team, 0);
                                }
                                ((PlayingState) game.getState()).skipTimer();
                                this.cancel();
                                game.onlinePlayers()
                                        .forEach(playerTeamEntry -> {
                                            Player player = playerTeamEntry.getKey();
                                            sendImpostorResult(player);
                                        });
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 10, 20);
                }).get();
    }

    private void showWinLossMessage(Team team, Player player, boolean votedCorrectly, boolean sameTeam) {
        if (sameTeam) {
            //win if
            //voted imposter out and player isnt the imposter
            //or didnt vote imposter and player is the imposter
            if (
                    (votedCorrectly && imposters.get(team).stream().noneMatch(warlordsPlayer -> warlordsPlayer.getName().equalsIgnoreCase(player.getName()))) ||
                            (!votedCorrectly && imposters.get(team).stream().anyMatch(warlordsPlayer -> warlordsPlayer.getName().equalsIgnoreCase(player.getName())))
            ) {
                PacketUtils.sendTitle(player, ChatColor.GREEN + "YOU WON!", "", 0, 300, 40);
                Party.sendMessageToPlayer(player, ChatColor.GREEN + "You won!", true, true);
            } else {
                PacketUtils.sendTitle(player, ChatColor.RED + "YOU LOST!", "", 0, 300, 40);
                Party.sendMessageToPlayer(player, ChatColor.RED + "You lost!", true, true);
            }
        } else {
            //win if
            //other team votes wrong imposter
            //or other team votes the right imposter then the imposter wins
            boolean isAnImposterOnOtherTeam = false;
            for (Map.Entry<Team, List<WarlordsPlayer>> teamListEntry : imposters.entrySet().stream()
                    .filter(teamListEntry -> teamListEntry.getKey() != team)
                    .collect(Collectors.toList())
            ) {
                for (WarlordsPlayer warlordsPlayer : teamListEntry.getValue()) {
                    if (warlordsPlayer.getName().equalsIgnoreCase(player.getName())) {
                        isAnImposterOnOtherTeam = true;
                        break;
                    }
                }
            }
            if (votedCorrectly && !isAnImposterOnOtherTeam) {
                PacketUtils.sendTitle(player, ChatColor.RED + "YOU LOST!", "", 0, 300, 40);
                Party.sendMessageToPlayer(player, ChatColor.RED + "You lost!", true, true);
            } else {
                PacketUtils.sendTitle(player, ChatColor.GREEN + "YOU WON!", "", 0, 300, 40);
                Party.sendMessageToPlayer(player, ChatColor.GREEN + "You won!", true, true);
            }
        }
    }

    private void sendImpostorResult(Player player) {
        StringBuilder message = new StringBuilder();
        imposters.forEach((team, warlordsPlayers) -> {
            if (warlordsPlayers.size() == 1) {
                message.append(ChatColor.GREEN + "The ")
                        .append(team.teamColor).append(team.name)
                        .append(ChatColor.GREEN).append(" imposter was ")
                        .append(ChatColor.AQUA).append(warlordsPlayers.stream().map(WarlordsPlayer::getName).collect(Collectors.joining(", ")))
                        .append("\n");
            } else if (warlordsPlayers.size() > 1) {
                message.append(ChatColor.GREEN + "The ")
                        .append(team.teamColor).append(team.name)
                        .append(ChatColor.GREEN).append(" imposters were ")
                        .append(ChatColor.AQUA).append(warlordsPlayers.stream().map(WarlordsPlayer::getName).collect(Collectors.joining(", ")))
                        .append("\n");
            }
        });
        message.setLength(message.length() - 1);
        Party.sendMessageToPlayer(
                player,
                message.toString(),
                true,
                true
        );
    }

    private void sendTitle(String title, String subtitle) {
        game.onlinePlayers().forEach(playerTeamEntry -> PacketUtils.sendTitle(playerTeamEntry.getKey(), title, subtitle, 0, 150, 40));
    }

    public int getNumberOfImpostersPerTeam() {
        return numberOfImpostersPerTeam;
    }

    public HashMap<Team, List<WarlordsPlayer>> getImposters() {
        return imposters;
    }

    public HashMap<Team, List<WarlordsPlayer>> getVoters() {
        return voters;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }
}
