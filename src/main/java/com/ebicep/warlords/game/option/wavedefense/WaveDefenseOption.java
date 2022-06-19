package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BoundingBoxOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.player.WarlordsEntity;
import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;
import com.ebicep.warlords.util.warlords.GameRunnable;
import static com.ebicep.warlords.util.warlords.Utils.iterable;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;


public class WaveDefenseOption implements Option {
    private final Set<WarlordsEntity> entities = new HashSet<>();
    private int waveCounter = 0;
    private int spawnCount = 0;
    private Wave currentWave;
    private final Team team;
    private final WaveList waves;
    private final Random random = new Random();
    @Nonnull
    private Game game;
    private Location lastLocation = new Location(null, 0, 0, 0);

    @Nullable BukkitTask spawner;

    public WaveDefenseOption(Team team, WaveList waves) {
        this.team = team;
        this.waves = waves;
    }
    
    public void startSpawnTask() {
        if(spawner != null) {
            spawner.cancel();
            spawner = null;
        }
        if(spawnCount == 0) {
            return;
        }
        spawner = new GameRunnable(game) {
            WarlordsEntity lastSpawn = null;
            
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
                WarlordsEntity we = currentWave.spawnRandomMonster(loc, random).toNPC(game, team, UUID.randomUUID());
                entities.add(we);
                return we;
            }
            @Override
            public void run() {
                if(lastSpawn == null) {
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
            
        }.runTaskTimer(1, 1);
    }

    public void newWave() {
        waveCounter++;
        currentWave = waves.getWave(waveCounter, random);
        spawnCount = currentWave.getMonsterCount();
        String message;
        if (currentWave.getMessage() != null) {
            message = "Wave: " + waveCounter + " (" + currentWave.getMessage() + ")";
        } else {
            message = "Wave: " + waveCounter;
        }
            
        for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
            sendMessage(entry.getKey(), false, message);
            sendMessage(entry.getKey(), false, "Spawning " + spawnCount + " enemies...");
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
            public void onEvent(WarlordsDeathEvent event) {
                entities.remove(event.getPlayer());
            }
        });
    }
    
    @Override
    public void start(@Nonnull Game game) {

        new GameRunnable(game) {
            @Override
            public void run() {
                if (entities.isEmpty() && spawnCount == 0) {
                    newWave();
                }
            }
        }.runTaskTimer(20, 0);
    }
    
    
    
    
}
