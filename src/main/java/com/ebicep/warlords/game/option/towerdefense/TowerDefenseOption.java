package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TowerDefenseOption implements PveOption {

    private final ConcurrentHashMap<AbstractMob, TowerDefenseMobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private Game game;
    private TowerBuildOption towerBuildOption;
    private TowerDefenseSpawner towerDefenseSpawner;

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        for (Option option : game.getOptions()) {
            if (option instanceof TowerBuildOption buildOption) {
                this.towerBuildOption = buildOption;
            } else if (option instanceof TowerDefenseSpawner spawner) {
                this.towerDefenseSpawner = spawner.init(this);
            }
        }
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {

            @Override
            public void run() {
                towerBuildOption.getBuiltTowers().forEach((tower, spawnTick) -> tower.whileActive(ticksElapsed.get() - spawnTick));
                ticksElapsed.incrementAndGet();
            }
        }.runTaskTimer(0, 0);
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
    public ConcurrentHashMap<AbstractMob, TowerDefenseMobData> getMobsMap() {
        return mobs;
    }

    @Override
    public void spawnNewMob(AbstractMob mob, Team team) {
        towerDefenseSpawner.spawnNewMob(mob, team);
    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        for (int i = 1; i < player.getAbilities().size(); i++) {
            player.getAbilities().remove(i);
            i--;
        }
        player.updateInventory(false);
    }


    public static class TowerDefenseMobData extends MobData {

        private final Location spawnLocation; // original spawn location with no offset
        private final int pathIndex; // index of whatever path its using
        private int lastWaypointIndex = 0;

        public TowerDefenseMobData(int spawnTick, Location spawnLocation, int pathIndex) {
            super(spawnTick);
            this.spawnLocation = spawnLocation;
            this.pathIndex = pathIndex;
        }

        public Location getSpawnLocation() {
            return spawnLocation;
        }

        public int getPathIndex() {
            return pathIndex;
        }

        public int getLastWaypointIndex() {
            return lastWaypointIndex;
        }

        public void setLastWaypointIndex(int lastWaypointIndex) {
            this.lastWaypointIndex = lastWaypointIndex;
        }
    }

}
