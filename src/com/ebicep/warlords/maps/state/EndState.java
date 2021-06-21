package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PlayingState.Stats;
import com.ebicep.warlords.util.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

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
        mvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.LIGHT_PURPLE + "Total Flag Captures (everyone): " + ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getFlagsCaptured).sum()) + "\n" + ChatColor.LIGHT_PURPLE + "Total Flag Returns (everyone): " + ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getFlagsCaptured).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(mvp));
        players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalCapsAndReturns)).collect(Collectors.toList());
        for (WarlordsPlayer player : players) {
            System.out.println(player.getName());
            System.out.println(player.getFlagsCaptured());
            System.out.println(player.getFlagsReturned());
        }
        TextComponent playerMvp = new TextComponent(ChatColor.AQUA + players.get(0).getName());
        playerMvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.LIGHT_PURPLE + "Flag Captures: " + ChatColor.GOLD + players.get(0).getFlagsCaptured() + "\n" + ChatColor.LIGHT_PURPLE + "Flag Returns: " + ChatColor.GOLD + players.get(0).getFlagsReturned()).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(playerMvp));
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent totalDamage = new TextComponent("" + ChatColor.RED + ChatColor.BOLD + "✚ TOP DAMAGE ✚");
        totalDamage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.RED + "Total Damage (everyone)" + ChatColor.GRAY + ": " + ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalDamage).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalDamage));
        players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalDamage)).collect(Collectors.toList());
        List<TextComponent> leaderboardPlayersDamage = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsPlayer warlordsPlayer = players.get(i);
            TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayer.getTotalHealing() + "k");
            player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_GRAY + "Lv" + ChatColor.GRAY + "90 " + ChatColor.GOLD + warlordsPlayer.getSpec().getClassName() + ChatColor.GREEN + " (" + warlordsPlayer.getSpec().getClass().getSimpleName() + ")").create()));
            leaderboardPlayersDamage.add(player);
            if (i != players.size() - 1) {
                leaderboardPlayersDamage.add(Game.spacer);
            }
        }
        sendCenteredHoverableMessageToAllGamePlayer(game, leaderboardPlayersDamage);
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent totalHealing = new TextComponent("" + ChatColor.GREEN + ChatColor.BOLD + "✚ TOP HEALING ✚");
        totalHealing.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Total Healing (everyone)" + ChatColor.GRAY + ": " + ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalHealing).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalHealing));
        players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalHealing)).collect(Collectors.toList());
        List<TextComponent> leaderboardPlayersHealing = new ArrayList<>();
        for (int i = 0; i < players.size() && i < 3; i++) {
            WarlordsPlayer warlordsPlayer = players.get(i);
            TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayer.getTotalHealing() + "k");
            player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.DARK_GRAY + "Lv" + ChatColor.GRAY + "90 " + ChatColor.GOLD + warlordsPlayer.getSpec().getClassName() + ChatColor.GREEN + " (" + warlordsPlayer.getSpec().getClass().getSimpleName() + ")").create()));
            leaderboardPlayersHealing.add(player);
            if (i != players.size() - 1) {
                leaderboardPlayersHealing.add(Game.spacer);
            }
        }
        sendCenteredHoverableMessageToAllGamePlayer(game, leaderboardPlayersHealing);
        sendMessageToAllGamePlayer(game, "", false);
        TextComponent yourStatistics = new TextComponent("" + ChatColor.GOLD + ChatColor.BOLD + "✚ YOUR STATISTICS ✚");
        yourStatistics.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.WHITE + "Total Kills (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalKills).sum()) + "\n" + ChatColor.WHITE + "Total Assists (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalAssists).sum()) + "\n" + ChatColor.WHITE + "Total Deaths (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalDeaths).sum())).create()));
        sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(yourStatistics));
        for (WarlordsPlayer value : Warlords.getPlayers().values()) {
            Player player = Bukkit.getPlayer(value.getUuid());
            if(player == null) continue;
            
            TextComponent kills = new TextComponent(ChatColor.WHITE + "Kills: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalKills()));
            TextComponent assists = new TextComponent(ChatColor.WHITE + "Assists: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalAssists()));
            TextComponent deaths = new TextComponent(ChatColor.WHITE + "Deaths: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalDeaths()));
            String killsJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Kills"));
            String assistsJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Assists"));
            String deathsJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Deaths"));
            kills.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(killsJson).create()));
            assists.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(assistsJson).create()));
            deaths.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(deathsJson).create()));
            Utils.sendCenteredHoverableMessage(player, Arrays.asList(kills, Game.spacer, assists, Game.spacer, deaths));
            TextComponent damage = new TextComponent(ChatColor.WHITE + "Damage: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalDamage()));
            TextComponent heal = new TextComponent(ChatColor.WHITE + "Healing: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalHealing()));
            TextComponent absorb = new TextComponent(ChatColor.WHITE + "Absorbed: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalAbsorbed()));
            String damageJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Damage"));
            String healingJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Healing"));
            String absorbedJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Absorbed"));
            damage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(damageJson).create()));
            heal.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(healingJson).create()));
            absorb.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(absorbedJson).create()));
            Utils.sendCenteredHoverableMessage(player, Arrays.asList(damage, Game.spacer, heal, Game.spacer, absorb));
            player.setGameMode(GameMode.ADVENTURE);
        }
        sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
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
            Utils.sendCenteredHoverableMessage(p, message);
        });
    }
}
