package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.EventGameEndOption;
import com.ebicep.warlords.game.option.win.WinByPointsOption;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.poll.polls.GamePoll;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ImposterModeOption implements Option, EventGameEndOption {

    public static int NUMBER_OF_IMPOSTERS_PER_TEAM = 1;
    private final HashMap<Team, List<UUID>> imposters = new HashMap<>();
    private final HashMap<Team, List<UUID>> voters = new HashMap<>();
    private Game game;
    private GamePoll poll;
    private boolean forceEnd = false;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;

        for (Team team : TeamMarker.getTeams(game)) {
            imposters.put(team, new ArrayList<>());
        }

        game.registerGameMarker(ScoreboardHandler.class,
                new SimpleScoreboardHandler(Integer.MAX_VALUE - 15, "imposter") {
                    @Nonnull
                    @Override
                    public List<Component> computeLines(@Nullable WarlordsPlayer warlordsPlayer) {
                        if (warlordsPlayer == null) {
                            return Collections.singletonList(Component.empty());
                        }
                        if (imposters.get(warlordsPlayer.getTeam()).isEmpty()) {
                            return Collections.singletonList(Component.empty());
                        }
                        if (imposters.entrySet()
                                     .stream()
                                     .anyMatch(teamListEntry -> teamListEntry.getValue()
                                                                             .contains(warlordsPlayer.getUuid()))) {
                            return Collections.singletonList(
                                    Component.text("Role: ", NamedTextColor.WHITE)
                                             .append(Component.text("Imposter", NamedTextColor.RED))
                            );
                        }
                        return Collections.singletonList(
                                Component.text("Role: ", NamedTextColor.WHITE)
                                         .append(Component.text("Innocent", NamedTextColor.GREEN))
                        );
                    }

                    @Override
                    public boolean emptyLinesBetween() {
                        return false;
                    }
                }
        );
    }

    @Override
    public void start(@Nonnull Game game) {
        assignImpostersWithAnimation(20 * 12);
    }

    @Override
    public void onGameEnding(@Nonnull Game game) {
        if (!forceEnd) {
            game.getState(EndState.class).ifPresent(endState -> {
                WarlordsGameTriggerWinEvent winEvent = endState.getWinEvent();
                if (winEvent == null) {
                    return;
                }
                Team winner = winEvent.getDeclaredWinner();
                if (winner != null) {
                    game.onlinePlayersWithoutSpectators().forEach(playerTeamEntry -> {
                        Player player = playerTeamEntry.getKey();
                        Team team = playerTeamEntry.getValue();
                        //winners - imposter lose
                        //losers - imposter win
                        if (team == winner) {
                            if (imposters.get(team).stream().anyMatch(uuid -> uuid.equals(player.getUniqueId()))) {
                                sendResultMessage(player, NamedTextColor.RED, "YOU LOST!", "You lost!");
                            } else {
                                sendResultMessage(player, NamedTextColor.GREEN, "YOU WON!", "You won!");
                            }
                        } else {
                            boolean isAnImposterOnOtherTeam = false;
                            for (Map.Entry<Team, List<UUID>> teamListEntry : imposters.entrySet().stream()
                                                                                      .filter(teamListEntry -> teamListEntry.getKey() != winner)
                                                                                      .toList()
                            ) {
                                for (UUID uuid : teamListEntry.getValue()) {
                                    if (uuid.equals(player.getUniqueId())) {
                                        isAnImposterOnOtherTeam = true;
                                        break;
                                    }
                                }
                            }
                            if (isAnImposterOnOtherTeam) {
                                sendResultMessage(player, NamedTextColor.GREEN, "YOU WON!", "You won!");
                            } else {
                                sendResultMessage(player, NamedTextColor.RED, "YOU LOST!", "You lost!");
                            }
                        }
                        sendImpostorResult(player);
                    });
                }
            });
        }
    }

    private static void sendResultMessage(Player player, NamedTextColor red, String resultTitle, String resultChat) {
        player.showTitle(Title.title(
                Component.text(resultTitle, red),
                Component.empty(),
                Title.Times.times(Ticks.duration(0), Ticks.duration(300), Ticks.duration(40))
        ));
        ChatUtils.sendMessageToPlayer(player,
                Component.text(resultChat, red),
                NamedTextColor.BLUE,
                true
        );
    }

    private void sendImpostorResult(Player player) {
        TextComponent.Builder message = Component.text().color(NamedTextColor.GREEN);
        int counter = 0;
        for (Map.Entry<Team, List<UUID>> entry : imposters.entrySet()) {
            Team team = entry.getKey();
            List<UUID> warlordsPlayers = entry.getValue();
            message.append(Component.text("The "))
                   .append(Component.text(team.name, team.getTeamColor()))
                   .append(Component.text(warlordsPlayers.size() == 1 ? " imposter was " : " imposters were "))
                   .append(Component.text(warlordsPlayers.stream()
                                                         .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                                                         .collect(Collectors.joining(", ")), NamedTextColor.AQUA))
                   .append(Component.newline());
        }
        sendImposterMessage(player, message.build());
    }

    public void assignImpostersWithAnimation(int tickDelay) {
        new GameRunnable(game) {

            int counter = 0;

            @Override
            public void run() {
                if (counter == 4) {
                    assignImposters();
                }
                game.onlinePlayersWithoutSpectators().forEach(playerTeamEntry -> {
                    Player player = playerTeamEntry.getKey();
                    Team team = playerTeamEntry.getValue();
                    Component title = Component.empty();
                    switch (counter) {
                        case 0 -> title = Component.text("3", NamedTextColor.GREEN);
                        case 1 -> title = Component.text("2", NamedTextColor.YELLOW);
                        case 2 -> title = Component.text("1", NamedTextColor.RED);
                        case 3 -> title = Component.text("You are...", NamedTextColor.YELLOW);
                        case 4 -> {
                            List<UUID> imposterUUIDs = imposters.get(team);
                            if (imposterUUIDs.contains(player.getUniqueId())) {
                                title = Component.text("The IMPOSTER", NamedTextColor.RED);
                                sendImposterMessage(player, Component.text("You are the IMPOSTER", NamedTextColor.RED));
                                if (imposterUUIDs.size() > 1) {
                                    List<UUID> otherImposters = new ArrayList<>(imposterUUIDs);
                                    otherImposters.remove(player.getUniqueId());
                                    ChatUtils.sendMessageToPlayer(player,
                                            Component.text("Other imposters: ", NamedTextColor.GRAY)
                                                     .append(otherImposters.stream()
                                                                           .map(Bukkit::getPlayer)
                                                                           .filter(Objects::nonNull)
                                                                           .map(p -> Component.text(p.getName(), NamedTextColor.RED))
                                                                           .collect(Component.toComponent(Component.text(", ", NamedTextColor.GRAY)))),
                                            NamedTextColor.BLUE,
                                            true
                                    );
                                }
                            } else {
                                title = Component.text("INNOCENT", NamedTextColor.GREEN);
                                sendImposterMessage(player, Component.text("You are INNOCENT", NamedTextColor.GREEN));
                            }
                        }
                    }
                    player.showTitle(Title.title(
                            title,
                            Component.empty(),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(100), Ticks.duration(40))
                    ));
                });
                counter++;
                if (counter == 5) {
                    this.cancel();
                }
            }
        }.runTaskTimer(tickDelay, 20);
    }

    public void assignImposters() {
        imposters.forEach((team, uuids) -> uuids.clear());
        for (Team team : TeamMarker.getTeams(game)) {
            List<WarlordsEntity> teamPlayers = game.warlordsPlayers()
                                                   .filter(warlordsPlayer -> warlordsPlayer.getTeam() == team)
                                                   .collect(Collectors.toList());
            if (teamPlayers.size() == 0) {
                continue;
            }
            Collections.shuffle(teamPlayers);
            for (int i = 0; i < NUMBER_OF_IMPOSTERS_PER_TEAM; i++) {
                imposters.get(team).add(teamPlayers.get(i).getUuid());
            }
        }
        ChatUtils.MessageType.WARLORDS.sendMessage(" --- Assigned Imposters --- ");
        for (Team team : TeamMarker.getTeams(game)) {
            ChatUtils.MessageType.WARLORDS.sendMessage(team.name + " - " + imposters.get(team)
                                                                                    .stream()
                                                                                    .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                                                                                    .collect(Collectors.joining(", ")));
        }
    }

    public static void sendImposterMessage(Player player, Component message) {
        ChatUtils.sendMessageToPlayer(player, message, NamedTextColor.AQUA, true);
    }

    public void sendPoll(Team team) {
        poll = new GamePoll.Builder(game)
                .setQuestion("Who is the most SUS on your team?")
                .setTimeLeft(60)
                .setOptions(game.warlordsPlayers()
                                .filter(warlordsPlayer -> warlordsPlayer.getTeam() == team)
                                .map(warlordsPlayer -> warlordsPlayer.getName() + " - " + warlordsPlayer.getSpecClass().name)
                                .toList())
                .setExcludedPlayers(game.offlinePlayersWithoutSpectators()
                                        .filter(uuidTeamEntry -> uuidTeamEntry.getValue() != team)
                                        .map(offlinePlayerTeamEntry -> offlinePlayerTeamEntry.getKey()
                                                                                             .getUniqueId())
                                        .toList())
                .setRunnableAfterPollEnded(p -> {
                    forceEnd = true;
                    int mostVotes = Collections.max(p.getOptionsWithVotes().entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getValue();
                    List<WarlordsEntity> votedOut = p.getOptionsWithVotes().entrySet().stream()
                                                     .filter(stringIntegerEntry -> stringIntegerEntry.getValue() == mostVotes)
                                                     .map(Map.Entry::getKey)
                                                     .map(s -> game.warlordsPlayers()
                                                                   .filter(warlordsPlayer -> warlordsPlayer.getName().equals(s.substring(0, s.indexOf(" - "))))
                                                                   .findFirst()
                                                                   .get())
                                                     .collect(Collectors.toList());
                    //If multiple top votes and one is imposter then voted wrong
                    boolean votedCorrectly = votedOut.size() == 1 && imposters.get(team)
                                                                              .stream()
                                                                              .anyMatch(uuid -> uuid == votedOut.get(0).getUuid());

                    new GameRunnable(game, true) {
                        int counter = 0;

                        @Override
                        public void run() {
                            Component title = Component.empty();
                            Component subtitle = Component.empty();
                            switch (counter) {
                                case 0, 1 -> title = Component.text(team.name + " voted...", team.getTeamColor());
                                case 2, 3 -> {
                                    if (votedCorrectly) {
                                        title = Component.text("Correctly!", NamedTextColor.GREEN);
                                    } else {
                                        title = Component.text("Incorrectly!", NamedTextColor.RED);
                                    }
                                    subtitle = Component.text(Bukkit.getOfflinePlayer(imposters.get(team).get(0)).getName(), team.getTeamColor())
                                                        .append(Component.text(" was the imposter", NamedTextColor.YELLOW));
                                }
                            }
                            counter++;
                            if (counter < 6) {
                                sendTitle(title, subtitle);
                            } else if (counter == 6) {
                                game.onlinePlayersWithoutSpectators()
                                    .forEach(playerTeamEntry -> {
                                        Player player = playerTeamEntry.getKey();
                                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                                        showWinLossMessage(team,
                                                player,
                                                votedCorrectly,
                                                playerTeamEntry.getValue() == team
                                        );
                                    });
                            } else if (counter == 9) {
                                game.removeFrozenCause(Component.text(team.name, team.getTeamColor())
                                                                .append(Component.text(" is voting!", NamedTextColor.GREEN)));
                                int scoreNeededToEndGame = game.getOptions()
                                                               .stream()
                                                               .filter(e -> e instanceof WinByPointsOption)
                                                               .mapToInt(e -> ((WinByPointsOption) e).getPointLimit())
                                                               .sorted()
                                                               .findFirst()
                                                               .orElse(Integer.MAX_VALUE);
                                if (votedCorrectly) {
                                    game.setPoints(team, scoreNeededToEndGame);
                                } else {
                                    //team with most points win, excluding voting team
                                    game.setPoints(TeamMarker.getTeams(game).stream()
                                                             .filter(t -> t != team)
                                                             .min(Comparator.comparingInt(o -> game.getPoints(o)))
                                                             .get(),
                                            scoreNeededToEndGame
                                    );
                                }
                                ((PlayingState) game.getState()).skipTimer();
                                game.onlinePlayers()
                                    .forEach(playerTeamEntry -> {
                                        Player player = playerTeamEntry.getKey();
                                        sendImpostorResult(player);
                                    });
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(10, 20);
                }).get();
    }

    private void sendTitle(Component title, Component subtitle) {
        game.onlinePlayers()
            .forEach(playerTeamEntry -> playerTeamEntry.getKey().showTitle(Title.title(
                    title,
                    subtitle,
                    Title.Times.times(Ticks.duration(0), Ticks.duration(150), Ticks.duration(40))
            )));
    }

    private void showWinLossMessage(Team team, Player player, boolean votedCorrectly, boolean sameTeam) {
        if (sameTeam) {
            //win if
            //voted imposter out and player isnt the imposter
            //or didnt vote imposter and player is the imposter
            if (
                    (votedCorrectly && imposters.get(team)
                                                .stream()
                                                .noneMatch(uuid -> uuid.equals(player.getUniqueId()))) ||
                            (!votedCorrectly && imposters.get(team)
                                                         .stream()
                                                         .anyMatch(uuid -> uuid.equals(player.getUniqueId())))
            ) {
                sendResultMessage(player, NamedTextColor.GREEN, "YOU WON!", "You won!");
            } else {
                sendResultMessage(player, NamedTextColor.RED, "YOU LOST!", "You lost!");
            }
        } else {
            //win if
            //other team votes wrong imposter
            //or other team votes the right imposter then the imposter wins
            boolean isAnImposterOnOtherTeam = false;
            for (Map.Entry<Team, List<UUID>> teamListEntry : imposters.entrySet().stream()
                                                                      .filter(teamListEntry -> teamListEntry.getKey() != team)
                                                                      .toList()
            ) {
                for (UUID uuid : teamListEntry.getValue()) {
                    if (uuid.equals(player.getUniqueId())) {
                        isAnImposterOnOtherTeam = true;
                        break;
                    }
                }
            }
            if (votedCorrectly && !isAnImposterOnOtherTeam) {
                sendResultMessage(player, NamedTextColor.RED, "YOU LOST!", "You lost!");
            } else {
                sendResultMessage(player, NamedTextColor.GREEN, "YOU WON!", "You won!");
            }
        }
    }

    public HashMap<Team, List<UUID>> getImposters() {
        return imposters;
    }

    public HashMap<Team, List<UUID>> getVoters() {
        return voters;
    }

    public GamePoll getPoll() {
        return poll;
    }

    public void setPoll(GamePoll poll) {
        this.poll = poll;
    }
}
