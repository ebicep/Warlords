package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BoundingBoxOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import com.ebicep.warlords.game.option.wavedefense.waves.Wave;
import com.ebicep.warlords.game.option.wavedefense.waves.WaveList;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;
import static com.ebicep.warlords.util.warlords.Utils.iterable;


public class WaveDefenseOption implements Option {
    private static final int SCOREBOARD_PRIORITY = 5;
    private final Set<AbstractMob<?>> mobs = new HashSet<>();
    private final Team team;
    private final WaveList waves;
    SimpleScoreboardHandler scoreboard;
    private int waveCounter = 0;
    private int maxWave = 10000;
    private int spawnCount = 0;
    private Wave currentWave;
    @Nonnull
    private Game game;
    private Location lastLocation = new Location(null, 0, 0, 0);
    @Nullable
    private BukkitTask spawner;

    public WaveDefenseOption(Team team, WaveList waves) {
        this.team = team;
        this.waves = waves;
    }

    public WaveDefenseOption(Team team, WaveList waves, int maxWave) {
        this.team = team;
        this.waves = waves;
        this.maxWave = maxWave;
    }

    public void startSpawnTask() {
        if (spawner != null) {
            spawner.cancel();
            spawner = null;
        }

        if (spawnCount == 0) {
            return;
        }

        spawner = new GameRunnable(game) {
            WarlordsEntity lastSpawn = null;
            int counter = 0;

            private Location getSpawnLocation(WarlordsEntity entity) {
                List<Location> candidates = new ArrayList<>();
                double priority = Double.NEGATIVE_INFINITY;
                for (SpawnLocationMarker marker : getGame().getMarkers(SpawnLocationMarker.class)) {
                    if (candidates.isEmpty()) {
                        candidates.add(marker.getLocation());
                        priority = marker.getPriority(entity);
                    } else {
                        double newPriority = marker.getPriority(entity);
                        if (newPriority >= priority) {
                            if (newPriority > priority) {
                                candidates.clear();
                                priority = newPriority;
                            }
                            candidates.add(marker.getLocation());
                        }
                    }
                }
                if (!candidates.isEmpty()) {
                    return candidates.get((int) (Math.random() * candidates.size()));
                }
                return lastLocation;
            }

            public WarlordsEntity spawn(Location loc) {
                AbstractMob<?> abstractMob = currentWave.spawnRandomMonster(loc);
                mobs.add(abstractMob);
                return abstractMob.toNPC(game, team, UUID.randomUUID());
            }

            @Override
            public void run() {
                counter++;
                if (lastSpawn == null) {
                    lastSpawn = spawn(lastLocation);
                    if (lastSpawn != null) {
                        Location newLoc = getSpawnLocation(lastSpawn);
                        lastSpawn.teleport(newLoc);
                        lastSpawn.getLocation(lastLocation);
                    }
                } else {
                    lastSpawn = spawn(getSpawnLocation(lastSpawn));
                    lastSpawn.getLocation(lastLocation);
                }

                spawnCount--;
                if (spawnCount <= 0) {
                    spawner.cancel();
                    spawner = null;
                }
            }

        }.runTaskTimer(currentWave.getDelay(), 13);
    }

    public void newWave() {
        if (currentWave != null) {
            String message;
            if (currentWave.getMessage() != null) {
                message = ChatColor.GREEN + "Wave complete! (" + currentWave.getMessage() + ")";
            } else {
                message = ChatColor.GREEN + "Wave complete!";
            }

            for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
                sendMessage(entry.getKey(), false, message);
                entry.getKey().playSound(entry.getKey().getLocation(), Sound.LEVEL_UP, 500, 2);
            }
        }
        waveCounter++;
        currentWave = waves.getWave(waveCounter, new Random());
        spawnCount = currentWave.getMonsterCount();

        for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
            if (currentWave.getMessage() != null) {
                sendMessage(
                        entry.getKey(),
                        false,
                        ChatColor.YELLOW + "A boss will spawn in §c" + currentWave.getDelay() / 20 + " §eseconds!"
                );
            } else {
                sendMessage(
                        entry.getKey(),
                        false,
                        ChatColor.YELLOW + "A wave of §c§l" +
                                spawnCount + "§e monsters will spawn in §c" +
                                currentWave.getDelay() / 20 + " §eseconds!"
                );
            }

            float soundPitch = 0.8f;
            String wavePrefix = "§eWave ";
            if (waveCounter >= 20) {
                soundPitch = 0.75f;
                wavePrefix = "§eWave ";
            }
            if (waveCounter >= 40) {
                soundPitch = 0.7f;
                wavePrefix = "§6Wave ";
            }
            if (waveCounter >= 60) {
                soundPitch = 0.65f;
                wavePrefix = "§7Wave ";
            }
            if (waveCounter >= 80) {
                soundPitch = 0.5f;
                wavePrefix = "§8§lWave ";
            }
            if (waveCounter >= 100) {
                soundPitch = 0.4f;
                wavePrefix = "§d§lWave ";
            }
            if (waveCounter >= 150) {
                soundPitch = 0.3f;
                wavePrefix = "§5§lWave ";
            }
            if (waveCounter >= 200) {
                soundPitch = 0.2f;
                wavePrefix = "§5W§5§k§la§5§lve ";
            }
            if (waveCounter >= 250) {
                soundPitch = 0.1f;
                wavePrefix = "§4W§4§k§la§4§lve ";
            }
            if (waveCounter >= 300) {
                wavePrefix = "§0W§0§k§la§0§lv§0§k§le§4§l ";
            }

            entry.getKey().playSound(entry.getKey().getLocation(), Sound.WITHER_SPAWN, 500, soundPitch);
            PacketUtils.sendTitle(entry.getKey(), wavePrefix + waveCounter, "", 20, 60, 20);
        }
        startSpawnTask();
    }

    @Override
    public void register(Game game) {
        this.game = game;
        for (Option o : game.getOptions()) {
            if (o instanceof BoundingBoxOption) {
                BoundingBoxOption boundingBoxOption = (BoundingBoxOption) o;
                lastLocation = boundingBoxOption.getCenter();
            }
        }
        game.registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                WarlordsEntity attacker = event.getAttacker();
                WarlordsEntity receiver = event.getPlayer();
                for (AbstractMob<?> mob : mobs) {
                    if (mob.getWarlordsNPC().equals(attacker) && event.isDamageInstance()) {
                        mob.onAttack(attacker, receiver);
                    }
                }

                for (AbstractMob<?> mob : mobs) {
                    if (mob.getWarlordsNPC().equals(receiver) && event.isDamageInstance()) {
                        mob.onDamageTaken(receiver, attacker);
                    }
                }
            }

            @EventHandler
            public void onEvent(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getPlayer();
                AbstractMob<?> removed = null;
                for (AbstractMob<?> mob : mobs) {
                    if (mob.getWarlordsNPC().equals(we)) {
                        removed = mob;
                        mobs.remove(mob);
                        break;
                    }
                }
                if (removed != null) {
                    AbstractMob<?> finalRemoved = removed;
                    new GameRunnable(game) {
                        @Override
                        public void run() {
                            finalRemoved.onDeath(event.getKiller(), we.getDeathLocation(), WaveDefenseOption.this);
                            game.removePlayer(we.getUuid());
                        }
                    }.runTask();
                }
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {
                return Collections.singletonList(
                        "Wave: " + ChatColor.GREEN + waveCounter + ChatColor.RESET + (maxWave == 50 ? "/" + ChatColor.GREEN + maxWave : "") +
                                ChatColor.RESET + (currentWave != null && currentWave.getMessage() != null ? " (" + currentWave.getMessage() + ")" : "")
                );
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {
                return Collections.singletonList("Monsters left: " + ChatColor.GREEN + mobs.size());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(6, "kills") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {

                return PlayerFilter.playingGame(game)
                        .filter(e -> e instanceof WarlordsPlayer)
                        .stream()
                        .map(e -> e.getName() + ": " + (e.isDead() ? ChatColor.DARK_RED + "DEAD" : ChatColor.RED + "❤ " + (int) e.getHealth()) +
                                ChatColor.RESET + " / " + ChatColor.RED + "⚔ " + e.getMinuteStats().total().getKills())
                        .collect(Collectors.toList());
            }
        });

        new TimerSkipAbleMarker() {
            @Override
            public int getDelay() {
                return 0;
            }

            @Override
            public void skipTimer(int delay) {
                newWave();
            }

        }.register(game);
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            int counter = 0;

            @Override
            public void run() {
                if (mobs.isEmpty() && spawnCount == 0) {
                    newWave();

                    if (waveCounter > 1) {
                        getGame().forEachOnlineWarlordsEntity(we -> {
                            if (we instanceof WarlordsPlayer) {
                                int currency;
                                if (waveCounter % 10 == 1) {
                                    currency = 1000;
                                } else {
                                    currency = 100;
                                }
                                we.addCurrency(currency);
                                we.sendMessage(ChatColor.AQUA + "+" + currency + " ❂ Insignia");
                            }
                        });
                    }
                }

                for (AbstractMob<?> mob : mobs) {
                    mob.whileAlive(counter);
                }


                if (waveCounter > maxWave) {
                    game.setNextState(new EndState(game, null));
                    this.cancel();
                }
                counter++;
            }
        }.runTaskTimer(20, 0);
    }

    public Set<AbstractMob<?>> getMobs() {
        return mobs;
    }

    public int getWaveCounter() {
        return waveCounter;
    }

    public void setWaveCounter(int waveCounter) {
        this.waveCounter = waveCounter - 1;
        newWave();
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public WaveList getWaves() {
        return waves;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    public int getMaxWave() {
        return maxWave;
    }

    public void setMaxWave(int maxWave) {
        this.maxWave = maxWave;
    }

    public int getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(int spawnCount) {
        this.spawnCount = spawnCount;
    }
}
