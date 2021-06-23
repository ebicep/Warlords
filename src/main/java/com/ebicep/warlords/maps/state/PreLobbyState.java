package com.ebicep.warlords.maps.state;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Gates;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import static com.ebicep.warlords.util.Utils.sendMessage;

public class PreLobbyState implements State, TimerDebugAble {

    private int timer = 0;
    private final Game game;

    public PreLobbyState(Game game) {
        this.game = game;
    }

    @Override
    public void begin() {
        timer = game.getMap().getCountdownTimerInTicks();
        Gates.changeGates(game.getMap(), false);
    }

    @Override
    public State run() {
        int players = game.playersCount();
        if (players >= game.getMap().getMinPlayers()) {
            if (timer % 20 == 0) {
                int time = timer / 20;
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
                        String s = time == 1 ? "" : "s";
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

}
