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
import com.ebicep.warlords.game.option.towerdefense.path.TowerDefenseDirectAcyclicGraph;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerDefenseTowerMob;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.WarlordsTower;
import com.ebicep.warlords.pve.commands.MobCommand;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.dag.DAGUtils;
import com.ebicep.warlords.util.java.dag.Node;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

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
    private final Material mobPathMaterial = Material.TERRACOTTA;
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
    }

    @Override
    public void start(@Nonnull Game game) {
        castles.values().forEach(TowerDefenseCastle::displayInit);
        towerDefenseSpawner.getPaths().forEach((location, towerDefensePaths) -> {
            for (TowerDefenseDirectAcyclicGraph towerDefensePath : towerDefensePaths) {
                towerDefensePath.calculateEdgeData();
                towerDefensePath.calculateNodeDistances();
                Node<Location> root = towerDefensePath.getRoot();
                HashSet<Node<Location>> nodes = new HashSet<>();
                DAGUtils.depthFirstSearch(towerDefensePath, root, nodes);
                nodes.forEach(locationNode -> renderNode(towerDefensePath, locationNode));
                for (Node<Location> locationNode : root.getChildren()) {
                    List<TowerDefenseDirectAcyclicGraph.TowerDefenseEdge> towerDefenseEdges = towerDefensePath.getEdges().get(locationNode);
                    if (towerDefenseEdges != null) {
                        towerDefenseEdges.forEach(edge -> renderEdge(locationNode, edge));
                    }
                }
            }
        });
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
                    towerDefenseSpawner.renderEdges();
                }
                if (ticksElapsed.get() % 20 == 0) {
                    towerDefenseSpawner.recalculateMobPositions();
                }
            }
        }.runTaskTimer(0, 0);
    }

    private void renderNode(TowerDefenseDirectAcyclicGraph towerDefensePath, Node<Location> node) {
        Location location = node.getValue();
        location.getWorld().spawn(location.clone().add(0, 2, 0), TextDisplay.class, display -> {
            display.text(Component.text("NODE (" + NumberFormat.formatOptionalTenths(towerDefensePath.getNodeDistanceToEnd().get(node)) + ")", NamedTextColor.DARK_AQUA));
            display.setBillboard(Display.Billboard.CENTER);
            display.setTransformation(new Transformation(
                    new Vector3f(),
                    new AxisAngle4f(),
                    new Vector3f(2),
                    new AxisAngle4f()
            ));
        });
    }

    private void renderEdge(Node<Location> from, TowerDefenseDirectAcyclicGraph.TowerDefenseEdge edge) {
        Location fromLocation = from.getValue();
        Location toLocation = edge.getTo().getValue();
        toLocation.getWorld().spawn(new LocationBuilder(fromLocation).faceTowards(toLocation).forward(edge.getDistance() / 2).addY(2), TextDisplay.class, display -> {
            display.text(Component.text(edge.getPathDirection() + " (" + NumberFormat.formatOptionalTenths(edge.getDistance()) + ")", NamedTextColor.AQUA));
            display.setBillboard(Display.Billboard.CENTER);
            display.setTransformation(new Transformation(
                    new Vector3f(),
                    new AxisAngle4f(),
                    new Vector3f(2),
                    new AxisAngle4f()
            ));
        });
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
            TowerDefenseMobData data = entry.getValue();
            if (!(data instanceof TowerDefenseAttackingMobData mobData)) {
                continue;
            }
            mob.whileAlive(ticksElapsed.get() - mobData.getSpawnTick(), this);
            mob.activateAbilities();
            if (mobData.isAttackingCastle()) {
                Team attackingTeam = mobData.getAttackingTeam();
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
                    TowerDefenseMobData data = entry.getValue();
                    boolean remove = data instanceof TowerDefenseAttackingMobData mobData && mobData.attackingTeam == destroyedCastleTeam ||
                            data instanceof TowerDefenseDefendingMobData && mob.getWarlordsNPC().getTeam() == destroyedCastleTeam;
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
        if (mob instanceof TowerDefenseMob towerDefenseMob) {
            Location spawnLocation = mob.getSpawnLocation();
            Team attackingTeam = towerDefenseSpawner.getTeamSpawnLocations().get(spawnLocation);
            if (spawner instanceof WarlordsNPC warlordsNPC && mobs.get(warlordsNPC.getMob()) instanceof TowerDefenseAttackingMobData mobData) {
                attackingTeam = mobData.getAttackingTeam();
            }
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
        } else if (mob instanceof TowerDefenseTowerMob towerDefenseTowerMob && spawner instanceof WarlordsTower warlordsTower) {
            towerDefenseSpawner.spawnNewMob(towerDefenseTowerMob, warlordsTower);
        }
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

    public Material getMobPathMaterial() {
        return mobPathMaterial;
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
        TowerDefensePlayerInfo info = playerInfo.get(player);
        if (info.getWaveTask() == null) {
            info.setWaveTask(new GameRunnable(game) {
                int ticksElapsed = 0;

                @Override
                public void run() {
                    info.getPlayerWave().tick(TowerDefenseOption.this);
                    if (ticksElapsed++ % 5 == 0 &&
                            player.getEntity() instanceof Player p &&
                            PlainTextComponentSerializer.plainText().serialize(p.getOpenInventory().title()).equals(TowerDefenseMenu.SUMMON_MENU_TITLE)
                    ) {
                        TowerDefenseMenu.openSummonTroopsMenu(p, player, towerDefenseSpawner, info);
                    }
                }
            }.runTaskTimer(0, 0));
        }
        return info;
    }

    @EventHandler
    public void onMobCompletePath(TowerDefenseMobCompletePathEvent event) {
        TowerDefenseMobData mobData = mobs.get(event.getMob());
        if (mobData instanceof TowerDefenseAttackingMobData mobMobData) {
            mobMobData.setAttackingCastle(true);
        }
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

    public TowerBuildOption getTowerBuildOption() {
        return towerBuildOption;
    }

    public TowerDefenseSpawner getTowerDefenseSpawner() {
        return towerDefenseSpawner;
    }

    public static class TowerDefenseMobData extends MobData {

        public TowerDefenseMobData(int spawnTick) {
            super(spawnTick);
        }
    }

    public static class TowerDefenseDefendingMobData extends TowerDefenseMobData {

        public TowerDefenseDefendingMobData(int spawnTick) {
            super(spawnTick);
        }
    }

    public static class TowerDefenseAttackingMobData extends TowerDefenseMobData {

        private final Team attackingTeam; // side that mob is attacking
        private final Location spawnLocation; // original spawn location with no offset
        private final int pathIndex; // index of whatever path its using
        private int currentNode;
        private int targetNode;
        private int edgeIndex;
        private int position; // position in game, first = 1
        private boolean attackingCastle = false;

        public TowerDefenseAttackingMobData(
                int spawnTick,
                Team attackingTeam,
                Location spawnLocation,
                int pathIndex,
                int currentNode,
                int targetNode,
                int edgeIndex,
                int position
        ) {
            super(spawnTick);
            this.attackingTeam = attackingTeam;
            this.spawnLocation = spawnLocation;
            this.pathIndex = pathIndex;
            this.currentNode = currentNode;
            this.targetNode = targetNode;
            this.edgeIndex = edgeIndex;
            this.position = position;
        }

        @Override
        public String toString() {
            return "TowerDefenseMobData{" +
                    "spawnLocation=" + spawnLocation +
                    ", pathIndex=" + pathIndex +
                    ", lastWaypointIndex=" + targetNode +
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

        public int getTargetNode() {
            return targetNode;
        }

        public void setTargetNode(int targetNode) {
            this.targetNode = targetNode;
        }

        public int getCurrentNode() {
            return currentNode;
        }

        public void setCurrentNode(int currentNode) {
            this.currentNode = currentNode;
        }

        public int getEdgeIndex() {
            return edgeIndex;
        }

        public void setEdgeIndex(int edgeIndex) {
            this.edgeIndex = edgeIndex;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }


        public boolean isAttackingCastle() {
            return attackingCastle;
        }

        public void setAttackingCastle(boolean attackingCastle) {
            this.attackingCastle = attackingCastle;
        }
    }

}
