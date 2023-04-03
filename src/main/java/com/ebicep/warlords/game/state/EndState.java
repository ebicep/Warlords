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
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PlayerPveRewards;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltaroBonanzaOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltarosLairOption;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildExperienceUtils;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.MinuteStats;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.quests.Quests;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.pve.weapons.weaponaddons.WeaponScore;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


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
        ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Game " + game.getGameId() + " has ended");
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

        ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Game options onGameEnding done");

        boolean teamBlueWins = winEvent != null && winEvent.getDeclaredWinner() == Team.BLUE;
        boolean teamRedWins = winEvent != null && winEvent.getDeclaredWinner() == Team.RED;
        List<WarlordsPlayer> players = game.warlordsPlayers().collect(Collectors.toList());
        if (players.isEmpty()) {
            return;
        }
        sendGlobalMessage(game,
                "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                true
        );
        sendGlobalMessage(game, "" + ChatColor.WHITE + ChatColor.BOLD + "  Warlords 2.0", true);
        sendGlobalMessage(game, "", false);
        if (teamBlueWins) {
            if (com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.BLUE + "PLAYERS", true);
            } else {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.BLUE + "BLU", true);
            }
        } else if (teamRedWins) {
            if (com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.RED + "MONSTERS", true);
            } else {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.RED + "RED", true);
            }
        } else {
            if (game.getAddons().contains(GameAddon.IMPOSTER_MODE) ||
                    options.stream().anyMatch(BoltaroBonanzaOption.class::isInstance) ||
                    options.stream().anyMatch(BoltarosLairOption.class::isInstance)
            ) {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "GAME END", true);
            } else {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "DRAW", true);
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
                showTopDamage(players);
                showTopHealing(players);
                break;
            case CAPTURE_THE_FLAG:
                showFlagCaptures(players);
                showTopDamage(players);
                showTopHealing(players);
                break;
            default:
                sendGlobalMessage(game, "", false);
                break;
        }

        //PLAYER STATS

        sendGlobalMessage(game, "", false);
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

            ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
                    .appendHoverText(ChatColor.GOLD.toString() + ChatColor.BOLD + "✚ YOUR STATISTICS ✚",
                            ChatColor.WHITE + "Total Kills (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(totalKills) + "\n" +
                                    ChatColor.WHITE + "Total Assists (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(totalAssists) + "\n" +
                                    ChatColor.WHITE + "Total Deaths (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(totalDeaths) + "\n" +
                                    ChatColor.WHITE + "Total Melee Hits (you): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(wp.getMinuteStats()
                                                                                                                                     .total()
                                                                                                                                     .getMeleeHits())
                    )
                    .create()
            );

            boolean hoverable = !com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode());
            List<BaseComponent> baseComponents = new ArrayList<>(List.of(wp.getAllMinuteHoverableStats(MinuteStats.KILLS, hoverable)));
            baseComponents.add(ChatUtils.SPACER);
            baseComponents.addAll(List.of(wp.getAllMinuteHoverableStats(MinuteStats.ASSISTS, hoverable)));
            baseComponents.add(ChatUtils.SPACER);
            baseComponents.addAll(List.of(wp.getAllMinuteHoverableStats(MinuteStats.DEATHS, hoverable)));
            ChatUtils.sendCenteredMessageWithEvents(player, baseComponents.toArray(new BaseComponent[0]));

            baseComponents.clear();
            baseComponents.addAll(List.of(wp.getAllMinuteHoverableStats(MinuteStats.DAMAGE, hoverable)));
            baseComponents.add(ChatUtils.SPACER);
            baseComponents.addAll(List.of(wp.getAllMinuteHoverableStats(MinuteStats.HEALING, hoverable)));
            baseComponents.add(ChatUtils.SPACER);
            baseComponents.addAll(List.of(wp.getAllMinuteHoverableStats(MinuteStats.ABSORBED, hoverable)));
            ChatUtils.sendCenteredMessageWithEvents(player, baseComponents.toArray(new BaseComponent[0]));

            ChatUtils.sendMessage(player, false, "");

            //ABILITY INFO
            List<BaseComponent> formattedData = new ArrayList<>();
            List<BaseComponent[]> components = wp.getSpec().getFormattedData();
            for (int i = 0; i < components.size() && i < 3; i++) {
                formattedData.addAll(List.of(components.get(i)));
                if (i < 2) {
                    formattedData.add(ChatUtils.SPACER);
                }
            }
            ChatUtils.sendCenteredMessageWithEvents(player, formattedData.toArray(new BaseComponent[0]));
            formattedData.clear();
            for (int i = 3; i < components.size(); i++) {
                formattedData.addAll(List.of(components.get(i)));
                if (i < 4) {
                    formattedData.add(ChatUtils.SPACER);
                }
            }
            ChatUtils.sendCenteredMessageWithEvents(player, formattedData.toArray(new BaseComponent[0]));
            GetPlayerLastAbilityStatsCommand.PLAYER_LAST_ABILITY_STATS.put(player.getUniqueId(), components);

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);

            if (!game.getAddons().contains(GameAddon.IMPOSTER_MODE) &&
                    options.stream().noneMatch(BoltaroBonanzaOption.class::isInstance) &&
                    options.stream().noneMatch(BoltarosLairOption.class::isInstance)
            ) {
                if (winEvent == null || winEvent.getDeclaredWinner() == null) {
                    player.playSound(player.getLocation(), "defeat", 500, 1);
                    PacketUtils.sendTitle(player, "§d§lDRAW", "", 0, 100, 0);
                } else if (wp.getTeam() == winEvent.getDeclaredWinner()) {
                    player.playSound(player.getLocation(), "victory", 500, 1);
                    PacketUtils.sendTitle(player, "§6§lVICTORY!", "", 0, 100, 0);
                } else {
                    player.playSound(player.getLocation(), "defeat", 500, 1);
                    PacketUtils.sendTitle(player, "§c§lDEFEAT", "", 0, 100, 0);
                }
            }
        }

        this.resetTimer();

        //EXPERIENCE
        System.out.println("Game Added = " + gameAdded);
        if (gameAdded.get() && DatabaseManager.playerService != null) {
            sendGlobalMessage(game,
                    "" + ChatColor.GREEN + ChatColor.BOLD + " ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                    true
            );
            sendGlobalMessage(game, "", false);
            sendGlobalMessage(game,
                    "" + ChatColor.GREEN + ChatColor.BOLD + " ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                    true
            );
            sendGlobalMessage(game, "", false);
            showExperienceSummary(players);
            for (Option option : options) {
                if (option instanceof PveOption) {
                    PveOption pveOption = (PveOption) option;
                    showCoinSummary(pveOption, players);
                    showWeaponSummary(pveOption, players);
                    showMobDropSummary(pveOption, players);
                    showQuestSummary(pveOption, players);
                    break;
                }
            }
            sendGlobalMessage(game, "", false);
            sendGlobalMessage(game,
                    "" + ChatColor.GREEN + ChatColor.BOLD + " ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                    true
            );
            if (game.getGameMode() == com.ebicep.warlords.game.GameMode.EVENT_WAVE_DEFENSE) {
                sendGlobalMessage(game, "", false);
                sendGlobalMessage(game,
                        "" + ChatColor.GREEN + ChatColor.BOLD + " ▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                        true
                );
                showEventStats(players);
            }
        }

        sendGlobalMessage(game,
                "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
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

    private void sendGlobalMessage(Game game, String message, boolean centered) {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
            if (centered) {
                ChatUtils.sendCenteredMessage(p, message);
            } else {
                p.sendMessage(message);
            }
        });
    }

    private void showWaveDefenseStats(PveOption pveOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        StringBuilder hover = new StringBuilder();
        if (pveOption instanceof WaveDefenseOption) {
            WaveDefenseOption waveDefenseOption = (WaveDefenseOption) pveOption;
            hover.append(ChatColor.WHITE)
                 .append("Waves Cleared")
                 .append(ChatColor.GRAY)
                 .append(": ")
                 .append(ChatColor.GREEN)
                 .append(waveDefenseOption.getWavesCleared())
                 .append("\n");
        }
        game.getOptions()
            .stream()
            .filter(option -> option instanceof RecordTimeElapsedOption)
            .map(RecordTimeElapsedOption.class::cast)
            .findAny()
            .ifPresent(recordTimeElapsedOption -> {
                hover.append(ChatColor.WHITE).append("Time Elapsed").append(ChatColor.GRAY).append(": ")
                     .append(ChatColor.GREEN).append(Utils.formatTimeLeft(recordTimeElapsedOption.getTicksElapsed() / 20));
            });
        sendGlobalEventMessage(game, new ComponentBuilder()
                .appendHoverText(ChatColor.BLUE.toString() + ChatColor.BOLD + "✚ GAME STATS ✚", hover.toString())
                .create()
        );
    }

    private void showTopDamage(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalEventMessage(game, new ComponentBuilder()
                .appendHoverText(ChatColor.RED.toString() + ChatColor.BOLD + "✚ TOP DAMAGE ✚",
                        ChatColor.RED + "Total Damage (everyone)" +
                                ChatColor.GRAY + ": " +
                                ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream()
                                                                                      .mapToLong(wp -> wp.getMinuteStats().total().getDamage())
                                                                                      .sum())
                )
                .create()
        );
        players = players.stream()
                         .sorted(Comparator.comparing((WarlordsEntity wp) -> wp.getMinuteStats().total().getDamage()).reversed())
                         .collect(Collectors.toList());
        List<BaseComponent> leaderboardPlayersDamage = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsEntity we = players.get(i);
            leaderboardPlayersDamage.addAll(List.of(new ComponentBuilder()
                    .appendHoverText(ChatColor.AQUA + we.getName() +
                                    ChatColor.GRAY + ": " +
                                    ChatColor.GOLD + NumberFormat.getSimplifiedNumber(we.getMinuteStats().total().getDamage()),
                            ChatColor.DARK_GRAY + "Lv" +
                                    ChatColor.GRAY + ExperienceManager.getLevelForSpec(we.getUuid(), we.getSpecClass()) + " " +
                                    ChatColor.GOLD + we.getSpec().getClassName() +
                                    ChatColor.GREEN + " (" +
                                    we.getSpec().getClass().getSimpleName() +
                                    ")"
                    )
                    .create()));

            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersDamage.add(ChatUtils.SPACER);
            }
        }
        sendGlobalEventMessage(game, leaderboardPlayersDamage.toArray(new BaseComponent[0]));
    }

    private void showTopHealing(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalEventMessage(game, new ComponentBuilder()
                .appendHoverText(ChatColor.GREEN.toString() + ChatColor.BOLD + "✚ TOP HEALING ✚",
                        ChatColor.GREEN + "Total Healing (everyone)" +
                                ChatColor.GRAY + ": " +
                                ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream()
                                                                                      .mapToLong(wp -> wp.getMinuteStats().total().getHealing())
                                                                                      .sum())
                )
                .create()
        );
        players = players.stream()
                         .sorted(Comparator.comparing((WarlordsEntity wp) -> wp.getMinuteStats().total().getHealing()).reversed())
                         .collect(Collectors.toList());
        List<BaseComponent> leaderboardPlayersHealing = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsEntity we = players.get(i);
            leaderboardPlayersHealing.addAll(List.of(new ComponentBuilder()
                    .appendHoverText(ChatColor.AQUA + we.getName() +
                                    ChatColor.GRAY + ": " +
                                    ChatColor.GOLD + NumberFormat.getSimplifiedNumber(we.getMinuteStats().total().getHealing()),
                            ChatColor.DARK_GRAY + "Lv" +
                                    ChatColor.GRAY + ExperienceManager.getLevelForSpec(we.getUuid(), we.getSpecClass()) + " " +
                                    ChatColor.GOLD + we.getSpec().getClassName() +
                                    ChatColor.GREEN + " (" +
                                    we.getSpec().getClass().getSimpleName() +
                                    ")"
                    )
                    .create()));

            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersHealing.add(ChatUtils.SPACER);
            }
        }
        sendGlobalEventMessage(game, leaderboardPlayersHealing.toArray(new BaseComponent[0]));
    }

    /**
     * @param players player collection to give the capture message module to.
     */
    private void showFlagCaptures(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalEventMessage(game, new ComponentBuilder()
                .appendHoverText(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "✚ MVP ✚",
                        ChatColor.LIGHT_PURPLE + "Total Flag Captures (everyone): " +
                                ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream()
                                                                                      .mapToInt(WarlordsEntity::getFlagsCaptured)
                                                                                      .sum()) +
                                "\n" +
                                ChatColor.LIGHT_PURPLE + "Total Flag Returns (everyone): " +
                                ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream()
                                                                                      .mapToInt(WarlordsEntity::getFlagsCaptured)
                                                                                      .sum())
                )
                .create());
        players = players.stream()
                         .sorted(Comparator.comparing(WarlordsEntity::getTotalCapsAndReturnsWeighted).reversed())
                         .collect(Collectors.toList());
        sendGlobalEventMessage(game, new ComponentBuilder()
                .appendHoverText(ChatColor.AQUA + players.get(0).getName(),
                        ChatColor.LIGHT_PURPLE + "Flag Captures: " +
                                ChatColor.GOLD + players.get(0).getFlagsCaptured() +
                                "\n" +
                                ChatColor.LIGHT_PURPLE + "Flag Returns: " + ChatColor.GOLD + players.get(0).getFlagsReturned()
                )
                .create()
        );
    }

    private void showExperienceSummary(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, ChatColor.YELLOW.toString() + ChatColor.BOLD + "✚ EXPERIENCE SUMMARY ✚", true);
        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }

            LinkedHashMap<String, Long> expSummary = ExperienceManager.getExpFromGameStats(wp, false);
            long experienceEarnedUniversal = expSummary.values().stream().mapToLong(Long::longValue).sum();
            long experienceEarnedSpec = ExperienceManager.getSpecExpFromSummary(expSummary);
            long experienceOnSpec = ExperienceManager.getExperienceForSpec(wp.getUuid(), wp.getSpecClass());
            long experienceUniversal = ExperienceManager.getUniversalLevel(wp.getUuid());
            StringBuilder specExpSummary = new StringBuilder();
            StringBuilder universalExpSummary = new StringBuilder();
            expSummary.forEach((s, aLong) -> {
                if (
                        !s.equals("First Game of the Day") &&
                                !s.equals("Second Game of the Day") &&
                                !s.equals("Third Game of the Day")
                ) {
                    specExpSummary.append(ChatColor.AQUA)
                                  .append(s).append(ChatColor.WHITE)
                                  .append(": ")
                                  .append(ChatColor.DARK_GRAY)
                                  .append("+")
                                  .append(ChatColor.DARK_GREEN)
                                  .append(aLong)
                                  .append("\n");
                }

                universalExpSummary.append(ChatColor.AQUA)
                                   .append(s)
                                   .append(ChatColor.WHITE)
                                   .append(": ")
                                   .append(ChatColor.DARK_GRAY)
                                   .append("+")
                                   .append(ChatColor.DARK_GREEN)
                                   .append(aLong)
                                   .append("\n");
            });

            specExpSummary.setLength(specExpSummary.length() - 1);
            universalExpSummary.setLength(universalExpSummary.length() - 1);

            ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
                    .appendHoverText(
                            ChatColor.GRAY + "+" +
                                    ChatColor.DARK_GREEN + NumberFormat.addCommaAndRound(experienceEarnedSpec) + " " +
                                    ChatColor.GOLD + wp.getSpec().getClassName() + " Experience " +
                                    ChatColor.GRAY + "(" +
                                    wp.getSpecClass().specType.chatColor + wp.getSpecClass().name +
                                    ChatColor.GRAY + ")",
                            specExpSummary.toString()
                    )
                    .create()
            );

            ExperienceManager.giveLevelUpMessage(player, experienceOnSpec - experienceEarnedSpec, experienceOnSpec);
            ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
                    .appendHoverText(
                            ChatColor.GRAY + "+" +
                                    ChatColor.DARK_AQUA + NumberFormat.addCommaAndRound(experienceEarnedUniversal) + " " +
                                    ChatColor.GOLD + "Universal Experience ",
                            universalExpSummary.toString()
                    )
                    .create()
            );

            ExperienceManager.giveLevelUpMessage(player, experienceUniversal - experienceEarnedUniversal, experienceUniversal);
            ExperienceManager.CACHED_PLAYER_EXP_SUMMARY.remove(wp.getUuid());


            LinkedHashMap<String, Long> expFromPvE = GuildExperienceUtils.getExpFromPvE(wp, null, false);
            if (expFromPvE.size() > 0) {
                StringBuilder expFromPvESummary = new StringBuilder();
                expFromPvE.forEach((s, aLong) -> {
                    expFromPvESummary.append(ChatColor.AQUA)
                                     .append(s)
                                     .append(ChatColor.WHITE)
                                     .append(": ")
                                     .append(ChatColor.DARK_GRAY)
                                     .append("+")
                                     .append(ChatColor.DARK_GREEN)
                                     .append(aLong)
                                     .append("\n");
                });
                expFromPvESummary.setLength(expFromPvESummary.length() - 1);

                ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
                        .appendHoverText(
                                ChatColor.GRAY + "+" +
                                        ChatColor.GREEN + NumberFormat.addCommaAndRound(expFromPvE.values()
                                                                                                  .stream()
                                                                                                  .mapToLong(Long::longValue)
                                                                                                  .sum()) + " " +
                                        ChatColor.DARK_GREEN + "Guild Experience", expFromPvESummary.toString()
                        )
                        .create()
                );
            }
        }
    }

    private void showCoinSummary(PveOption pveOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalMessage(game, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "✚ COINS SUMMARY ✚", true);

        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }
            Currencies.PvECoinSummary pvECoinSummary = Currencies.getCoinGainFromGameStats(wp, pveOption, false);

            StringBuilder coinSummaryString = new StringBuilder();
            pvECoinSummary.getCoinSummary().forEach((s, aLong) -> {
                coinSummaryString.append(ChatColor.AQUA)
                                 .append(s)
                                 .append(ChatColor.WHITE)
                                 .append(": ")
                                 .append(ChatColor.DARK_GRAY)
                                 .append("+")
                                 .append(ChatColor.GOLD)
                                 .append(aLong)
                                 .append("\n");
            });
            coinSummaryString.setLength(coinSummaryString.length() - 1);

            ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
                    .appendHoverText(
                            ChatColor.GRAY + "+" +
                                    ChatColor.YELLOW + NumberFormat.addCommaAndRound(pvECoinSummary.getTotalCoinsGained()) + " " +
                                    ChatColor.GOLD + "Coins",
                            coinSummaryString.toString()
                    )
                    .create()
            );

            Pair<Guild, GuildPlayer> guildGuildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player.getUniqueId());
            if (guildGuildPlayerPair != null) {
                ChatUtils.sendMessage(player, true,
                        ChatColor.GRAY + "+" +
                                ChatColor.YELLOW + NumberFormat.addCommaAndRound(pvECoinSummary.getTotalGuildCoinsGained()) + " " +
                                ChatColor.GOLD + "Guild Coins"
                );
            }
        }
    }

    private void showWeaponSummary(PveOption pveOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalMessage(game, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "✚ WEAPONS SUMMARY ✚", true);


        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }
            PlayerPveRewards playerPveRewards = pveOption.getRewards().getPlayerRewards(wp.getUuid());
            List<AbstractWeapon> weaponsFound = playerPveRewards.getWeaponsFound();
            if (weaponsFound.isEmpty()) {
                ChatUtils.sendMessage(player, true, ChatColor.GOLD + "You did not find any weapons in this game!");
            } else {
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
                            ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
                                    .appendHoverText(rarity.chatColor.toString() + amountFound + " " + rarity.name + (amountFound == 1 ? "" : "s") + (autoSalvaged ? ChatColor.WHITE + "*" : ""),
                                            weapons.stream()
                                                   .map(abstractWeapon -> {
                                                       String output = abstractWeapon.getName();
                                                       if (abstractWeapon instanceof WeaponScore) {
                                                           output += ChatColor.YELLOW + " (" + NumberFormat.formatOptionalHundredths(((WeaponScore) abstractWeapon).getWeaponScore()) + ")";
                                                       }
                                                       if (!weaponInventory.contains(abstractWeapon)) {
                                                           output += ChatColor.WHITE + " (Auto Salvaged)";
                                                       }
                                                       return output;
                                                   })
                                                   .collect(Collectors.joining("\n"))
                                    )
                                    .create()
                            );
                        }
                    });
                });

            }

            long fragmentGain = playerPveRewards.getLegendFragmentGain();
            if (fragmentGain > 0) {
                ChatUtils.sendMessage(player,
                        true,
                        ChatColor.GRAY + "+" + ChatColor.GREEN + fragmentGain + " " + Currencies.LEGEND_FRAGMENTS.getColoredName() + "s"
                );
            }
        }
    }

    private void showMobDropSummary(PveOption pveOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalMessage(game, ChatColor.GREEN.toString() + ChatColor.BOLD + "✚ MOB DROPS SUMMARY ✚", true);


        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }
            PlayerPveRewards playerPveRewards = pveOption.getRewards().getPlayerRewards(wp.getUuid());
            HashMap<MobDrops, Long> weaponsFound = playerPveRewards.getMobDropsGained();
            if (weaponsFound.isEmpty()) {
                ChatUtils.sendMessage(player, true, ChatColor.GOLD + "You did not get any mob drops in this game!");
            } else {
                List<MobDrops> mobDrops = new ArrayList<>(weaponsFound.keySet());
                mobDrops.sort(Comparator.comparingInt(MobDrops::ordinal)); // TODO TEST
                for (MobDrops mobDrop : mobDrops) {
                    long amountFound = weaponsFound.get(mobDrop);
                    ChatUtils.sendMessage(player,
                            true,
                            mobDrop.getCostColoredName(amountFound)
                    );
                }
            }
        }

    }


    private void showQuestSummary(PveOption pveOption, List<WarlordsPlayer> players) {
        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }
            List<Quests> quests = Quests.getQuestsFromGameStats(wp, pveOption, false);
            if (!quests.isEmpty()) {
                player.sendMessage("");
                ChatUtils.sendCenteredMessage(player, ChatColor.AQUA.toString() + ChatColor.BOLD + "✚ QUESTS SUMMARY ✚");
            }
            for (Quests quest : quests) {
                ChatUtils.sendCenteredMessageWithEvents(player, new ComponentBuilder()
                        .appendHoverText(ChatColor.GREEN + quest.name, ChatColor.GREEN + quest.description)
                        .create()
                );
            }
        }
    }

    private void showEventStats(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalMessage(game, ChatColor.AQUA.toString() + ChatColor.BOLD + "✚ EVENT SUMMARY ✚", true);
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
                ChatUtils.sendMessage(player,
                        true,
                        ChatColor.GRAY + "+" + ChatColor.YELLOW + NumberFormat.addCommas(Math.min(points,
                                eventPointsOption.getCap()
                        )) + " Point" + (points == 1 ? "" : "s")
                );
            }

            for (Option option : game.getOptions()) {
                option.sendEventStatsMessage(game, player);
            }
        }
        sendGlobalMessage(game, "", false);
    }

    public void sendGlobalEventMessage(Game game, BaseComponent[] message) {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
            ChatUtils.sendCenteredMessageWithEvents(p, message);
        });
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
