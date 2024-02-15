package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PveRewards;
import com.ebicep.warlords.game.option.towerdefense.events.TowerDefenseCastleDestroyEvent;
import com.ebicep.warlords.game.option.towerdefense.events.TowerDefenseMobCompletePathEvent;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TowerDefenseOption implements PveOption, Listener {

    private static final ItemStack MARKET_ITEM = new ItemBuilder(Material.BRICKS)
            .name(Component.text("Market", NamedTextColor.GREEN))
            .setOnUseID("MARKET_ITEM")
            .get();
    private final ConcurrentHashMap<AbstractMob, TowerDefenseMobData> mobs = new ConcurrentHashMap<>();
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private final Map<WarlordsEntity, TowerDefensePlayerInfo> playerInfo = new HashMap<>();
    private final Map<Team, TowerDefenseCastle> castles = new LinkedHashMap<>();
    private Game game;
    private TowerBuildOption towerBuildOption;
    private TowerDefenseSpawner towerDefenseSpawner;

    public TowerDefenseOption addCastle(Team team, Location location, float maxHealth) {
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
        game.registerEvents(getBaseListener());
        game.registerEvents(this);
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(4, "castle") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                return castles.entrySet()
                              .stream()
                              .map(entry -> {
                                  Team team = entry.getKey();
                                  TowerDefenseCastle castle = entry.getValue();
                                  return Component.text(team.name + " Castle Health: ", team.getTeamColor()).append(castle.getHealthComponent());
                              })
                              .collect(Collectors.toList());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, new SimpleScoreboardHandler(16, "exp") {
            @Nonnull
            @Override
            public List<Component> computeLines(@Nullable WarlordsPlayer player) {
                if (player != null) {
                    TowerDefensePlayerInfo info = getPlayerInfo(player);
                    return Collections.singletonList(Component.text("Exp: ").append(Component.text(NumberFormat.addCommas(info.getCurrentExp()), NamedTextColor.DARK_AQUA)));
                }
                return Collections.singletonList(Component.empty());
            }

//            @Override
//            public boolean emptyLinesBetween() {
//                return false;
//            }
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (!TowerDefenseUtils.validInteractGame(game, warlordsEntity)) {
            return;
        }
        if (!TowerDefenseUtils.validInteract(event, "MARKET_ITEM")) {
            return;
        }
        TowerDefenseMenu.openMarket(player, warlordsEntity, getPlayerInfo(warlordsEntity));
    }

    public TowerDefensePlayerInfo getPlayerInfo(WarlordsEntity player) {
        playerInfo.putIfAbsent(player, new TowerDefensePlayerInfo());
        return playerInfo.get(player);
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
        spawnNewMob(mob, (WarlordsEntity) null);
    }

    public void spawnNewMob(AbstractMob mob, @Nullable WarlordsEntity spawner) {
        if (!(mob instanceof TowerDefenseMob towerDefenseMob)) {
            return;
        }
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
        towerDefenseSpawner.spawnNewMob(towerDefenseMob, spawner);
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

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        // override to remove talisman
        player.getInventory().setItem(4, MARKET_ITEM);
    }

    @EventHandler
    public void onMobCompletePath(TowerDefenseMobCompletePathEvent event) {
        TowerDefenseMobData mobData = mobs.get(event.getMob());
        if (mobData == null) {
            return;
        }
        mobData.setAttackingCastle(true);
    }

    @EventHandler
    public void onDeath(WarlordsDeathEvent event) {
        WarlordsEntity we = event.getWarlordsEntity();
        WarlordsEntity killer = event.getKiller();

        if (we instanceof WarlordsNPC) {
            AbstractMob mobToRemove = ((WarlordsNPC) we).getMob();
            if (mobs.containsKey(mobToRemove)) {
                mobToRemove.onDeath(killer, we.getDeathLocation(), TowerDefenseOption.this);
                new GameRunnable(game) {
                    @Override
                    public void run() {
                        mobs.remove(mobToRemove);
                        game.getPlayers().remove(we.getUuid());
                        Warlords.removePlayer(we.getUuid());
                        //game.removePlayer(we.getUuid());
                    }
                }.runTaskLater(1);

                if (killer instanceof WarlordsPlayer) {
                    killer.getMinuteStats().addMobKill(mobToRemove.getName());
                    we.getHitBy().forEach((assisted, value) -> assisted.getMinuteStats().addMobAssist(mobToRemove.getName()));
                }

            }
            MobCommand.SPAWNED_MOBS.remove(mobToRemove);
        } else if (we instanceof WarlordsPlayer && killer instanceof WarlordsNPC) {
            if (mobs.containsKey(((WarlordsNPC) killer).getMob())) {
                we.getMinuteStats().addMobDeath(((WarlordsNPC) killer).getMob().getName());
            }
        }
    }

    public Map<WarlordsEntity, TowerDefensePlayerInfo> getPlayerInfo() {
        return playerInfo;
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
