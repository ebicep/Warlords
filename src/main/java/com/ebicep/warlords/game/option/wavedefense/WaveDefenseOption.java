package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BoundingBoxOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.player.WarlordsEntity;
import com.ebicep.warlords.player.WarlordsPlayer;
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
    SimpleScoreboardHandler scoreboard;
    @Nullable
    private  BukkitTask spawner;

    public WaveDefenseOption(Team team, WaveList waves) {
        this.team = team;
        this.waves = waves;
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
            
        }.runTaskTimer(currentWave.getDelay(), 1);
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
                entry.getKey().playSound(entry.getKey().getLocation(), Sound.AMBIENCE_THUNDER, 500, 2);
            }
        }
        waveCounter++;
        currentWave = waves.getWave(waveCounter, random);
        spawnCount = currentWave.getMonsterCount();
            
        for (Map.Entry<Player, Team> entry : iterable(game.onlinePlayers())) {
            sendMessage(
                    entry.getKey(),
                    false,
                    ChatColor.YELLOW + "A wave of §c§l" +
                    spawnCount + "§e monsters will spawn in §c" +
                    currentWave.getDelay() / 20 + " §eseconds!"
            );
            entry.getKey().playSound(entry.getKey().getLocation(), Sound.WITHER_SPAWN, 500, 0.8f);
            PacketUtils.sendTitle(entry.getKey(), "§e§lWave §c§l" + waveCounter, "", 20, 60, 20);
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
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {
                return Collections.singletonList(
                        "Wave: " + ChatColor.GREEN + ChatColor.BOLD + waveCounter + ChatColor.RESET + (currentWave != null && currentWave.getMessage() != null ? " (" + currentWave.getMessage() + ")" : "")
                );
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(SCOREBOARD_PRIORITY, "wave") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {
                return Collections.singletonList(
                        "Monsters left: " + ChatColor.GREEN + entities.size()
                );
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(6, "kills") {
            @Override
            public List<String> computeLines(@Nullable WarlordsEntity player) {
                return PlayerFilter.playingGame(game)
                        .filter(e -> e instanceof WarlordsPlayer)
                        .stream()
                        .map(e -> e.getName() + ": " + ChatColor.RED + e.getMinuteStats().total().getKills())
                        .collect(Collectors.toList());
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

                    if (waveCounter % 10 == 0) {
                        PlayerFilter.playingGame(getGame())
                            .filter(warlordsEntity -> warlordsEntity instanceof WarlordsPlayer)
                            .forEach(we -> {
                                we.playSound(we.getLocation(), Sound.CHEST_OPEN, 500, 0.7f);
                                ((WarlordsPlayer) we).getAbilityTree().openAbilityTree();
                            });
                    }
                }
            }
        }.runTaskTimer(20, 0);
    }
    
    
    
    
}
