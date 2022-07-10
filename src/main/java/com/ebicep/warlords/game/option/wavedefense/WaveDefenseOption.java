package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.customentities.nms.pve.CustomEntity;
import com.ebicep.warlords.events.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BoundingBoxOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
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
    private BukkitTask spawner;

    public WaveDefenseOption(Team team, WaveList waves) {
        this.team = team;
        this.waves = waves;
    }

    public WarlordsEntity spawn(CustomEntity<?> entity) {
        WarlordsEntity we = game.addNPC(new WarlordsNPC(
                UUID.randomUUID(),
                entity.toString(),
                Weapons.ABBADON,
                (LivingEntity) entity,
                game,
                Team.RED,
                Specializations.PYROMANCER
        ));

        entities.add(we);
        currentWave.spawnMonster(we.getLocation()).toNPC(game, team, UUID.randomUUID());
        startSpawnTask();
        return we;
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
            PacketUtils.sendTitle(entry.getKey(),wavePrefix + waveCounter, "", 20, 60, 20);
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
                WarlordsEntity we = event.getPlayer();
                entities.remove(we);
                if (we.getEntity() instanceof CustomEntity) {
                    new GameRunnable(game) {
                        @Override
                        public void run() {
                            ((CustomEntity) we.getEntity()).onDeath((EntityInsentient) we.getEntity(), we.getDeathLocation(), WaveDefenseOption.this);
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
                        .map(e -> e.getName() + ": " + ChatColor.RED + "⚔ " + e.getMinuteStats().total().getKills()
                                + ChatColor.RESET + " / " + ChatColor.AQUA + "❂ " + e.getCurrency())
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
            @Override
            public void run() {
                if (entities.isEmpty() && spawnCount == 0) {
                    newWave();

                    if (waveCounter > 1) {
                        getGame().forEachOnlineWarlordsEntity(we -> {
                            we.addCurrency(100);
                            we.sendMessage(ChatColor.AQUA + "+100 ❂ Upgrade Insignia");
                        });
                    }
                }

                if (waveCounter == Integer.MAX_VALUE) {
                    game.setNextState(new EndState(game, null));
                }
            }
        }.runTaskTimer(20, 0);
    }

    public Set<WarlordsEntity> getEntities() {
        return entities;
    }

    public int getWaveCounter() {
        return waveCounter;
    }

    public Wave getCurrentWave() {
        return currentWave;
    }

    public WaveList getWaves() {
        return waves;
    }

    public void setWaveCounter(int waveCounter) {
        this.waveCounter = waveCounter;
        newWave();
    }
}
