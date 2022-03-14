package com.ebicep.warlords.game.state;

import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.player.ExperienceManager;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase.previousGames;


public class EndState implements State, TimerDebugAble {
    @Nonnull
    private final Game game;
    private int timer;
    private final WarlordsGameTriggerWinEvent winEvent;

    public EndState(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent event) {
        this.game = game;
        this.winEvent = event;
    }

    @Override
    public void begin() {
        for (Option option : game.getOptions()) {
            option.onGameEnding(game);
        }
        this.resetTimer();
        boolean teamBlueWins = winEvent != null && winEvent.getDeclaredWinner() == Team.BLUE;
        boolean teamRedWins = winEvent != null && winEvent.getDeclaredWinner() == Team.RED;
        List<WarlordsPlayer> players = game.warlordsPlayers().collect(Collectors.toList());
        if (players.isEmpty()) return;
        sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
        sendMessageToAllGamePlayer(game, "" + ChatColor.WHITE + ChatColor.BOLD + "  Warlords", true);
        sendMessageToAllGamePlayer(game, "", false);
        if (teamBlueWins) {
            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.BLUE + "BLU", true);
        } else if (teamRedWins) {
            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.RED + "RED", true);
        } else {
            if (game.getAddons().contains(GameAddon.IMPOSTER_MODE)) {
                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "GAME END", true);
            } else {
                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "DRAW", true);
            }
        }
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent mvp = new TextComponent("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "✚ MVP ✚");
        mvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.LIGHT_PURPLE + "Total Flag Captures (everyone): " + ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream().mapToInt(WarlordsPlayer::getFlagsCaptured).sum()) + "\n" + ChatColor.LIGHT_PURPLE + "Total Flag Returns (everyone): " + ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream().mapToInt(WarlordsPlayer::getFlagsCaptured).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(mvp));
        players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalCapsAndReturnsWeighted).reversed()).collect(Collectors.toList());
        TextComponent playerMvp = new TextComponent(ChatColor.AQUA + players.get(0).getName());
        playerMvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.LIGHT_PURPLE + "Flag Captures: " + ChatColor.GOLD + players.get(0).getFlagsCaptured() + "\n" + ChatColor.LIGHT_PURPLE + "Flag Returns: " + ChatColor.GOLD + players.get(0).getFlagsReturned()).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(playerMvp));
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent totalDamage = new TextComponent("" + ChatColor.RED + ChatColor.BOLD + "✚ TOP DAMAGE ✚");
        totalDamage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Total Damage (everyone)" + ChatColor.GRAY + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream().mapToLong(wp -> wp.getMinuteStats().total().getDamage()).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalDamage));
        players = players.stream().sorted(Comparator.comparing((WarlordsPlayer wp) -> wp.getMinuteStats().total().getDamage()).reversed()).collect(Collectors.toList());
        List<TextComponent> leaderboardPlayersDamage = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsPlayer warlordsPlayer = players.get(i);
            TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + NumberFormat.getSimplifiedNumber(warlordsPlayer.getMinuteStats().total().getDamage()));
            player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_GRAY + "Lv" + ChatColor.GRAY + ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass()) + " " + ChatColor.GOLD + warlordsPlayer.getSpec().getClassName() + ChatColor.GREEN + " (" + warlordsPlayer.getSpec().getClass().getSimpleName() + ")").create()));
            leaderboardPlayersDamage.add(player);
            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersDamage.add(ChatUtils.SPACER);
            }
        }
        sendCenteredHoverableMessageToAllGamePlayer(game, leaderboardPlayersDamage);
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent totalHealing = new TextComponent("" + ChatColor.GREEN + ChatColor.BOLD + "✚ TOP HEALING ✚");
        totalHealing.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Total Healing (everyone)" + ChatColor.GRAY + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(players.stream().mapToLong(wp -> wp.getMinuteStats().total().getHealing()).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalHealing));
        players = players.stream().sorted(Comparator.comparing((WarlordsPlayer wp) -> wp.getMinuteStats().total().getHealing()).reversed()).collect(Collectors.toList());
        List<TextComponent> leaderboardPlayersHealing = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsPlayer warlordsPlayer = players.get(i);
            TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + NumberFormat.getSimplifiedNumber(warlordsPlayer.getMinuteStats().total().getHealing()));
            player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_GRAY + "Lv" + ChatColor.GRAY + ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass()) + " " + ChatColor.GOLD + warlordsPlayer.getSpec().getClassName() + ChatColor.GREEN + " (" + warlordsPlayer.getSpec().getClass().getSimpleName() + ")").create()));
            leaderboardPlayersHealing.add(player);
            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersHealing.add(ChatUtils.SPACER);
            }
        }
        sendCenteredHoverableMessageToAllGamePlayer(game, leaderboardPlayersHealing);
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent yourStatistics = new TextComponent("" + ChatColor.GOLD + ChatColor.BOLD + "✚ YOUR STATISTICS ✚");
        yourStatistics.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.WHITE + "Total Kills (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(players.stream().mapToInt(wp -> wp.getMinuteStats().total().getKills()).sum()) + "\n" + ChatColor.WHITE + "Total Assists (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(players.stream().mapToInt(wp -> wp.getMinuteStats().total().getAssists()).sum()) + "\n" + ChatColor.WHITE + "Total Deaths (everyone): " + ChatColor.GREEN + NumberFormat.addCommaAndRound(players.stream().mapToInt(wp -> wp.getMinuteStats().total().getDeaths()).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(yourStatistics));
        for (WarlordsPlayer wp : PlayerFilter.playingGame(game)) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) continue;

            TextComponent kills = new TextComponent(ChatColor.WHITE + "Kills: " + ChatColor.GOLD + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getKills()));
            TextComponent assists = new TextComponent(ChatColor.WHITE + "Assists: " + ChatColor.GOLD + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getAssists()));
            TextComponent deaths = new TextComponent(ChatColor.WHITE + "Deaths: " + ChatColor.GOLD + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getDeaths()));
            kills.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(wp.getStatString("Kills")).create()));
            assists.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(wp.getStatString("Assists")).create()));
            deaths.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(wp.getStatString("Deaths")).create()));
            ChatUtils.sendCenteredMessageWithEvents(player, Arrays.asList(kills, ChatUtils.SPACER, assists, ChatUtils.SPACER, deaths));
            TextComponent damage = new TextComponent(ChatColor.WHITE + "Damage: " + ChatColor.GOLD + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getDamage()));
            TextComponent heal = new TextComponent(ChatColor.WHITE + "Healing: " + ChatColor.GOLD + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getHealing()));
            TextComponent absorb = new TextComponent(ChatColor.WHITE + "Absorbed: " + ChatColor.GOLD + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getAbsorbed()));
            damage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(wp.getStatString("Damage")).create()));
            heal.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(wp.getStatString("Healing")).create()));
            absorb.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(wp.getStatString("Absorbed")).create()));
            ChatUtils.sendCenteredMessageWithEvents(player, Arrays.asList(damage, ChatUtils.SPACER, heal, ChatUtils.SPACER, absorb));
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

        if (game.playersCount() >= 12 && previousGames.get(previousGames.size() - 1).isCounted()) {
            sendMessageToAllGamePlayer(game, "", false);
            sendMessageToAllGamePlayer(game, ChatColor.YELLOW.toString() + ChatColor.BOLD + "✚ EXPERIENCE SUMMARY ✚", true);
            for (WarlordsPlayer wp : PlayerFilter.playingGame(game)) {
                Player player = Bukkit.getPlayer(wp.getUuid());
                if (player == null) continue;

                LinkedHashMap<String, Long> expSummary = ExperienceManager.getExpFromGameStats(wp, false);
                long experienceEarnedUniversal = expSummary.values().stream().mapToLong(Long::longValue).sum();
                long experienceEarnedSpec = ExperienceManager.getSpecExpFromSummary(expSummary);
                long experienceOnSpec = ExperienceManager.getExperienceForSpec(wp.getUuid(), wp.getSpecClass());
                long experienceUniversal = ExperienceManager.getUniversalLevel(wp.getUuid());
                StringBuilder specExpSummary = new StringBuilder();
                StringBuilder universalExpSummary = new StringBuilder();
                expSummary.forEach((s, aLong) -> {
                    if(!s.equals("First Game of the Day") && !s.equals("Second Game of the Day") && !s.equals("Third Game of the Day")) {
                        specExpSummary.append(ChatColor.AQUA).append(s).append(ChatColor.WHITE).append(": ").append(ChatColor.DARK_GRAY).append("+").append(ChatColor.DARK_GREEN).append(aLong).append("\n");
                    }
                    universalExpSummary.append(ChatColor.AQUA).append(s).append(ChatColor.WHITE).append(": ").append(ChatColor.DARK_GRAY).append("+").append(ChatColor.DARK_GREEN).append(aLong).append("\n");
                });
                specExpSummary.setLength(specExpSummary.length() - 1);
                universalExpSummary.setLength(universalExpSummary.length() - 1);

                TextComponent classSpecExp = new TextComponent(ChatColor.GRAY + "+" + ChatColor.DARK_GREEN + NumberFormat.addCommaAndRound(experienceEarnedSpec) + " " + ChatColor.GOLD + wp.getSpec().getClassName() + " Experience " + ChatColor.GRAY + "(" + wp.getSpecClass().specType.chatColor + wp.getSpecClass().name + ChatColor.GRAY + ")");
                classSpecExp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(specExpSummary.toString()).create()));
                ChatUtils.sendCenteredMessageWithEvents(player, Collections.singletonList(classSpecExp));
                ExperienceManager.giveLevelUpMessage(player, experienceOnSpec, experienceOnSpec + experienceEarnedSpec);

                TextComponent universalExp = new TextComponent(ChatColor.GRAY + "+" + ChatColor.DARK_AQUA + NumberFormat.addCommaAndRound(experienceEarnedUniversal) + " " + ChatColor.GOLD + "Universal Experience ");
                universalExp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(universalExpSummary.toString()).create()));
                ChatUtils.sendCenteredMessageWithEvents(player, Collections.singletonList(universalExp));
                ExperienceManager.giveLevelUpMessage(player, experienceUniversal, experienceUniversal + experienceEarnedUniversal);
            }
        }
        sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
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

    private void sendMessageToAllGamePlayer(Game game, String message, boolean centered) {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
            if (centered) {
                ChatUtils.sendCenteredMessage(p, message);
            } else {
                p.sendMessage(message);
            }
        });
    }

    public void sendCenteredHoverableMessageToAllGamePlayer(Game game, List<TextComponent> message) {
        game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
            ChatUtils.sendCenteredMessageWithEvents(p, message);
        });
    }

}
