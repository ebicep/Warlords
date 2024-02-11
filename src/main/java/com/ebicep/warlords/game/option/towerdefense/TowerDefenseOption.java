package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.game.option.towerdefense.events.TowerDefenseCastleDestroyEvent;
import com.ebicep.warlords.game.option.towerdefense.events.TowerDefenseMobCompletePathEvent;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TowerDefenseOption implements PveOption, Listener {

    private final ConcurrentHashMap<AbstractMob, TowerDefenseMobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private final Map<Team, TowerDefenseCastle> castles = new HashMap<>();
    private Game game;
    private TowerBuildOption towerBuildOption;
    private TowerDefenseSpawner towerDefenseSpawner;

    public TowerDefenseOption addTower(Team team, Location location, float maxHealth) {
        castles.put(team, new TowerDefenseCastle(team, location, maxHealth));
        return this;
    }

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
        game.registerEvents(this);
    }

    @EventHandler
    public void onMobCompletePath(TowerDefenseMobCompletePathEvent event) {
        TowerDefenseMobData mobData = mobs.get(event.getMob());
        if (mobData == null) {
            return;
        }
        mobData.setAttackingCastle(true);
    }

    @Override
    public void start(@Nonnull Game game) {
        castles.values().forEach(TowerDefenseCastle::displayInit);
        new GameRunnable(game) {

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                towerBuildOption.getBuiltTowers().forEach((tower, spawnTick) -> tower.whileActive(ticksElapsed.get() - spawnTick));
                ticksElapsed.incrementAndGet();
                mobTick();
                if (ticksElapsed.get() % 5 == 0) {
                    towerDefenseSpawner.renderPaths();
                }
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public void mobTick() {
        Set<Team> destroyedCastleTeams = new HashSet<>();
        for (Map.Entry<AbstractMob, TowerDefenseMobData> entry : mobs.entrySet()) {
            AbstractMob mob = entry.getKey();
            TowerDefenseMobData towerDefenseMobData = entry.getValue();
            mob.whileAlive(ticksElapsed.get() - towerDefenseMobData.getSpawnTick(), this);
            mob.activateAbilities();
            if (towerDefenseMobData.isAttackingCastle()) {
                Team attackingTeam = towerDefenseMobData.getAttackingTeam();
                TowerDefenseCastle castle = castles.get(attackingTeam);
                if (castle == null) {
                    ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage("Castle for team " + attackingTeam + " is null");
                    continue;
                }
                if (castle.isDestroyed()) {
                    continue;
                }
                if (castle.takeDamage(mob)) {
                    destroyedCastleTeams.add(attackingTeam);
                    Bukkit.getPluginManager().callEvent(new TowerDefenseCastleDestroyEvent(game, castle));
                }
            }
        }
        if (destroyedCastleTeams.isEmpty()) {
            return;
        }
        for (Team destroyedCastleTeam : destroyedCastleTeams) {
            mobs.entrySet()
                .removeIf(entry -> {
                    AbstractMob mob = entry.getKey();
                    boolean remove = entry.getValue().attackingTeam == destroyedCastleTeam;
                    if (remove) {
                        mob.getWarlordsNPC().cleanup();
                        getGame().getPlayers().remove(mob.getWarlordsNPC().getUuid());
                        Warlords.removePlayer(mob.getWarlordsNPC().getUuid());
                    }
                    return remove;
                });
        }
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
        Location spawnLocation = mob.getSpawnLocation();
        Team attackingTeam = towerDefenseSpawner.getTeamSpawnLocations().get(spawnLocation);
        if (attackingTeam == null) {
            ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage("No team with given spawn: " + spawnLocation);
            return;
        }
        TowerDefenseCastle castle = castles.get(attackingTeam);
        if (castle == null) {
            ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage("No castle for team: " + attackingTeam);
            return;
        }
        if (castle.isDestroyed()) {
            return;
        }
        towerDefenseSpawner.spawnNewMob(mob, team);
    }

    @Override
    public PveRewards<?> getRewards() {
        return null;
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        castles.values().forEach(TowerDefenseCastle::cleanup);
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (!(player instanceof WarlordsPlayer)) {
            return;
        }
        for (int i = 1; i < player.getAbilities().size(); i++) {
            player.getAbilities().remove(i);
            i--;
        }
        player.updateInventory(false);
    }

    public Map<Team, TowerDefenseCastle> getCastles() {
        return castles;
    }

    public static class TowerDefenseMobData extends MobData {

        private final Team attackingTeam; // side that mob is attacking
        private final Location spawnLocation; // original spawn location with no offset
        private final int pathIndex; // index of whatever path its using
        private int lastWaypointIndex = 0;
        private boolean attackingCastle = false;

        public TowerDefenseMobData(int spawnTick, Team attackingTeam, Location spawnLocation, int pathIndex) {
            super(spawnTick);
            this.attackingTeam = attackingTeam;
            this.spawnLocation = spawnLocation;
            this.pathIndex = pathIndex;
        }

        @Override
        public String toString() {
            return "TowerDefenseMobData{" +
                    "spawnLocation=" + spawnLocation +
                    ", pathIndex=" + pathIndex +
                    ", lastWaypointIndex=" + lastWaypointIndex +
                    '}';
        }

        public Team getAttackingTeam() {
            return attackingTeam;
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

        public boolean isAttackingCastle() {
            return attackingCastle;
        }

        public void setAttackingCastle(boolean attackingCastle) {
            this.attackingCastle = attackingCastle;
        }
    }

}
