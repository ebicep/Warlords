package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.towerdefense.waves.TowerDefenseWave;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class TowerDefenseSpawner implements Option {

    private final Map<Location, List<TowerDefensePath>> paths = new HashMap<>(); // spawn location -> list of paths from that location
    private final List<TowerDefenseWave> waves = new ArrayList<>();
    private final List<BukkitTask> activeWaves = new ArrayList<>();
    private TowerDefenseOption towerDefenseOption;
    private ConcurrentHashMap<AbstractMob, TowerDefenseOption.TowerDefenseMobData> mobs;
    private int currentWave = 0; // index
    private Game game;

    public TowerDefenseSpawner init(TowerDefenseOption towerDefenseOption) {
        this.towerDefenseOption = towerDefenseOption;
        this.mobs = towerDefenseOption.getMobsMap();
        return this;
    }

    public TowerDefenseSpawner addPath(Location spawn, List<Location> path) {
        paths.computeIfAbsent(spawn, k -> new ArrayList<>()).add(new TowerDefensePath(spawn, path));
        return this;
    }

    public void renderPaths() {
        paths.forEach((spawnLocation, towerDefensePaths) -> {
            int yOffset = 0;
            for (TowerDefensePath towerDefensePath : towerDefensePaths) {
                int red = towerDefensePath.getRed();
                int green = towerDefensePath.getGreen();
                int blue = towerDefensePath.getBlue();
                List<Location> path = towerDefensePath.getPath();
                for (int i = 0; i < path.size() - 1; i++) {
                    Location loc1 = path.get(i).clone().add(0, yOffset, 0);
                    Location loc2 = path.get(i + 1).clone().add(0, yOffset, 0);
                    if (i == 0) {
                        Location spawnLoc = spawnLocation.clone().add(0, yOffset, 0);
                        EffectUtils.playParticleLinkAnimation(spawnLoc, loc1, red, green, blue, 1, 1);
                        EffectUtils.displayParticle(Particle.REDSTONE, spawnLoc.add(0, 1, 0), 3, new Particle.DustOptions(org.bukkit.Color.fromRGB(red, green, blue), 5));
                    }
                    EffectUtils.playParticleLinkAnimation(loc1, loc2, red, green, blue, 1, 1);
                    EffectUtils.displayParticle(Particle.REDSTONE, loc1.add(0, 1, 0), 1, new Particle.DustOptions(org.bukkit.Color.fromRGB(red, green, blue), 5));
                }
                yOffset++;
            }
        });
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        this.game.registerEvents(new Listener() {


            @EventHandler
            public void onMobSpawn(WarlordsMobSpawnEvent event) {
                AbstractMob mob = event.getMob();
                NPC npc = mob.getNpc();
                npc.data().set(NPC.Metadata.COLLIDABLE, false);
                npc.getDefaultGoalController().clear();
                npc.getNavigator().getDefaultParameters().distanceMargin(.75);
                TowerDefenseOption.TowerDefenseMobData mobData = mobs.get(mob);
                Pair<Double, Double> forward = getPathFromData(mobData).getForwardPath().get(0);
                Location nextTarget = getForwardLocation(npc, forward.getA() + .45, forward.getB());
                npc.getNavigator().setStraightLineTarget(nextTarget);

                System.out.println("Spawned: " + mob);
                if (Objects.equals(towerDefenseOption.getRandomSpawnLocation(Team.BLUE), mobData.getSpawnLocation())) {
                    System.out.println("BLUE");
                } else if (Objects.equals(towerDefenseOption.getRandomSpawnLocation(Team.RED), mobData.getSpawnLocation())) {
                    System.out.println("RED");
                } else {
                    System.out.println("?????");
                }
            }

            @EventHandler
            public void onNavigationComplete(NavigationCompleteEvent event) {
                NPC npc = event.getNPC();
                Entity entity = npc.getEntity();
                WarlordsEntity warlordsEntity = Warlords.getPlayer(entity);
                if (!(warlordsEntity instanceof WarlordsNPC warlordsNPC)) {
                    return;
                }
                if (!warlordsNPC.getGame().equals(game)) {
                    return;
                }
                AbstractMob mob = warlordsNPC.getMob();
                TowerDefenseOption.TowerDefenseMobData mobData = mobs.get(mob);
                if (mobData == null) {
                    return;
                }
                if (Objects.equals(towerDefenseOption.getRandomSpawnLocation(Team.BLUE), mobData.getSpawnLocation())) {
                    System.out.println("BLUE");
                } else if (Objects.equals(towerDefenseOption.getRandomSpawnLocation(Team.RED), mobData.getSpawnLocation())) {
                    System.out.println("RED");
                } else {
                    System.out.println("?????");
                }
                int lastWaypointIndex = mobData.getLastWaypointIndex();
                TowerDefensePath path = getPathFromData(mobData);
                List<Location> locations = path.getPath();
                if (lastWaypointIndex == locations.size() - 1) {
                    System.out.println("END");
                    return;
                }
                mobData.setLastWaypointIndex(lastWaypointIndex + 1);
                Pair<Double, Double> forward = path.getForwardPath().get(lastWaypointIndex + 1);
                LocationBuilder nextTarget = new LocationBuilder(npc.getStoredLocation())
                        .yaw(TowerDefenseUtils.getFastYaw(locations.get(lastWaypointIndex), locations.get(lastWaypointIndex + 1)))
                        .forward(forward.getA() + .45)
                        .addY(forward.getB());
                System.out.println(mobData);
                npc.getNavigator().setStraightLineTarget(nextTarget);
            }
        });
    }

    private TowerDefensePath getPathFromData(TowerDefenseOption.TowerDefenseMobData mobData) {
        return paths.get(mobData.getSpawnLocation()).get(mobData.getPathIndex());
    }

    private Location getForwardLocation(NPC npc, double xyAmount, double yAmount) {
        return new LocationBuilder(npc.getStoredLocation()).forward(xyAmount).addY(yAmount);
    }

    @Override
    public void start(@Nonnull Game game) {
        startCurrentWave();
    }

    /**
     * @param mob  mob to spawn
     * @param team team that spawned the mob - the mob will spawn on all other teams paths
     */
    public void spawnNewMob(AbstractMob mob, Team team) {
        Location randomSpawn = mob.getSpawnLocation();
        List<TowerDefensePath> pathList = paths.get(randomSpawn);
        if (pathList == null) {
            ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage("No path with given spawn: " + randomSpawn);
            return;
        }
        // get random path
        int randomPathIndex = ThreadLocalRandom.current().nextInt(pathList.size());
        Location spawnLocation = Objects.requireNonNull(randomSpawn).clone().add(
                ThreadLocalRandom.current().nextDouble(4) - 2,
                0,
                ThreadLocalRandom.current().nextDouble(4) - 2
        );
        mob.setSpawnLocation(spawnLocation);
        game.addNPC(mob.toNPC(game, team, warlordsNPC -> {}));
        mobs.put(mob, new TowerDefenseOption.TowerDefenseMobData(towerDefenseOption.getTicksElapsed(), randomSpawn, randomPathIndex));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    public TowerDefenseSpawner add(TowerDefenseWave wave) {
        waves.add(wave);
        return this;
    }

    public void startCurrentWave() {
        startWave(currentWave, () -> {
            currentWave++;
            startCurrentWave();
        });
    }

    public void startWave(int waveIndex, Runnable onWaveComplete) {
        if (waveIndex >= waves.size()) {
            return;
        }
        TowerDefenseWave towerDefenseWave = waves.get(waveIndex);
        activeWaves.add(new GameRunnable(game) {
            @Override
            public void run() {
                if (towerDefenseWave.tick(towerDefenseOption)) {
                    onWaveComplete.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0));
    }

}
