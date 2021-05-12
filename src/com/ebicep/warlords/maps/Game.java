package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.powerups.PowerupManager;
import com.ebicep.warlords.util.CustomScoreboard;
import com.ebicep.warlords.util.RemoveEntities;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class Game implements Runnable {

    private static final int POINT_LIMIT = 1000;

    public enum State {
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
            public Game.State run(Game game) {
                int players = game.teamBlue.size() + game.teamRed.size();
                if (players > game.map.getMinPlayers()) {
                    game.timer++;
                    int total = game.map.getCountdownTimerInTicks();
                    int remaining = total - game.timer;
                    if(remaining % 20 == 1) {
                        Bukkit.broadcastMessage("Gamestate PRE_GAME, remaining time: " + remaining / 20);
                    }
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
                List<String> blueTeam = new ArrayList<>();
                List<String> redTeam = new ArrayList<>();
                List<CustomScoreboard> customScoreboards = new ArrayList<>();

                RemoveEntities removeEntities = new RemoveEntities();
                removeEntities.onRemove();

                World world = Bukkit.getWorld(game.map.mapName);
                for (int i = 0; i < world.getPlayers().size(); i = i + 2) {
                    Player worldPlayer = world.getPlayers().get(i);
                    Warlords.addPlayer(new WarlordsPlayer(worldPlayer, worldPlayer.getName(), worldPlayer.getUniqueId(), new Berserker(worldPlayer), false));
                    worldPlayer.setMaxHealth(40);
                    //player.teleport(GameManager.GameMap.RIFT.map.getBlueLobbySpawnPoint());
                    blueTeam.add(worldPlayer.getName());
                    System.out.println("Added " + worldPlayer.getName());

                    if (i + 1 < world.getPlayers().size()) {
                        Player worldPlayer2 = world.getPlayers().get(i + 1);
                        Warlords.addPlayer(new WarlordsPlayer(worldPlayer2, worldPlayer2.getName(), worldPlayer2.getUniqueId(), new Defender(worldPlayer2), false));
                        worldPlayer2.setMaxHealth(40);
                        redTeam.add(worldPlayer.getName());
                        System.out.println("Added2 " + worldPlayer2.getName());
                    }

                    worldPlayer.setLevel((int) Warlords.getPlayer(worldPlayer).getMaxEnergy());
                    Warlords.getPlayer(worldPlayer).assignItemLore();
                }

                for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                    System.out.println("updated scoreboard for " + value.getName());
                    value.setScoreboard(new CustomScoreboard(value.getPlayer(), blueTeam, redTeam));
                }

                new PowerupManager(game.map).runTaskTimer(Warlords.getInstance(), 0, 0);

                SpawnFlag flag = new SpawnFlag();
                flag.spawnFlag(game.map);
            }

            @Override
            public Game.State run(Game game) {
                game.timer++;
                if (
                        game.bluePoints >= POINT_LIMIT || game.redPoints >= POINT_LIMIT || game.timer >= game.map.getGameTimerInTicks() * 20 || game.forceEnd
                ) {
                    return END;
                }
                if (game.timer == 10 * 20) {
                    // Destroy gates
                    // Enable abilities
                } else if (game.timer == 70 * 20) {
                    // Enable powerups
                }

                if (game.timer % 100 == 0) {
                   int remaining = (game.map.getGameTimerInTicks() - game.timer) / 20;
                   int minutes = remaining / 60;
                   int seconds = remaining % 60;

                   Bukkit.broadcastMessage(minutes + ":" + seconds + "remaining.");
                }

                return null;
            }

        },
        END {
            @Override
            public void begin(Game game) {
                Bukkit.broadcastMessage("The game has ended!");
                // Disable abilities
                game.timer = 0;
                boolean teamBlueWins = !game.forceEnd && game.bluePoints > game.redPoints;
                boolean teamReadWins = !game.forceEnd && game.redPoints > game.bluePoints;
                // Announce winner
            }

            @Override
            public Game.State run(Game game) {
                game.timer++;
                if (game.timer > 10 * 20) {
                    for(UUID id : game.teamBlue) {
                        Player player = Bukkit.getPlayer(id);
                        if(player != null) {
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        }
                    }
                    for(UUID id : game.teamRed) {
                        Player player = Bukkit.getPlayer(id);
                        if(player != null) {
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        }
                    }
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
        public abstract Game.State run(Game game);

        public abstract void begin(Game game);
    }

    private Game.State state = Game.State.PRE_GAME;
    private int timer = 0;
    private GameMap map = GameMap.RIFT;
    private final Set<UUID> teamRed = new HashSet<>();
    private final Set<UUID> teamBlue = new HashSet<>();
    private int redPoints;
    private int bluePoints;
    private boolean forceEnd;

    public void forceDraw() {
        this.forceEnd = true;
    }

    public State getState() {
        return state;
    }

    public int getTimer() {
        return timer;
    }

    public GameMap getMap() {
        return map;
    }

    public Set<UUID> getTeamRed() {
        return teamRed;
    }

    public Set<UUID> getTeamBlue() {
        return teamBlue;
    }

    public int getRedPoints() {
        return redPoints;
    }

    public int getBluePoints() {
        return bluePoints;
    }

    public boolean isForceEnd() {
        return forceEnd;
    }

    public boolean canChangeMap() {
        return teamBlue.isEmpty() && teamRed.isEmpty() && state == Game.State.PRE_GAME;
    }

    public void changeMap(GameMap map) {
        if (!canChangeMap()) {
            throw new IllegalStateException("cannot change map");
        }
        this.map = map;
    }

    public void addPlayer(Player player, boolean teamBlue) {
        if (teamBlue) {
            this.teamRed.remove(player.getUniqueId());
            this.teamBlue.add(player.getUniqueId());
            player.teleport(this.map.blueLobbySpawnPoint);
        } else {
            this.teamBlue.remove(player.getUniqueId());
            this.teamRed.add(player.getUniqueId());
            player.teleport(this.map.redLobbySpawnPoint);
        }
    }

    public void removePlayer(UUID id) {
        this.teamRed.remove(id);
        this.teamBlue.remove(id);
    }

    @Override
    public void run() {
        Game.State newState = state.run(this);
        if (newState != null) {
            this.state = newState;
            newState.begin(this);
        }
    }
}