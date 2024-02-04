package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class TowerDefenseOption implements PveOption {

    private final ConcurrentHashMap<AbstractMob, TowerDefenseMobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private List<TowerDefensePath> paths = new ArrayList<>();
    private Game game;

    public TowerDefenseOption(List<TowerDefensePath> paths) {
        this.paths = paths;
    }

    public TowerDefenseOption(TowerDefensePath... paths) {
        this.paths = List.of(paths);
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        Location current = getRandomSpawnLocation(null);
        for (TowerDefensePath path : paths) {
            path.createForwardPath(current);
        }
        game.registerEvents(new Listener() {

            @EventHandler
            public void onMobSpawn(WarlordsMobSpawnEvent event) {
                AbstractMob mob = event.getMob();
                NPC npc = mob.getNpc();
                npc.data().set(NPC.Metadata.COLLIDABLE, false);
                npc.getDefaultGoalController().clear();
                npc.getNavigator().getDefaultParameters().distanceMargin(1);
                Location nextTarget = getForwardLocation(npc, paths.get(mobs.get(event.getMob()).pathIndex).getForwardPath().get(0));
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
                TowerDefenseMobData mobData = mobs.get(mob);
                if (mobData == null) {
                    return;
                }
                int lastWaypointIndex = mobData.getLastWaypointIndex();
                TowerDefensePath path = paths.get(mobs.get(mob).pathIndex);
                if (lastWaypointIndex == path.getPath().size() - 1) {
                    return;
                }
                mobData.setLastWaypointIndex(lastWaypointIndex + 1);
                LocationBuilder nextTarget = new LocationBuilder(npc.getStoredLocation())
                        .yaw(path.getPath().get(lastWaypointIndex).getYaw()) //TODO remove yaw requirement
                        .forward(path.getForwardPath().get(lastWaypointIndex + 1));
                npc.getNavigator().setStraightLineTarget(nextTarget);
            }

        });
    }

    private Location getForwardLocation(NPC npc, double amount) {
        return new LocationBuilder(npc.getStoredLocation()).forward(amount);
    }


    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            @Override
            public void run() {

            }
        }.runTaskTimer(0, 20);
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public Set<AbstractMob> getMobs() {
        return mobs.keySet();
    }

    @Override
    public int getTicksElapsed() {
        return ticksElapsed.get();
    }

    @Override
    public ConcurrentHashMap<AbstractMob, ? extends MobData> getMobsMap() {
        return mobs;
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        Location spawnLocation = Objects.requireNonNull(getRandomSpawnLocation(null)).add(
                ThreadLocalRandom.current().nextDouble(4) - 2,
                0,
                ThreadLocalRandom.current().nextDouble(4) - 2
        );
        mob.setSpawnLocation(spawnLocation);
        game.addNPC(mob.toNPC(game, team, warlordsNPC -> {}));
        mobs.put(mob, new TowerDefenseMobData(ticksElapsed.get(), ThreadLocalRandom.current().nextInt(paths.size())));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }

    public List<TowerDefensePath> getPaths() {
        return paths;
    }

    public static class TowerDefenseMobData extends MobData {

        private final int pathIndex; // index of whatever path its using
        private int lastWaypointIndex = 0;

        public TowerDefenseMobData(int spawnTick, int pathIndex) {
            super(spawnTick);
            this.pathIndex = pathIndex;
        }

        public int getLastWaypointIndex() {
            return lastWaypointIndex;
        }

        public void setLastWaypointIndex(int lastWaypointIndex) {
            this.lastWaypointIndex = lastWaypointIndex;
        }
    }

    public static class TowerDefensePath {

        private final List<Location> path;
        private final List<Double> forwardPath = new ArrayList<>();

        public TowerDefensePath(List<Location> path) {
            this.path = path;
        }

        public void createForwardPath(Location current) {
            for (Location location : path) {
                forwardPath.add(current.distance(location));
                current = location;
            }
        }

        public List<Double> getForwardPath() {
            return forwardPath;
        }

        public List<Location> getPath() {
            return path;
        }
    }
}
