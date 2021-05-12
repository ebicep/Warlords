package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    // MAPS:
    // "Crossfire"
    // "Rift"
    // "Atherrough_Valley"
    // "Warsong"
    // "Gorge"

    public enum GameMap {
        RIFT(new Map(
                "Rift",
                24,
                1,
                900, // seconds
                30, // seconds
                "",

                new Location(Bukkit.getWorld("TestWorld"), -32.5, 25.5, 49.5), // BLUE DAMAGE
                new Location(Bukkit.getWorld("TestWorld"), 33.5, 25.5, -48.5), // RED DAMAGE

                new Location(Bukkit.getWorld("Rift"), -54.5, 36.5, 24.5), // BLUE SPEED
                new Location(Bukkit.getWorld("Rift"), 55.5, 36.5, -23.5), // RED SPEED

                new Location(Bukkit.getWorld("Rift"), -0.5, 24.5, 64.5), // BLUE HEALING
                new Location(Bukkit.getWorld("Rift"), 1.5, 24.5, -62.5), // RED HEALING

                new Location(Bukkit.getWorld("Rift"), -86.5, 45.5, -33.5),// BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("Rift"), 87, 45.5, -35.5), // RED LOBBY SPAWN

                new Location(Bukkit.getWorld("Rift"), -32.5, 34.5, -43.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("Rift"), 34.5, 34.5, 42.5), // RED RESPAWN

                new Location(Bukkit.getWorld("Rift"), -98.5, 45.5, -17.5), // BLUE FLAG
                new Location(Bukkit.getWorld("Rift"), 99.5, 45.5, 17.5) // RED FLAG
        )),

        CROSSFIRE(new Map(
                "Crossfire",
                24,
                1,
                900, // seconds
                30, // seconds
                "",

                new Location(Bukkit.getWorld("Crossfire"), 158.5, 6.5, 28.5), // BLUE DAMAGE
                new Location(Bukkit.getWorld("Crossfire"), 65.5, 6.5, 97.5), // RED DAMAGE

                new Location(Bukkit.getWorld("Crossfire"), 217.5, 36.5, 89.5), // BLUE SPEED
                new Location(Bukkit.getWorld("Crossfire"), 6.5, 36.5, 39.5), // RED SPEED

                new Location(Bukkit.getWorld("Crossfire"), 96.5, 6.5, 108.5), // BLUE HEALING
                new Location(Bukkit.getWorld("Crossfire"), 126.5, 6.5, 19.5), // RED HEALING

                new Location(Bukkit.getWorld("Crossfire"), -86.5, 45.5, -33.5),// BLUE LOBBY SPAWN
                new Location(Bukkit.getWorld("Crossfire"), 87, 45.5, -35.5), // RED LOBBY SPAWN

                // placeholder locations
                new Location(Bukkit.getWorld("Crossfire"), -32.5, 34.5, -43.5), // BLUE RESPAWN
                new Location(Bukkit.getWorld("Crossfire"), 34.5, 34.5, 42.5), // RED RESPAWN

                new Location(Bukkit.getWorld("Crossfire"), -98.5, 45.5, -17.5), // BLUE FLAG
                new Location(Bukkit.getWorld("Crossfire"), 99.5, 45.5, 17.5) // RED FLAG
                // placeholder locations
        ));


        // TODO: add other maps

        public final Map map;

        GameMap(Map map) {
            this.map = map;
        }


    }

    public class GameManager1 {

    }

    static class Game implements Runnable {

        private static final int POINT_LIMIT = 1000;

        enum State {
            PRE_GAME {
                @Override
                public void begin(Game game) {
                    game.timer = 0;
                    game.redPoints = 0;
                    game.bluePoints = 0;
                    game.forceEnd = false;
                    game.teamBlue.clear();
                    game.teamRed.clear();
                    // Repair gates
                    // Repair map damage (remove powerups)
                }

                @Override
                public State run(Game game) {
                    int players = game.teamBlue.size() + game.teamRed.size();
                    if (players > game.map.getMinPlayers()) {
                        game.timer++;
                        int total = game.map.getCountdownTimerInSeconds();
                        int remaining = total - game.timer;
                        if (game.timer == total) {
                            return GAME;
                        }
                    } else {
                        game.timer = 0;
                    }
                    return null;
                }

            },
            GAME {
                @Override
                public void begin(Game game) {
                    game.timer = 0;
                    // Close config screen
                    // Set max energy
                    // Set max health
                }

                @Override
                public State run(Game game) {
                    game.timer++;
                    if (
                            game.bluePoints >= POINT_LIMIT || game.redPoints >= POINT_LIMIT || game.timer >= game.map.getGameTimerInSeconds() * 20 || game.forceEnd
                    ) {
                        return END;
                    }
                    if (game.timer == 10 * 20) {
                        // Destroy gates
                        // Enable abilities
                    } else if (game.timer == 70 * 20) {
                        // Enable powerups
                    } else if (game.timer % 12 * 20 == 1) {
                        // Respawn wave
                    }
                    return null;
                }

            },
            END {
                @Override
                public void begin(Game game) {
                    // Remove entities
                    // Disable abilities
                    game.timer = 0;
                    boolean teamBlueWins = !game.forceEnd && game.bluePoints > game.redPoints;
                    boolean teamReadWins = !game.forceEnd && game.redPoints > game.bluePoints;
                    // Announce winner
                }

                @Override
                public State run(Game game) {
                    game.timer++;
                    if (game.timer > 10 * 20) {
                        // Teleport players back
                        return PRE_GAME;
                    }
                    return null;
                }
            },
            ;

            /**
             * Run a tick of the game
             *
             * @param game The current game instance
             * @return null if no change needs to be made, a new State if the state needs to be changed
             */
            public abstract State run(Game game);

            public abstract void begin(Game game);
        }

        private State state = State.PRE_GAME;
        private int timer = 0;
        private Map map;
        private final Set<UUID> teamRed = new HashSet<>();
        private final Set<UUID> teamBlue = new HashSet<>();
        private int redPoints;
        private int bluePoints;
        private boolean forceEnd;

        public boolean canChangeMap() {
            return teamBlue.isEmpty() && teamRed.isEmpty() && state == State.PRE_GAME;
        }

        public void changeMap(Map map) {
            if (!canChangeMap()) {
                throw new IllegalStateException("cannot change map");
            }
            this.map = map;
        }

        public void addPlayer(UUID id, boolean teamBlue) {
            if (teamBlue) {
                this.teamRed.remove(id);
                this.teamBlue.add(id);
            } else {
                this.teamBlue.remove(id);
                this.teamRed.add(id);
            }
        }

        public void removePlayer(UUID id) {
            this.teamRed.remove(id);
            this.teamBlue.remove(id);
        }

        @Override
        public void run() {
            State newState = state.run(this);
            if (newState != null) {
                this.state = newState;
                newState.begin(this);
            }
        }
    }
}

/*
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
}*/
