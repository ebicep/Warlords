package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PlayingState.Stats;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.RemoveEntities;
import com.ebicep.warlords.util.Utils;
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

public class EndState implements State, TimerDebugAble {
    @Nonnull
    private final Game game;
    @Nullable
    private final Team winner;
    private int timer;

    public EndState(@Nonnull Game game, @Nullable Team winner, @Nonnull Stats redStats, @Nonnull Stats blueStats) {
        this.game = game;
        this.winner = winner;
    }

    @Override
    public void begin( ) {
        this.resetTimer();
        boolean teamBlueWins = winner == Team.BLUE;
        boolean teamRedWins = winner == Team.RED;
        List<WarlordsPlayer> players = new ArrayList<>(Warlords.getPlayers().values());
        sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
        sendMessageToAllGamePlayer(game, "" + ChatColor.WHITE + ChatColor.BOLD + "  Warlords", true);
        sendMessageToAllGamePlayer(game, "", false);
        if (teamBlueWins) {
            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.BLUE + "BLU", true);
        } else if (teamRedWins) {
            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.RED + "RED", true);
        } else {
            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "DRAW", true);
        }
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent mvp = new TextComponent("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "✚ MVP ✚");
        mvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.LIGHT_PURPLE + "Total Flag Captures (everyone): " + ChatColor.GOLD + Utils.addCommaAndRound(players.stream().mapToInt(WarlordsPlayer::getFlagsCaptured).sum()) + "\n" + ChatColor.LIGHT_PURPLE + "Total Flag Returns (everyone): " + ChatColor.GOLD + Utils.addCommaAndRound(players.stream().mapToInt(WarlordsPlayer::getFlagsCaptured).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(mvp));
        players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalCapsAndReturnsWeighted).reversed()).collect(Collectors.toList());
        TextComponent playerMvp = new TextComponent(ChatColor.AQUA + players.get(0).getName());
        playerMvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.LIGHT_PURPLE + "Flag Captures: " + ChatColor.GOLD + players.get(0).getFlagsCaptured() + "\n" + ChatColor.LIGHT_PURPLE + "Flag Returns: " + ChatColor.GOLD + players.get(0).getFlagsReturned()).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(playerMvp));
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent totalDamage = new TextComponent("" + ChatColor.RED + ChatColor.BOLD + "✚ TOP DAMAGE ✚");
        totalDamage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Total Damage (everyone)" + ChatColor.GRAY + ": " + ChatColor.GOLD + Utils.addCommaAndRound(players.stream().mapToLong(WarlordsPlayer::getTotalDamage).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalDamage));
        players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalDamage).reversed()).collect(Collectors.toList());
        List<TextComponent> leaderboardPlayersDamage = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsPlayer warlordsPlayer = players.get(i);
            TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + Utils.getSimplifiedNumber(warlordsPlayer.getTotalDamage()));
            player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_GRAY + "Lv" + ChatColor.GRAY + "90 " + ChatColor.GOLD + warlordsPlayer.getSpec().getClassName() + ChatColor.GREEN + " (" + warlordsPlayer.getSpec().getClass().getSimpleName() + ")").create()));
            leaderboardPlayersDamage.add(player);
            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersDamage.add(Game.spacer);
            }
        }
        sendCenteredHoverableMessageToAllGamePlayer(game, leaderboardPlayersDamage);
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent totalHealing = new TextComponent("" + ChatColor.GREEN + ChatColor.BOLD + "✚ TOP HEALING ✚");
        totalHealing.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Total Healing (everyone)" + ChatColor.GRAY + ": " + ChatColor.GOLD + Utils.addCommaAndRound(players.stream().mapToLong(WarlordsPlayer::getTotalHealing).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalHealing));
        players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalHealing).reversed()).collect(Collectors.toList());
        List<TextComponent> leaderboardPlayersHealing = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsPlayer warlordsPlayer = players.get(i);
            TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + Utils.getSimplifiedNumber(warlordsPlayer.getTotalHealing()));
            player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_GRAY + "Lv" + ChatColor.GRAY + "90 " + ChatColor.GOLD + warlordsPlayer.getSpec().getClassName() + ChatColor.GREEN + " (" + warlordsPlayer.getSpec().getClass().getSimpleName() + ")").create()));
            leaderboardPlayersHealing.add(player);
            if (i != players.size() - 1 && i != 2) {
                leaderboardPlayersHealing.add(Game.spacer);
            }
        }
        sendCenteredHoverableMessageToAllGamePlayer(game, leaderboardPlayersHealing);
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent yourStatistics = new TextComponent("" + ChatColor.GOLD + ChatColor.BOLD + "✚ YOUR STATISTICS ✚");
        yourStatistics.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.WHITE + "Total Kills (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound(players.stream().mapToInt(WarlordsPlayer::getTotalKills).sum()) + "\n" + ChatColor.WHITE + "Total Assists (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound(players.stream().mapToInt(WarlordsPlayer::getTotalAssists).sum()) + "\n" + ChatColor.WHITE + "Total Deaths (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound(players.stream().mapToInt(WarlordsPlayer::getTotalDeaths).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(yourStatistics));
        for (WarlordsPlayer wp : PlayerFilter.playingGame(game)) {
            Player player = Bukkit.getPlayer(wp.getUuid());
            if (player == null) continue;

            TextComponent kills = new TextComponent(ChatColor.WHITE + "Kills: " + ChatColor.GOLD + Utils.addCommaAndRound(wp.getTotalKills()));
            TextComponent assists = new TextComponent(ChatColor.WHITE + "Assists: " + ChatColor.GOLD + Utils.addCommaAndRound(wp.getTotalAssists()));
            TextComponent deaths = new TextComponent(ChatColor.WHITE + "Deaths: " + ChatColor.GOLD + Utils.addCommaAndRound(wp.getTotalDeaths()));
            String killsJson = Utils.convertItemStackToJsonRegular(wp.getStatItemStack("Kills"));
            String assistsJson = Utils.convertItemStackToJsonRegular(wp.getStatItemStack("Assists"));
            String deathsJson = Utils.convertItemStackToJsonRegular(wp.getStatItemStack("Deaths"));
            kills.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(killsJson).create()));
            assists.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(assistsJson).create()));
            deaths.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(deathsJson).create()));
            Utils.sendCenteredMessageWithEvents(player, Arrays.asList(kills, Game.spacer, assists, Game.spacer, deaths));
            TextComponent damage = new TextComponent(ChatColor.WHITE + "Damage: " + ChatColor.GOLD + Utils.addCommaAndRound(wp.getTotalDamage()));
            TextComponent heal = new TextComponent(ChatColor.WHITE + "Healing: " + ChatColor.GOLD + Utils.addCommaAndRound(wp.getTotalHealing()));
            TextComponent absorb = new TextComponent(ChatColor.WHITE + "Absorbed: " + ChatColor.GOLD + Utils.addCommaAndRound(wp.getTotalAbsorbed()));
            String damageJson = Utils.convertItemStackToJsonRegular(wp.getStatItemStack("Damage"));
            String healingJson = Utils.convertItemStackToJsonRegular(wp.getStatItemStack("Healing"));
            String absorbedJson = Utils.convertItemStackToJsonRegular(wp.getStatItemStack("Absorbed"));
            damage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(damageJson).create()));
            heal.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(healingJson).create()));
            absorb.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(absorbedJson).create()));
            Utils.sendCenteredMessageWithEvents(player, Arrays.asList(damage, Game.spacer, heal, Game.spacer, absorb));
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);

            if (winner == null) {
                PacketUtils.sendTitle(player, "§d§lDRAW", "", 0, 100, 0);
                player.playSound(player.getLocation(), "defeat", 500, 1);
            } else if (wp.getTeam() == winner) {
                player.playSound(player.getLocation(), "victory", 500, 1);
                PacketUtils.sendTitle(player, "§6§lVICTORY!", "", 0, 100, 0);
            } else {
                player.playSound(player.getLocation(), "defeat", 500, 1);
                PacketUtils.sendTitle(player, "§c§lDEFEAT", "", 0, 100, 0);
            }

        }
//        if(game.playersCount() >= 16 && DatabaseManager.previousGames.get(DatabaseManager.previousGames.size() - 1).isUpdatePlayerStats()) {
//            sendMessageToAllGamePlayer(game, "", false);
//            sendMessageToAllGamePlayer(game, ChatColor.YELLOW.toString() + ChatColor.BOLD + "✚ EXPERIENCE SUMMARY ✚", true);
//            for (WarlordsPlayer wp : PlayerFilter.playingGame(game)) {
//                Player player = Bukkit.getPlayer(wp.getUuid());
//                if (player == null) continue;
//
//                LinkedHashMap<String, Long> expSummary = ExperienceManager.getExpFromGameStats(wp, false);
//                long experienceEarnedUniversal = expSummary.values().stream().mapToLong(Long::longValue).sum();
//                long experienceEarnedSpec = ExperienceManager.getSpecExpFromSummary(expSummary);
//                long experienceOnSpec = ExperienceManager.getExperienceForSpec(wp.getUuid(), wp.getSpecClass());
//                long experienceUniversal = ExperienceManager.getUniversalLevel(wp.getUuid());
//                StringBuilder specExpSummary = new StringBuilder();
//                StringBuilder universalExpSummary = new StringBuilder();
//                expSummary.forEach((s, aLong) -> {
//                    if(!s.equals("First Game of the Day") && !s.equals("Second Game of the Day") && !s.equals("Third Game of the Day")) {
//                        specExpSummary.append(ChatColor.AQUA).append(s).append(ChatColor.WHITE).append(": ").append(ChatColor.DARK_GRAY).append("+").append(ChatColor.DARK_GREEN).append(aLong).append("\n");
//                    }
//                    universalExpSummary.append(ChatColor.AQUA).append(s).append(ChatColor.WHITE).append(": ").append(ChatColor.DARK_GRAY).append("+").append(ChatColor.DARK_GREEN).append(aLong).append("\n");
//                });
//                specExpSummary.setLength(specExpSummary.length() - 1);
//                universalExpSummary.setLength(universalExpSummary.length() - 1);
//
//                TextComponent classSpecExp = new TextComponent(ChatColor.GRAY + "+" + ChatColor.DARK_GREEN + Utils.addCommaAndRound(experienceEarnedSpec) + " " + ChatColor.GOLD + wp.getSpec().getClassName() + " Experience " + ChatColor.GRAY + "(" + wp.getSpecClass().specType.chatColor + wp.getSpecClass().name + ChatColor.GRAY + ")");
//                classSpecExp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(specExpSummary.toString()).create()));
//                Utils.sendCenteredMessageWithEvents(player, Collections.singletonList(classSpecExp));
//                ExperienceManager.giveLevelUpMessage(player, experienceOnSpec, experienceOnSpec + experienceEarnedSpec);
//
//                TextComponent universalExp = new TextComponent(ChatColor.GRAY + "+" + ChatColor.DARK_AQUA + Utils.addCommaAndRound(experienceEarnedUniversal) + " " + ChatColor.GOLD + "Universal Experience ");
//                universalExp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(universalExpSummary.toString()).create()));
//                Utils.sendCenteredMessageWithEvents(player, Collections.singletonList(universalExp));
//                ExperienceManager.giveLevelUpMessage(player, experienceUniversal, experienceUniversal + experienceEarnedUniversal);
//            }
//        }
        sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
        RemoveEntities.removeArmorStands();
    }

    @Override
    public State run() {
        timer--;
        if(timer <= 0) {
            return new InitState(game);
        }
        return null;
    }

    @Override
    public void end() {
        game.clearAllPlayers();
        game.getSpectators().forEach(uuid -> {
            if(Bukkit.getPlayer(uuid) != null) {
                game.removeSpectator(uuid, false);
            }
        });
        game.getSpectators().clear();
        game.getGameTasks().forEach((task, timeCreated) -> task.cancel());
    }

    @Override
    public void skipTimer() throws IllegalStateException {
        this.timer = 0;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = 10 * 20;
    }

    private void sendMessageToAllGamePlayer(Game game, String message, boolean centered) {
        game.forEachOnlinePlayer((p, team) -> {
            if (centered) {
                Utils.sendCenteredMessage(p, message);
            } else {
                p.sendMessage(message);
            }
        });
    }

    public void sendCenteredHoverableMessageToAllGamePlayer(Game game, List<TextComponent> message) {
        game.forEachOnlinePlayer((p, team) -> {
            Utils.sendCenteredMessageWithEvents(p, message);
        });
    }

}
