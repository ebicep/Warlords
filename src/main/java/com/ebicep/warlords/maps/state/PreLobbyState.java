package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Gates;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.ebicep.warlords.util.Utils.sendMessage;

public class PreLobbyState implements State, TimerDebugAble {

    private int timer = 0;
    private final Game game;

    public PreLobbyState(Game game) {
        this.game = game;
    }

    @Override
    public void begin( ) {
        timer = game.getMap().getCountdownTimerInTicks();
        Gates.changeGates(game.getMap(), false);
    }

    @Override
    public State run() {
        int players = game.playersCount();
        if (players >= game.getMap().getMinPlayers()) {
            if (timer % 20 == 0) {
                int time = timer / 20;
                game.forEachOnlinePlayer((player, team) -> {
                    updateTimeLeft(player, time);
                    updatePlayers(player, players, game);
                });
                if (time == 30) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + "30 " + ChatColor.YELLOW + "seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 20) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in 20 seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 10) {
                    game.forEachOnlinePlayer((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + "10 " + ChatColor.YELLOW + "seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time <= 5 && time != 0) {
                    game.forEachOnlinePlayer((player, team) -> {
                        String s = time == 1 ? "!" : "s!";
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " second" + s);
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 0) {
                    game.forEachOnlinePlayer((player, team) -> {
                        Utils.sendMessage(player, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        Utils.sendMessage(player, true, "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords");
                        Utils.sendMessage(player, true, "");
                        Utils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "Steal and capture the enemy team's flag to");
                        Utils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "earn " + ChatColor.AQUA + ChatColor.BOLD + "250 " + ChatColor.YELLOW + ChatColor.BOLD + "points! The first team with a");
                        Utils.sendMessage(player, true, "" + ChatColor.YELLOW + ChatColor.BOLD + "score of " + ChatColor.AQUA + ChatColor.BOLD + "1000 " + ChatColor.YELLOW + ChatColor.BOLD + "wins!");
                        Utils.sendMessage(player, true, "");
                        Utils.sendMessage(player, false, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                        player.playSound(player.getLocation(), "gamestart", 1, 1);
                    });
                }
            }
            if (timer <= 0) {
                return new PlayingState(game);
            }
            timer--;
        } else {
            timer = game.getMap().getCountdownTimerInTicks();
        }
        return null;
    }

    @Override
    public void end() {
    }

    @Override
    public void skipTimer() {
        this.timer = 0;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = game.getMap().getCountdownTimerInTicks();
    }

    public void updatePlayers(Player player, int players, Game game) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        Scoreboard scoreboard = player.getScoreboard();
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("Players")) {
                scoreboard.resetScores(entry);
                scoreboard.getObjective(dateString).getScore(ChatColor.WHITE + "Players: " + ChatColor.GREEN + players + "/" + game.getMap().getMaxPlayers()).setScore(10);
            }
        }
    }

    public void updateTimeLeft(Player player, int time) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        Scoreboard scoreboard = player.getScoreboard();
        time += 1;
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("Starting in")) {
                scoreboard.resetScores(entry);
                if (time < 10) {
                    scoreboard.getObjective(dateString).getScore(ChatColor.WHITE + "Starting in: " + ChatColor.GREEN + "00:0" + time + ChatColor.WHITE + " to").setScore(8);
                } else {
                    scoreboard.getObjective(dateString).getScore(ChatColor.WHITE + "Starting in: " + ChatColor.GREEN + "00:" + time + ChatColor.WHITE + " to").setScore(8);
                }
            }
        }
    }

}
