package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
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
                Pair<Double, Double> forward = getPathFromData(mobs.get(mob)).getForwardPath().get(0);
                Location nextTarget = getForwardLocation(npc, forward.getA() + .5, forward.getB());
                npc.getNavigator().setStraightLineTarget(nextTarget);
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
                int lastWaypointIndex = mobData.getLastWaypointIndex();
                TowerDefensePath path = getPathFromData(mobData);
                if (lastWaypointIndex == path.getPath().size() - 1) {
                    return;
                }
                mobData.setLastWaypointIndex(lastWaypointIndex + 1);
                Pair<Double, Double> forward = path.getForwardPath().get(lastWaypointIndex + 1);
                LocationBuilder nextTarget = new LocationBuilder(npc.getStoredLocation())
                        .yaw(TowerDefenseUtils.getFastYaw(path.getPath().get(lastWaypointIndex), path.getPath().get(lastWaypointIndex + 1)))
                        .forward(forward.getA() + .5)
                        .addY(forward.getB());
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

    public void spawnNewMob(AbstractMob mob, Team team) {
        for (Team t : TeamMarker.getTeams(game)) {
            if (t == team) {
                continue;
            }
            Location randomSpawn = towerDefenseOption.getRandomSpawnLocation(t);
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
