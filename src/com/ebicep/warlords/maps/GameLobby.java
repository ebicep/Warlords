package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.text.SimpleDateFormat;
import java.util.Date;


// WIP not usable yet
public class GameLobby {

    private GameState state;

    enum Map {
        RIFT,
        CROSSFIRE,
        GORGE,
        WARSONG,
        VALLEY
    }

    enum GameState {
        LOBBY,
        PLAYING,
        END
    }

    public static class Game implements Runnable {

        private static final int MIN_PLAYERS = 8;
        private static final int MAX_PLAYERS = 24;
        private static final int COUNTDOWN_IN_SECONDS = 60;
        private final Object PLAYING = true;
        private final Object COUNTDOWN_AFTER_GAME_IN_SECONDS = 20;
        private GameState state = GameState.LOBBY;
        private int countdown = -1;
        private Object countDown;

        private int blueKills = 0;
        private int redKills = 0;


        @Override
        public void run() {
            switch (this.state) {
                case LOBBY:
                    int playersInGame = Bukkit.getWorld("game").getPlayers().size();
                    if (playersInGame >= MAX_PLAYERS) {
                        this.state = GameState.PLAYING;
                        // Teleport players to their start positions
                    } else if (playersInGame >= MIN_PLAYERS) {
                        if (this.countdown == -1)
                            this.countdown = COUNTDOWN_IN_SECONDS;

                    } else if (this.countdown == 0) {
                        this.state = GameState.PLAYING;
                        // Teleport players to their start positions


                    } else {
                        this.countdown--;
                    }
                    break;

                case PLAYING:
                    int playersInGame2 = Bukkit.getWorld("game").getPlayers().size(); // todo filter out spectators from actual players
                    if (playersInGame2 == 1) {
                        this.state = GameState.END;
                        this.countDown = COUNTDOWN_AFTER_GAME_IN_SECONDS;
                    }
                    break;
                case END:
                    if (countdown > 0)
                        countdown--;
                    else {
                        state = GameState.LOBBY;
                        countdown = -1;
                        // Kick out every player, game ends
                    }
                    break;
            }
        }

        public int getBlueKills() {
            return blueKills;
        }

        public void setBlueKills(int blueKills) {
            this.blueKills = blueKills;
        }

        public int getRedKills() {
            return redKills;
        }

        public void setRedKills(int redKills) {
            this.redKills = redKills;
        }
    }

    public GameState getState() {
        return this.state;
    }


}
