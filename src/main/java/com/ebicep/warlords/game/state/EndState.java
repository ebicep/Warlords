package com.ebicep.warlords.game.state;

import com.ebicep.warlords.commands.debugcommands.misc.GetPlayerLastAbilityStatsCommand;
import com.ebicep.warlords.commands.miscellaneouscommands.StreamChaptersCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildExperienceUtils;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.MinuteStats;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.bukkit.TextComponentBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import net.md_5.bungee.api.chat.TextComponent;
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
        for (Option option : game.getOptions()) {
            option.onGameEnding(game);
        }
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
            if (game.getGameMode() == com.ebicep.warlords.game.GameMode.WAVE_DEFENSE) {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.BLUE + "PLAYERS", true);
            } else {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.BLUE + "BLU", true);
            }
        } else if (teamRedWins) {
            if (game.getGameMode() == com.ebicep.warlords.game.GameMode.WAVE_DEFENSE) {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.RED + "MONSTERS", true);
            } else {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.RED + "RED", true);
            }
        } else {
            if (game.getAddons().contains(GameAddon.IMPOSTER_MODE)) {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "GAME END", true);
            } else {
                sendGlobalMessage(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "DRAW", true);
            }
        }

        switch (game.getGameMode()) {
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
            case WAVE_DEFENSE:
                showTopDamage(players);
                showTopHealing(players);
                break;
            default:
                sendGlobalMessage(game, "", false);
                break;
        }

        //PLAYER STATS

        sendGlobalMessage(game, "", false);
        int totalKills = players.stream()
                .mapToInt(wp -> wp.getMinuteStats().total().getKills())
                .sum();
        int totalAssists = players.stream().mapToInt(wp -> wp.getMinuteStats().total().getAssists()).sum();
        int totalDeaths = players.stream().mapToInt(wp -> wp.getMinuteStats().total().getDeaths()).sum();

        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }

            ChatUtils.sendCenteredMessageWithEvents(
                    player,
                    Collections.singletonList(new TextComponentBuilder(ChatColor.GOLD.toString() + ChatColor.BOLD + "✚ YOUR STATISTICS ✚")
                            .setHoverText(ChatColor.WHITE + "Total Kills (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(totalKills) + "\n" +
                                    ChatColor.WHITE + "Total Assists (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(totalAssists) + "\n" +
                                    ChatColor.WHITE + "Total Deaths (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(totalDeaths) + "\n" +
                                    ChatColor.WHITE + "Total Melee Hits (you): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(wp.getMinuteStats()
                                    .total()
                                    .getMeleeHits()))
                            .getTextComponent())
            );

            List<TextComponent> textComponents = new ArrayList<>(wp.getAllMinuteHoverableStats(MinuteStats.KILLS));
            textComponents.add(ChatUtils.SPACER);
            textComponents.addAll(wp.getAllMinuteHoverableStats(MinuteStats.ASSISTS));
            textComponents.add(ChatUtils.SPACER);
            textComponents.addAll(wp.getAllMinuteHoverableStats(MinuteStats.DEATHS));
            ChatUtils.sendCenteredMessageWithEvents(player, textComponents);

            textComponents.clear();
            textComponents.addAll(wp.getAllMinuteHoverableStats(MinuteStats.DAMAGE));
            textComponents.add(ChatUtils.SPACER);
            textComponents.addAll(wp.getAllMinuteHoverableStats(MinuteStats.HEALING));
            textComponents.add(ChatUtils.SPACER);
            textComponents.addAll(wp.getAllMinuteHoverableStats(MinuteStats.ABSORBED));
            ChatUtils.sendCenteredMessageWithEvents(player, textComponents);

            ChatUtils.sendMessage(player, false, "");

            //ABILITY INFO
            List<TextComponent> formattedData = wp.getSpec().getFormattedData();
            ChatUtils.sendCenteredMessageWithEvents(player,
                    Arrays.asList(formattedData.get(0), ChatUtils.SPACER, formattedData.get(1), ChatUtils.SPACER, formattedData.get(2))
            );
            ChatUtils.sendCenteredMessageWithEvents(player, Arrays.asList(formattedData.get(3), ChatUtils.SPACER, formattedData.get(4)));

            GetPlayerLastAbilityStatsCommand.playerLastAbilityStats.put(player.getUniqueId(), formattedData);

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);

            if (!game.getAddons().contains(GameAddon.IMPOSTER_MODE)) {
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
            showExperienceSummary(players);
            for (Option option : game.getOptions()) {
                if (option instanceof WaveDefenseOption) {
                    showCoinSummary((WaveDefenseOption) option, players);
                    showWeaponSummary((WaveDefenseOption) option, players);
                    break;
                }
            }
        }

        sendGlobalMessage(game,
                "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬",
                true
        );

        if (winEvent != null) {
            this.game.forEachOfflineWarlordsPlayer(wp -> {
                if (StreamChaptersCommand.gameTimes.containsKey(wp.getUuid())) {
                    List<StreamChaptersCommand.GameTime> gameTimes = StreamChaptersCommand.gameTimes.get(wp.getUuid());
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

    private void showTopDamage(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalEventMessage(game, Collections.singletonList(
                new TextComponentBuilder(ChatColor.RED.toString() + ChatColor.BOLD + "✚ TOP DAMAGE ✚")
                        .setHoverText(
                                ChatColor.RED + "Total Damage (everyone)" +
                                        ChatColor.GRAY + ": " +
                                        ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream()
                                        .mapToLong(wp -> wp.getMinuteStats().total().getDamage())
                                        .sum()))
                        .getTextComponent()));

        players = players.stream()
                .sorted(Comparator.comparing((WarlordsEntity wp) -> wp.getMinuteStats().total().getDamage()).reversed())
                .collect(Collectors.toList());
        List<TextComponent> leaderboardPlayersDamage = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsEntity we = players.get(i);
            leaderboardPlayersDamage.add(
                    new TextComponentBuilder(
                            ChatColor.AQUA + we.getName() +
                                    ChatColor.GRAY + ": " +
                                    ChatColor.GOLD + NumberFormat.getSimplifiedNumber(we.getMinuteStats().total().getDamage()))
                            .setHoverText(
                                    ChatColor.DARK_GRAY + "Lv" +
                                            ChatColor.GRAY + ExperienceManager.getLevelForSpec(we.getUuid(), we.getSpecClass()) + " " +
                                            ChatColor.GOLD + we.getSpec().getClassName() +
                                            ChatColor.GREEN + " (" +
                                            we.getSpec().getClass().getSimpleName() +
                                            ")"
                            ).getTextComponent());

            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersDamage.add(ChatUtils.SPACER);
            }
        }
        sendGlobalEventMessage(game, leaderboardPlayersDamage);
    }

    private void showTopHealing(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalEventMessage(game, Collections.singletonList(
                new TextComponentBuilder(ChatColor.GREEN.toString() + ChatColor.BOLD + "✚ TOP HEALING ✚")
                        .setHoverText(
                                ChatColor.GREEN + "Total Healing (everyone)" +
                                        ChatColor.GRAY + ": " +
                                        ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream()
                                        .mapToLong(wp -> wp.getMinuteStats().total().getHealing())
                                        .sum()))
                        .getTextComponent()));

        players = players.stream()
                .sorted(Comparator.comparing((WarlordsEntity wp) -> wp.getMinuteStats().total().getHealing()).reversed())
                .collect(Collectors.toList());
        List<TextComponent> leaderboardPlayersHealing = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsEntity wp = players.get(i);
            leaderboardPlayersHealing.add(
                    new TextComponentBuilder(
                            ChatColor.AQUA + wp.getName() +
                                    ChatColor.GRAY + ": " +
                                    ChatColor.GOLD + NumberFormat.getSimplifiedNumber(wp.getMinuteStats().total().getHealing()))
                            .setHoverText(
                                    ChatColor.DARK_GRAY + "Lv" +
                                            ChatColor.GRAY + ExperienceManager.getLevelForSpec(wp.getUuid(), wp.getSpecClass()) + " " +
                                            ChatColor.GOLD + wp.getSpec().getClassName() +
                                            ChatColor.GREEN + " (" +
                                            wp.getSpec().getClass().getSimpleName() +
                                            ")"
                            ).getTextComponent());

            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersHealing.add(ChatUtils.SPACER);
            }
        }
        sendGlobalEventMessage(game, leaderboardPlayersHealing);
    }

    /**
     * @param players player collection to give the capture message module to.
     */
    private void showFlagCaptures(List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalEventMessage(game, Collections.singletonList(
                new TextComponentBuilder(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "✚ MVP ✚")
                        .setHoverText(
                                ChatColor.LIGHT_PURPLE + "Total Flag Captures (everyone): " +
                                        ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream()
                                        .mapToInt(WarlordsEntity::getFlagsCaptured)
                                        .sum()) +
                                        "\n" +
                                        ChatColor.LIGHT_PURPLE + "Total Flag Returns (everyone): " +
                                        ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream()
                                        .mapToInt(WarlordsEntity::getFlagsCaptured)
                                        .sum()))
                        .getTextComponent()));
        players = players.stream()
                .sorted(Comparator.comparing(WarlordsEntity::getTotalCapsAndReturnsWeighted).reversed())
                .collect(Collectors.toList());
        sendGlobalEventMessage(game, Collections.singletonList(
                new TextComponentBuilder(ChatColor.AQUA + players.get(0).getName())
                        .setHoverText(ChatColor.LIGHT_PURPLE + "Flag Captures: " +
                                ChatColor.GOLD + players.get(0).getFlagsCaptured() +
                                "\n" +
                                ChatColor.LIGHT_PURPLE + "Flag Returns: " + ChatColor.GOLD + players.get(0).getFlagsReturned())
                        .getTextComponent()));
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

            ChatUtils.sendCenteredMessageWithEvents(player, Collections.singletonList(
                    new TextComponentBuilder(
                            ChatColor.GRAY + "+" +
                                    ChatColor.DARK_GREEN + NumberFormat.addCommaAndRound(experienceEarnedSpec) + " " +
                                    ChatColor.GOLD + wp.getSpec().getClassName() + " Experience " +
                                    ChatColor.GRAY + "(" +
                                    wp.getSpecClass().specType.chatColor + wp.getSpecClass().name +
                                    ChatColor.GRAY + ")")
                            .setHoverText(specExpSummary.toString())
                            .getTextComponent())
            );

            ExperienceManager.giveLevelUpMessage(player, experienceOnSpec, experienceOnSpec + experienceEarnedSpec);
            ChatUtils.sendCenteredMessageWithEvents(player, Collections.singletonList(
                    new TextComponentBuilder(
                            ChatColor.GRAY + "+" +
                                    ChatColor.DARK_AQUA + NumberFormat.addCommaAndRound(experienceEarnedUniversal) + " " +
                                    ChatColor.GOLD + "Universal Experience ")
                            .setHoverText(universalExpSummary.toString())
                            .getTextComponent())
            );

            ExperienceManager.giveLevelUpMessage(player, experienceUniversal, experienceUniversal + experienceEarnedUniversal);
            ExperienceManager.CACHED_PLAYER_EXP_SUMMARY.remove(wp.getUuid());


            LinkedHashMap<String, Long> expFromWaveDefense = GuildExperienceUtils.getExpFromWaveDefense(wp, false);
            if (expFromWaveDefense.size() > 0) {
                StringBuilder expFromWaveDefenseSummary = new StringBuilder();
                expFromWaveDefense.forEach((s, aLong) -> {
                    expFromWaveDefenseSummary.append(ChatColor.AQUA)
                            .append(s)
                            .append(ChatColor.WHITE)
                            .append(": ")
                            .append(ChatColor.DARK_GRAY)
                            .append("+")
                            .append(ChatColor.DARK_GREEN)
                            .append(aLong)
                            .append("\n");
                });
                expFromWaveDefenseSummary.setLength(expFromWaveDefenseSummary.length() - 1);

                ChatUtils.sendCenteredMessageWithEvents(player, Collections.singletonList(
                        new TextComponentBuilder(
                                ChatColor.GRAY + "+" +
                                        ChatColor.GREEN + NumberFormat.addCommaAndRound(expFromWaveDefense.values()
                                        .stream()
                                        .mapToLong(Long::longValue)
                                        .sum()) + " " +
                                        ChatColor.DARK_GREEN + "Guild Experience")
                                .setHoverText(expFromWaveDefenseSummary.toString())
                                .getTextComponent())
                );
            }
        }
    }

    private void showCoinSummary(WaveDefenseOption waveDefenseOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalMessage(game, ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "✚ COINS SUMMARY ✚", true);

        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }
            Currencies.PvECoinSummary pvECoinSummary = Currencies.getCoinGainFromGameStats(wp, waveDefenseOption, false);

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

            ChatUtils.sendCenteredMessageWithEvents(player, Collections.singletonList(
                    new TextComponentBuilder(
                            ChatColor.GRAY + "+" +
                                    ChatColor.YELLOW + NumberFormat.addCommaAndRound(pvECoinSummary.getTotalCoinsGained()) + " " +
                                    ChatColor.GOLD + "Coins")
                            .setHoverText(coinSummaryString.toString())
                            .getTextComponent())
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

    private void showWeaponSummary(WaveDefenseOption waveDefenseOption, List<WarlordsPlayer> players) {
        sendGlobalMessage(game, "", false);
        sendGlobalMessage(game, ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "✚ WEAPONS SUMMARY ✚", true);

        HashMap<UUID, Long> playerLegendFragmentGain = waveDefenseOption.getWaveDefenseStats().getPlayerLegendFragmentGain();

        for (WarlordsPlayer wp : players) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) {
                continue;
            }

            List<AbstractWeapon> weaponsFound = waveDefenseOption.getWaveDefenseStats()
                    .getPlayerWeaponsFound()
                    .getOrDefault(wp.getUuid(), new ArrayList<>());
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
                weaponsFoundByType.forEach((rarity, weapons) -> {
                    int amountFound = weapons.size();
                    if (amountFound > 0) {
                        ChatUtils.sendCenteredMessageWithEvents(
                                player,
                                Collections.singletonList(new TextComponentBuilder(rarity.chatColor.toString() + amountFound + " " + rarity.name + (amountFound == 1 ? "" : "s"))
                                        .setHoverText(
                                                weapons.stream()
                                                        .map(AbstractWeapon::getName)
                                                        .collect(Collectors.joining("\n")))
                                        .getTextComponent())
                        );
                    }
                });
            }

            Long fragmentGain = playerLegendFragmentGain.getOrDefault(wp.getUuid(), 0L);
            if (fragmentGain > 0) {
                ChatUtils.sendMessage(player,
                        true,
                        ChatColor.GRAY + "+" + ChatColor.GREEN + fragmentGain + " " + Currencies.LEGEND_FRAGMENTS.getColoredName() + "s"
                );
            }
        }
    }

    public void sendGlobalEventMessage(Game game, List<TextComponent> message) {
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
