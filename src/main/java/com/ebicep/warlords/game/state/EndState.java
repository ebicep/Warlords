package com.ebicep.warlords.game.state;

import com.ebicep.warlords.commands.debugcommands.misc.GetPlayerLastAbilityStatsCommand;
import com.ebicep.warlords.commands.miscellaneouscommands.StreamChaptersCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.EventGameEndOption;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PlayerPveRewards;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildExperienceUtils;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.MinuteStats;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class EndState implements State, TimerDebugAble {
    @Nonnull
    private final Game game;
    private final WarlordsGameTriggerWinEvent winEvent;
    private int timer;

    private AtomicBoolean gameAdded = new AtomicBoolean(false);

    public EndState(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent event) {
        this.game = game;
        this.winEvent = event;
    }

    public EndState(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent event, AtomicBoolean gameAdded) {
        this.game = game;
        this.winEvent = event;
        this.gameAdded = gameAdded;
    }

    @Override
    public void begin() {
        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Game " + game.getGameId() + " has ended");
        if (game.getGameMode() == com.ebicep.warlords.game.GameMode.TUTORIAL) {
            game.warlordsPlayers().forEach(warlordsPlayer -> {
                DatabaseManager.updatePlayer(warlordsPlayer.getUuid(), databasePlayer -> {
                    databasePlayer.getPveStats().setCompletedTutorial(true);
                });
            });
            return;
        }
        WarlordsPlayer.STUNNED_PLAYERS.removeAll(game.getPlayers().keySet());

        List<Option> options = game.getOptions();
        for (Option option : options) {
            option.onGameEnding(game);
        }

        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Game options onGameEnding done");

        boolean teamBlueWins = winEvent != null && winEvent.getDeclaredWinner() == Team.BLUE;
        boolean teamRedWins = winEvent != null && winEvent.getDeclaredWinner() == Team.RED;
        List<WarlordsPlayer> players = game.warlordsPlayers().toList();
        if (players.isEmpty()) {
            return;
        }
        sendGlobalMessage(game,
                Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.GREEN, TextDecoration.BOLD),
                true
        );
        sendGlobalMessage(game, Component.text("Warlords 2.0", NamedTextColor.WHITE, TextDecoration.BOLD), true);
        sendGlobalMessage(game, Component.empty(), false);
        if (teamBlueWins) {
            if (com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
                sendGlobalMessage(game, Component.text("Winner", NamedTextColor.YELLOW)
                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                 .append(Component.text("PLAYERS", NamedTextColor.BLUE)), true);
            } else {
                sendGlobalMessage(game, Component.text("Winner", NamedTextColor.YELLOW)
                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                 .append(Component.text("BLU", NamedTextColor.BLUE)), true);
            }
        } else if (teamRedWins) {
            if (com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
                sendGlobalMessage(game, Component.text("Winner", NamedTextColor.YELLOW)
                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                 .append(Component.text("MONSTERS", NamedTextColor.RED)), true);
            } else {
                sendGlobalMessage(game, Component.text("Winner", NamedTextColor.YELLOW)
                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                 .append(Component.text("RED", NamedTextColor.RED)), true);
            }
        } else {
            if (options.stream().anyMatch(EventGameEndOption.class::isInstance)) {
                sendGlobalMessage(game, Component.text("Winner", NamedTextColor.YELLOW)
                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                 .append(Component.text("GAME END", NamedTextColor.LIGHT_PURPLE)), true);
            } else {
                sendGlobalMessage(game, Component.text("Winner", NamedTextColor.YELLOW)
                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                 .append(Component.text("DRAW", NamedTextColor.LIGHT_PURPLE)), true);
            }
        }

        switch (game.getGameMode()) {
            case WAVE_DEFENSE:
            case EVENT_WAVE_DEFENSE:
            case ONSLAUGHT:
                for (Option option : options) {
                    if (option instanceof PveOption) {
                        showWaveDefenseStats((PveOption) option, players);
                        break;
                    }
                }
            case INTERCEPTION:
            case SIMULATION_TRIAL:
            case TEAM_DEATHMATCH:
            case PAYLOAD:
            case SIEGE:
                showTopDamage(players);
                showTopHealing(players);
                break;
            case CAPTURE_THE_FLAG:
                showFlagCaptures(players);
                showTopDamage(players);
                showTopHealing(players);
                break;
            default:
                sendGlobalMessage(game, Component.empty(), false);
                break;
        }

        //PLAYER STATS

        sendGlobalMessage(game, Component.empty(), false);
        int totalKills = players
                .stream()
                .mapToInt(wp -> wp.getMinuteStats().total().getKills())
                .sum();
        int totalAssists = players.stream().mapToInt(wp -> wp.getMinuteStats().total().getAssists()).sum();
        int totalDeaths = players.stream().mapToInt(wp -> wp.getMinuteStats().total().getDeaths()).sum();

        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }
            ChatUtils.sendCenteredMessage(player,
                    Component.text("✚ YOUR STATISTICS ✚", NamedTextColor.GOLD, TextDecoration.BOLD)
                             .hoverEvent(HoverEvent.showText(
                                     Component.text("Total Kills (everyone): ", NamedTextColor.WHITE)
                                              .append(Component.text(NumberFormat.addCommaAndRound(totalKills), NamedTextColor.GREEN))
                                              .append(Component.newline())
                                              .append(Component.text("Total Assists (everyone): ", NamedTextColor.WHITE))
                                              .append(Component.text(NumberFormat.addCommaAndRound(totalAssists), NamedTextColor.GREEN))
                                              .append(Component.newline())
                                              .append(Component.text("Total Deaths (everyone): ", NamedTextColor.WHITE))
                                              .append(Component.text(NumberFormat.addCommaAndRound(totalDeaths), NamedTextColor.GREEN))
                                              .append(Component.newline())
                                              .append(Component.text("Total Melee Hits (you): ", NamedTextColor.WHITE))
                                              .append(Component.text(NumberFormat.addCommaAndRound(wp.getMinuteStats()
                                                                                                     .total()
                                                                                                     .getMeleeHits()),
                                                      NamedTextColor.GREEN
                                              ))))
            );

            boolean hoverable = !com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode());
            ChatUtils.sendCenteredMessage(player,
                    wp.getAllMinuteHoverableStats(MinuteStats.KILLS, hoverable)
                      .append(ChatUtils.SPACER)
                      .append(wp.getAllMinuteHoverableStats(MinuteStats.ASSISTS, hoverable))
                      .append(ChatUtils.SPACER)
                      .append(wp.getAllMinuteHoverableStats(MinuteStats.DEATHS, hoverable))
            );
            ChatUtils.sendCenteredMessage(player,
                    wp.getAllMinuteHoverableStats(MinuteStats.DAMAGE, hoverable)
                      .append(ChatUtils.SPACER)
                      .append(wp.getAllMinuteHoverableStats(MinuteStats.HEALING, hoverable))
                      .append(ChatUtils.SPACER)
                      .append(wp.getAllMinuteHoverableStats(MinuteStats.ABSORBED, hoverable))
            );

            ChatUtils.sendCenteredMessage(player, Component.empty());

            //ABILITY INFO
            GetPlayerLastAbilityStatsCommand.PLAYER_LAST_ABILITY_STATS.put(player.getUniqueId(), wp.getSpec().getFormattedData());
            GetPlayerLastAbilityStatsCommand.sendLastAbilityStats(player, player.getUniqueId());

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);

            if (!game.getAddons().contains(GameAddon.IMPOSTER_MODE) && options.stream().noneMatch(option -> option instanceof EventGameEndOption)) {
                if (winEvent == null || winEvent.getDeclaredWinner() == null) {
                    player.playSound(player.getLocation(), "defeat", 500, 1);
                    player.showTitle(Title.title(
                            Component.text("DRAW", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD),
                            Component.empty(),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(100), Ticks.duration(0))
                    ));
                } else if (wp.getTeam() == winEvent.getDeclaredWinner()) {
                    player.playSound(player.getLocation(), "victory", 500, 1);
                    player.showTitle(Title.title(
                            Component.text("VICTORY", NamedTextColor.GOLD, TextDecoration.BOLD),
                            Component.empty(),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(100), Ticks.duration(0))
                    ));
                } else {
                    player.playSound(player.getLocation(), "defeat", 500, 1);
                    player.showTitle(Title.title(
                            Component.text("DEFEAT", NamedTextColor.RED, TextDecoration.BOLD),
                            Component.empty(),
                            Title.Times.times(Ticks.duration(0), Ticks.duration(100), Ticks.duration(0))
                    ));
                }
            }
        }

        this.resetTimer();

        //EXPERIENCE
        ChatUtils.MessageType.WARLORDS.sendMessage("Game Added = " + gameAdded);
        if (gameAdded.get() && DatabaseManager.playerService != null) {
            sendGlobalMessage(game,
                    Component.text(" ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.GREEN, TextDecoration.BOLD),
                    true
            );
            sendGlobalMessage(game, Component.empty(), false);
            sendGlobalMessage(game,
                    Component.text(" ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.GREEN, TextDecoration.BOLD),
                    true
            );
            sendGlobalMessage(game, Component.empty(), false);
            showExperienceSummary(players);
            for (Option option : options) {
                if (option instanceof PveOption pveOption) {
                    showCoinSummary(pveOption, players);
                    showDropsSummary(pveOption, players);
//                    showQuestSummary(pveOption, players);
                    break;
                }
            }
            sendGlobalMessage(game, Component.empty(), false);
            sendGlobalMessage(game,
                    Component.text(" ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.GREEN, TextDecoration.BOLD),
                    true
            );
            if (game.getGameMode() == com.ebicep.warlords.game.GameMode.EVENT_WAVE_DEFENSE) {
                sendGlobalMessage(game, Component.empty(), false);
                sendGlobalMessage(game,
                        Component.text(" ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.GREEN, TextDecoration.BOLD),
                        true
                );
                showEventStats(players);
            }
        }

        sendGlobalMessage(game,
                Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.GREEN, TextDecoration.BOLD),
                true
        );

        if (winEvent != null) {
            this.game.forEachOfflineWarlordsPlayer(wp -> {
                if (StreamChaptersCommand.GAME_TIMES.containsKey(wp.getUuid())) {
                    List<StreamChaptersCommand.GameTime> gameTimes = StreamChaptersCommand.GAME_TIMES.get(wp.getUuid());
                    gameTimes.get(gameTimes.size() - 1).setEnd(Instant.now());
                }
            });
        }
    }

    @Override
    public State run() {
        timer--;
        if (timer <= 0) {
            return new ClosedState(game);
        }
        return null;
    }

    @Override
    public void end() {
        game.removeAllPlayers();
    }

    @Override
    public int getTicksElapsed() {
        return 0;
    }

    private void sendGlobalMessage(Game game, Component message, boolean centered) {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
            if (centered) {
                ChatUtils.sendCenteredMessage(p, message);
            } else {
                p.sendMessage(message);
            }
        });
    }

    private void showWaveDefenseStats(PveOption pveOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, Component.empty(), false);
        TextComponent.Builder hover = Component.empty().toBuilder();
        if (pveOption instanceof WaveDefenseOption waveDefenseOption) {
            hover.append(Component.text("Waves Cleared", NamedTextColor.WHITE))
                 .append(Component.text(": ", NamedTextColor.GRAY))
                 .append(Component.text(waveDefenseOption.getWavesCleared(), NamedTextColor.GREEN))
                 .append(Component.newline());
        }
        game.getOptions()
            .stream()
            .filter(option -> option instanceof RecordTimeElapsedOption)
            .map(RecordTimeElapsedOption.class::cast)
            .findAny()
            .ifPresent(recordTimeElapsedOption -> {
                hover.append(Component.text("Time Elapsed", NamedTextColor.WHITE))
                     .append(Component.text(": ", NamedTextColor.GRAY))
                     .append(Component.text(StringUtils.formatTimeLeft(recordTimeElapsedOption.getTicksElapsed() / 20), NamedTextColor.GREEN));
            });
        sendGlobalEventMessage(game, Component.text("✚ GAME STATS ✚", NamedTextColor.BLUE, TextDecoration.BOLD)
                                              .hoverEvent(HoverEvent.showText(hover.build())));
    }

    private void showTopDamage(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, Component.empty(), false);
        sendGlobalEventMessage(game,
                Component.text("✚ TOP DAMAGE ✚", NamedTextColor.RED, TextDecoration.BOLD)
                         .hoverEvent(HoverEvent.showText(
                                 Component.text("Total Damage (everyone)", NamedTextColor.RED)
                                          .append(Component.text(": ", NamedTextColor.GRAY))
                                          .append(Component.text(NumberFormat.addCommaAndRound(players.stream()
                                                                                                      .mapToLong(wp -> wp.getMinuteStats().total().getDamage())
                                                                                                      .sum()), NamedTextColor.GOLD))
                         ))
        );
        players = players.stream()
                         .sorted(Comparator.comparing((WarlordsEntity wp) -> wp.getMinuteStats().total().getDamage()).reversed())
                         .toList();
        TextComponent.Builder leaderboardPlayersDamage = Component.empty().toBuilder();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsEntity we = players.get(i);
            leaderboardPlayersDamage.append(
                    Component.text(we.getName(), NamedTextColor.AQUA)
                             .append(Component.text(": ", NamedTextColor.GRAY))
                             .append(Component.text(NumberFormat.getSimplifiedNumber(we.getMinuteStats().total().getDamage()), NamedTextColor.GOLD))
                             .hoverEvent(HoverEvent.showText(
                                     Component.text("Lv", NamedTextColor.DARK_GRAY)
                                              .append(Component.text(ExperienceManager.getLevelForSpec(we.getUuid(), we.getSpecClass()) + " ",
                                                      NamedTextColor.GRAY
                                              ))
                                              .append(Component.text(we.getSpec().getClassName(), NamedTextColor.GOLD))
                                              .append(Component.text(" (" + we.getSpec().getClass().getSimpleName() + ")",
                                                      NamedTextColor.GREEN
                                              ))

                             )));

            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersDamage.append(ChatUtils.SPACER);
            }
        }
        sendGlobalEventMessage(game, leaderboardPlayersDamage.build());
    }

    private void showTopHealing(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, Component.empty(), false);
        sendGlobalEventMessage(game,
                Component.text("✚ TOP HEALING ✚", NamedTextColor.GREEN, TextDecoration.BOLD)
                         .hoverEvent(HoverEvent.showText(
                                 Component.text("Total Healing (everyone)", NamedTextColor.GREEN)
                                          .append(Component.text(": ", NamedTextColor.GRAY))
                                          .append(Component.text(NumberFormat.addCommaAndRound(players.stream()
                                                                                                      .mapToLong(wp -> wp.getMinuteStats().total().getHealing())
                                                                                                      .sum()), NamedTextColor.GOLD))
                         ))
        );
        players = players.stream()
                         .sorted(Comparator.comparing((WarlordsEntity wp) -> wp.getMinuteStats().total().getHealing()).reversed())
                         .toList();
        TextComponent.Builder leaderboardPlayersHealing = Component.empty().toBuilder();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsEntity we = players.get(i);
            leaderboardPlayersHealing.append(
                    Component.text(we.getName(), NamedTextColor.AQUA)
                             .append(Component.text(": ", NamedTextColor.GRAY))
                             .append(Component.text(NumberFormat.getSimplifiedNumber(we.getMinuteStats().total().getHealing()), NamedTextColor.GOLD))
                             .hoverEvent(HoverEvent.showText(
                                     Component.text("Lv", NamedTextColor.DARK_GRAY)
                                              .append(Component.text(ExperienceManager.getLevelForSpec(we.getUuid(), we.getSpecClass()) + " ",
                                                      NamedTextColor.GRAY
                                              ))
                                              .append(Component.text(we.getSpec().getClassName(), NamedTextColor.GOLD))
                                              .append(Component.text(" (" + we.getSpec().getClass().getSimpleName() + ")",
                                                      NamedTextColor.GREEN
                                              ))
                             )));

            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersHealing.append(ChatUtils.SPACER);
            }
        }
        sendGlobalEventMessage(game, leaderboardPlayersHealing.build());
    }

    /**
     * @param players player collection to give the capture message module to.
     */
    private void showFlagCaptures(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, Component.empty(), false);
        sendGlobalEventMessage(game,
                Component.text("✚ MVP ✚", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)
                         .hoverEvent(HoverEvent.showText(
                                 Component.text("Total Flag Captures (everyone): ", NamedTextColor.LIGHT_PURPLE)
                                          .append(Component.text(NumberFormat.addCommaAndRound(players.stream()
                                                                                                      .mapToInt(WarlordsEntity::getFlagsCaptured)
                                                                                                      .sum()), NamedTextColor.GOLD))
                                          .append(Component.newline())
                                          .append(Component.text("Total Flag Returns (everyone): ", NamedTextColor.LIGHT_PURPLE))
                                          .append(Component.text(NumberFormat.addCommaAndRound(players.stream()
                                                                                                      .mapToInt(WarlordsEntity::getFlagsReturned)
                                                                                                      .sum()), NamedTextColor.GOLD))

                         ))
        );
        players = players.stream()
                         .sorted(Comparator.comparing(WarlordsEntity::getTotalCapsAndReturnsWeighted).reversed())
                         .toList();
        WarlordsPlayer topPlayer = players.get(0);
        sendGlobalEventMessage(game,
                Component.text(topPlayer.getName(), NamedTextColor.AQUA)
                         .hoverEvent(HoverEvent.showText(
                                 Component.text("Flag Captures: ", NamedTextColor.LIGHT_PURPLE)
                                          .append(Component.text(topPlayer.getFlagsCaptured(), NamedTextColor.GOLD))
                                          .append(Component.newline())
                                          .append(Component.text("Flag Returns: ", NamedTextColor.LIGHT_PURPLE))
                                          .append(Component.text(topPlayer.getFlagsReturned(), NamedTextColor.GOLD))
                         ))
        );
    }

    private void showExperienceSummary(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, Component.text("✚ EXPERIENCE SUMMARY ✚", NamedTextColor.YELLOW, TextDecoration.BOLD), true);
        for (WarlordsPlayer wp : players) {
            UUID uuid = wp.getUuid();
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }

            ExperienceManager.ExperienceSummary expSummary = ExperienceManager.getExpFromGameStats(wp, false);
            for (Specializations spec : wp.getSpecMinuteStats().keySet()) {
                long experienceOnSpec = ExperienceManager.getExperienceForSpec(uuid, spec);
                long experienceEarnedSpec = expSummary.getSpecExpGain(spec);
                ChatUtils.sendCenteredMessage(player,
                        Component.text("+", NamedTextColor.GRAY)
                                 .append(Component.text(NumberFormat.addCommaAndRound(experienceEarnedSpec), NamedTextColor.DARK_GREEN))
                                 .append(Component.text(" " + Specializations.getClass(spec).name + " Experience", NamedTextColor.GOLD))
                                 .append(Component.text(" (", NamedTextColor.GRAY))
                                 .append(Component.text(spec.name, spec.specType.getTextColor()))
                                 .append(Component.text(")", NamedTextColor.GRAY))
                                 .hoverEvent(HoverEvent.showText(expSummary.getSpecSummary(spec)))
                );
                ExperienceManager.giveLevelUpMessage(player, experienceOnSpec - experienceEarnedSpec, experienceOnSpec);
            }
            long experienceUniversal = ExperienceManager.getUniversalLevel(uuid);
            long experienceEarnedUniversal = expSummary.getUniversalExpGain();
            ChatUtils.sendCenteredMessage(player,
                    Component.text("+", NamedTextColor.DARK_GRAY)
                             .append(Component.text(NumberFormat.addCommaAndRound(experienceEarnedUniversal), NamedTextColor.DARK_AQUA))
                             .append(Component.text(" Universal Experience", NamedTextColor.GOLD))
                             .hoverEvent(HoverEvent.showText(expSummary.getUniversalSummary()))
            );
            ExperienceManager.giveLevelUpMessage(player, experienceUniversal - experienceEarnedUniversal, experienceUniversal);

            ExperienceManager.CACHED_PLAYER_EXP_SUMMARY.remove(uuid);

            LinkedHashMap<String, Long> expFromPvE = GuildExperienceUtils.getExpFromPvE(wp, null, false);
            if (expFromPvE.size() > 0) {
                TextComponent.Builder expFromPvESummary = Component.empty().toBuilder();
                int counter = 0;
                for (Map.Entry<String, Long> entry : expFromPvE.entrySet()) {
                    String s = entry.getKey();
                    Long aLong = entry.getValue();
                    expFromPvESummary.append(Component.text(s, NamedTextColor.AQUA))
                                     .append(Component.text(": ", NamedTextColor.WHITE))
                                     .append(Component.text("+", NamedTextColor.DARK_GRAY))
                                     .append(Component.text(aLong, NamedTextColor.DARK_GREEN));
                    if (counter != expFromPvE.size() - 1) {
                        expFromPvESummary.append(Component.newline());
                    }
                    counter++;
                }

                ChatUtils.sendCenteredMessage(player,
                        Component.text("+", NamedTextColor.DARK_GRAY)
                                 .append(Component.text(NumberFormat.addCommaAndRound(expFromPvE.values().stream().mapToLong(Long::longValue).sum()),
                                         NamedTextColor.GREEN
                                 ))
                                 .append(Component.text(" Guild Experience", NamedTextColor.DARK_GREEN))
                                 .hoverEvent(HoverEvent.showText(expFromPvESummary.build()))
                );
            }
        }
    }

    private void showCoinSummary(PveOption pveOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, Component.empty(), false);
        sendGlobalMessage(game, Component.text("✚ COINS SUMMARY ✚", NamedTextColor.DARK_AQUA, TextDecoration.BOLD), true);

        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }
            Currencies.PvECoinSummary pvECoinSummary = Currencies.getCoinGainFromGameStats(wp, pveOption, false);

            TextComponent.Builder coinSummaryString = Component.empty().toBuilder();
            int counter = 0;
            for (Map.Entry<String, Long> entry : pvECoinSummary.getCoinSummary().entrySet()) {
                String s = entry.getKey();
                Long aLong = entry.getValue();
                coinSummaryString.append(Component.text(s, NamedTextColor.AQUA))
                                 .append(Component.text(": ", NamedTextColor.WHITE))
                                 .append(Component.text("+", NamedTextColor.DARK_GRAY))
                                 .append(Component.text(aLong, NamedTextColor.GOLD));
                if (counter != pvECoinSummary.getCoinSummary().size() - 1) {
                    coinSummaryString.append(Component.newline());
                }
            }

            ChatUtils.sendCenteredMessage(player,
                    Component.text("+", NamedTextColor.GRAY)
                             .append(Component.text(NumberFormat.addCommaAndRound(pvECoinSummary.getTotalCoinsGained()), NamedTextColor.YELLOW))
                             .append(Component.text(" Coins", NamedTextColor.GOLD))
                             .hoverEvent(HoverEvent.showText(coinSummaryString.build()))
            );

            Pair<Guild, GuildPlayer> guildGuildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player.getUniqueId());
            if (guildGuildPlayerPair != null) {
                ChatUtils.sendCenteredMessage(player,
                        Component.text("+", NamedTextColor.GRAY)
                                 .append(Component.text(NumberFormat.addCommaAndRound(pvECoinSummary.getTotalGuildCoinsGained()), NamedTextColor.YELLOW))
                                 .append(Component.text(" Guild Coins", NamedTextColor.GOLD))
                );
            }
        }
    }

    private void showDropsSummary(PveOption pveOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, Component.empty(), false);
        sendGlobalMessage(game, Component.text("✚ DROPS SUMMARY ✚", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD), true);


        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }
            PlayerPveRewards playerPveRewards = pveOption.getRewards().getPlayerRewards(wp.getUuid());
            boolean gotAnyDrops = false;
            long illusionShardGain = playerPveRewards.getIllusionShardGain();
            if (illusionShardGain > 0) {
                gotAnyDrops = true;
                ChatUtils.sendCenteredMessage(player,
                        Component.text("+", NamedTextColor.GRAY)
                                 .append(Component.text(NumberFormat.addCommaAndRound(illusionShardGain) + " ", NamedTextColor.GREEN))
                                 .append(Currencies.ILLUSION_SHARD.getColoredName().append(Component.text(illusionShardGain == 1 ? "" : "s")))
                );
            }
            List<AbstractWeapon> weaponsFound = playerPveRewards.getWeaponsFound();
            if (!weaponsFound.isEmpty()) {
                if (gotAnyDrops) {
                    ChatUtils.sendCenteredMessage(player, Component.empty());
                }
                gotAnyDrops = true;
                LinkedHashMap<WeaponsPvE, List<AbstractWeapon>> weaponsFoundByType = new LinkedHashMap<>();
                for (WeaponsPvE rarity : WeaponsPvE.VALUES) {
                    weaponsFoundByType.put(rarity, new ArrayList<>());
                }
                for (AbstractWeapon weapon : weaponsFound) {
                    weaponsFoundByType.get(weapon.getRarity()).add(weapon);
                }
                DatabaseManager.getPlayer(wp.getUuid(), databasePlayer -> {
                    List<AbstractWeapon> weaponInventory = databasePlayer.getPveStats().getWeaponInventory();
                    weaponsFoundByType.forEach((rarity, weapons) -> {
                        int amountFound = weapons.size();
                        if (amountFound > 0) {
                            boolean autoSalvaged = weapons.stream().anyMatch(abstractWeapon -> !weaponInventory.contains(abstractWeapon));
                            TextComponent.Builder weaponTypeSummary = Component.empty().toBuilder();
                            for (int i = 0; i < weapons.size(); i++) {
                                AbstractWeapon weapon = weapons.get(i);
                                weaponTypeSummary.append(weapon.getName());
                                if (weapon instanceof WeaponScore) {
                                    weaponTypeSummary.append(Component.text(" (" + NumberFormat.formatOptionalHundredths(((WeaponScore) weapon).getWeaponScore()) + ")",
                                            NamedTextColor.YELLOW
                                    ));
                                }
                                if (!weaponInventory.contains(weapon)) {
                                    weaponTypeSummary.append(Component.text(" (Auto Salvaged)", NamedTextColor.WHITE));
                                }
                                if (i != weapons.size() - 1) {
                                    weaponTypeSummary.append(Component.newline());
                                }
                            }
                            ChatUtils.sendCenteredMessage(player,
                                    Component.text(amountFound + " ", rarity.textColor)
                                             .append(Component.text(rarity.name + " Weapon" + (amountFound == 1 ? "" : "s")))
                                             .append(Component.text(autoSalvaged ? "*" : "", NamedTextColor.WHITE))
                                             .hoverEvent(HoverEvent.showText(weaponTypeSummary.build()))
                            );
                        }
                    });
                });
            }
            long fragmentGain = playerPveRewards.getLegendFragmentGain();
            if (fragmentGain > 0) {
                gotAnyDrops = true;
                ChatUtils.sendCenteredMessage(player,
                        Component.text("+", NamedTextColor.GRAY)
                                 .append(Component.text(NumberFormat.addCommaAndRound(fragmentGain) + " ", NamedTextColor.GREEN))
                                 .append(Currencies.LEGEND_FRAGMENTS.getColoredName().append(Component.text(fragmentGain == 1 ? "" : "s")))
                );
            }
            HashMap<MobDrop, Long> mobDropsGained = playerPveRewards.getMobDropsGained();
            if (!mobDropsGained.isEmpty()) {
                if (gotAnyDrops) {
                    ChatUtils.sendCenteredMessage(player, Component.empty());
                }
                gotAnyDrops = true;
                List<MobDrop> mobDrops = new ArrayList<>(mobDropsGained.keySet());
                mobDrops.sort(Comparator.comparingInt(MobDrop::ordinal));
                for (MobDrop mobDrop : mobDrops) {
                    long amountFound = mobDropsGained.get(mobDrop);
                    ChatUtils.sendCenteredMessage(player, mobDrop.getCostColoredName(amountFound));
                }
            }
            List<AbstractItem> itemsFound = playerPveRewards.getItemsFound();
            if (!itemsFound.isEmpty()) {
                if (gotAnyDrops) {
                    ChatUtils.sendCenteredMessage(player, Component.empty());
                }
                gotAnyDrops = true;
                for (AbstractItem item : itemsFound) {
                    ChatUtils.sendCenteredMessage(player, item.getHoverComponent());
                }
            }
            int blessingsFound = playerPveRewards.getBlessingsFound();
            if (blessingsFound > 0) {
                gotAnyDrops = true;
                ChatUtils.sendCenteredMessage(player,
                        Component.text("+", NamedTextColor.GRAY)
                                 .append(Component.text(NumberFormat.addCommaAndRound(blessingsFound), NamedTextColor.GREEN))
                                 .append(Component.text(" Unknown Blessings", NamedTextColor.GRAY))
                );
            }

            Map<Spendable, Long> syntheticPouch = playerPveRewards.getSyntheticPouch();
            Map<Spendable, Long> aspirantPouch = playerPveRewards.getAspirantPouch();
            if (!syntheticPouch.isEmpty() || !aspirantPouch.isEmpty()) {
                if (gotAnyDrops) {
                    ChatUtils.sendCenteredMessage(player, Component.empty());
                }
                gotAnyDrops = true;
                if (!syntheticPouch.isEmpty()) {
                    ChatUtils.sendCenteredMessage(player,
                            Component.text("Synthetic Pouch", NamedTextColor.AQUA)
                                     .hoverEvent(HoverEvent.showText(getPouchSummary(syntheticPouch)))
                    );
                }
                if (!aspirantPouch.isEmpty()) {
                    ChatUtils.sendCenteredMessage(player,
                            Component.text("Aspirant Pouch", NamedTextColor.AQUA)
                                     .hoverEvent(HoverEvent.showText(getPouchSummary(aspirantPouch)))
                    );
                }
            }

            if (!gotAnyDrops) {
                ChatUtils.sendCenteredMessage(player, Component.text("You did not receive any drops this game!", NamedTextColor.GOLD));
            }
        }
    }

    private static TextComponent getPouchSummary(Map<Spendable, Long> syntheticPouch) {
        TextComponent.Builder pouch = Component.empty().toBuilder();
        List<Map.Entry<Spendable, Long>> toSort = new ArrayList<>(syntheticPouch.entrySet());
        toSort.sort((o1, o2) -> Long.compare(o2.getValue(), o1.getValue()));
        for (int i = 0; i < toSort.size(); i++) {
            Map.Entry<Spendable, Long> entry = toSort.get(i);
            pouch.append(Component.text(" - ", NamedTextColor.GRAY)
                                  .append(entry.getKey().getCostColoredName(entry.getValue())));
            if (i != toSort.size() - 1) {
                pouch.append(Component.newline());
            }
        }
        return pouch.build();
    }


//    private void showQuestSummary(PveOption pveOption, List<WarlordsPlayer> players) {
//        for (WarlordsPlayer wp : players) {
//            Player player = Bukkit.getPlayer(wp.getUuid());
//            if (player == null) {
//                continue;
//            }
//            List<Quests> quests = Quests.getQuestsFromGameStats(wp, pveOption, false);
//            if (!quests.isEmpty()) {
//                player.sendMessage("");
//                ChatUtils.sendCenteredMessage(player, Component.text("✚ QUESTS SUMMARY ✚", NamedTextColor.AQUA, TextDecoration.BOLD));
//            }
//            for (Quests quest : quests) {
//                ChatUtils.sendCenteredMessage(player,
//                        Component.text(quest.name, NamedTextColor.GREEN)
//                                 .hoverEvent(HoverEvent.showText(Component.text(quest.description, NamedTextColor.GREEN)))
//                );
//            }
//        }
//    }

    private void showEventStats(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, Component.empty(), false);
        sendGlobalMessage(game, Component.text("✚ EVENT SUMMARY ✚", NamedTextColor.AQUA, TextDecoration.BOLD), true);
        EventPointsOption eventPointsOption = game
                .getOptions()
                .stream()
                .filter(option -> option instanceof EventPointsOption)
                .map(EventPointsOption.class::cast)
                .findFirst()
                .orElse(null);
        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }

            if (eventPointsOption != null) {
                Integer points = eventPointsOption.getPoints().getOrDefault(player.getUniqueId(), 0);
                String pointsFormatted = NumberFormat.addCommas(Math.min(points, eventPointsOption.getCap()));
                ChatUtils.sendCenteredMessage(player,
                        Component.text("+", NamedTextColor.GRAY)
                                 .append(Component.text(pointsFormatted + " Point" + (points == 1 ? "" : "s"), NamedTextColor.YELLOW))
                );
            }

            for (Option option : game.getOptions()) {
                option.sendEventStatsMessage(game, player);
            }
        }
        sendGlobalMessage(game, Component.empty(), false);
    }

    public void sendGlobalEventMessage(Game game, Component component) {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> ChatUtils.sendCenteredMessage(p, component));
    }

    @Override
    public void skipTimer() throws IllegalStateException {
        this.timer = 0;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = 10 * 20;
    }

    public WarlordsGameTriggerWinEvent getWinEvent() {
        return winEvent;
    }

}
