package com.ebicep.warlords.game.option.towerdefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.towerdefense.events.TowerDefenseMobCompletePathEvent;
import com.ebicep.warlords.game.option.towerdefense.mobs.NPCTowerDefensePathfindGoal;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMob;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMobInfo;
import com.ebicep.warlords.game.option.towerdefense.path.PathDirection;
import com.ebicep.warlords.game.option.towerdefense.path.TowerDefenseDirectAcyclicGraph;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerDefenseTowerMob;
import com.ebicep.warlords.game.option.towerdefense.waves.TowerDefenseWave;
import com.ebicep.warlords.player.ingame.*;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.dag.Node;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.event.NavigationBeginEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class TowerDefenseSpawner implements Option, Listener {

    private static final ItemStack SUMMON_TROOPS_ITEM = new ItemBuilder(Material.SPAWNER)
            .name(Component.text("Summon Troops", NamedTextColor.GREEN))
            .setOnUseID("SUMMON_TROOPS_ITEM")
            .get();
    public static int MAX_PLAYER_SPAWN_AMOUNT = 5; // max numbers of mobs players can spawn at a time
    private final Map<Location, List<TowerDefenseDirectAcyclicGraph>> paths = new HashMap<>(); // spawn location -> list of paths from that location
    private final List<TowerDefenseWave> waves = new ArrayList<>();
    private final List<BukkitTask> activeWaves = new ArrayList<>();
    private final List<BukkitTask> activePlayerWaves = new ArrayList<>();
    private TowerDefenseOption towerDefenseOption;
    private ConcurrentHashMap<AbstractMob, TowerDefenseOption.TowerDefenseMobData> mobs;
    private int currentWave = 0; // index
    private Game game;
    private Map<Location, Team> teamSpawnLocations = new HashMap<>();

    public TowerDefenseSpawner init(TowerDefenseOption towerDefenseOption) {
        this.towerDefenseOption = towerDefenseOption;
        this.mobs = towerDefenseOption.getMobsMap();
        return this;
    }

    public TowerDefenseSpawner addPath(Location spawn, TowerDefenseDirectAcyclicGraph path) {
        paths.computeIfAbsent(spawn, k -> new ArrayList<>()).add(path);
        return this;
    }


    public void renderEdges() {
        paths.forEach((spawnLocation, towerDefensePaths) -> {
            for (TowerDefenseDirectAcyclicGraph towerDefensePath : towerDefensePaths) {
                Map<Node<Location>, List<TowerDefenseDirectAcyclicGraph.TowerDefenseEdge>> edges = towerDefensePath.getEdges();
                edges.forEach((locationNode, towerDefenseEdges) -> {
                    Location from = locationNode.getValue().clone().add(0, 1, 0);
                    for (TowerDefenseDirectAcyclicGraph.TowerDefenseEdge edge : towerDefenseEdges) {
                        Location to = edge.getTo().getValue().clone().add(0, 1, 0);
                        EffectUtils.playParticleLinkAnimation(from, to, 255, 0, 0, 1, 1);
                    }
                });
            }
        });
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        for (Team team : TeamMarker.getTeams(game)) {
            for (Location spawnLocation : TowerDefenseUtils.getTeamSpawnLocations(game, team)) {
                teamSpawnLocations.put(spawnLocation, team);
            }
        }
        this.game.registerEvents(this);
    }

    @Override
    public void start(@Nonnull Game game) {
        startCurrentWave();
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {

    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        player.getInventory().setItem(8, SUMMON_TROOPS_ITEM);
    }

    @EventHandler
    public void onMobSpawn(WarlordsMobSpawnEvent event) {
        AbstractMob mob = event.getMob();
        NPC npc = mob.getNpc();
//        use this code if collisions are on
//        if (npc.getEntity() instanceof LivingEntity livingEntity) {
//            livingEntity.getCollidableExemptions().addAll(game.warlordsPlayers().map(WarlordsEntity::getUuid).collect(Collectors.toSet()));
//        }
        TowerDefenseOption.TowerDefenseMobData mobData = mobs.get(mob);
        if (mobData instanceof TowerDefenseOption.TowerDefenseAttackingMobData attackingMobData) {
            int lastNodeIdentifier = attackingMobData.getTargetNode();
            TowerDefenseDirectAcyclicGraph path = getPathFromData(attackingMobData);
            Node<Location> node = path.getNodeIndex().get(lastNodeIdentifier);
            List<TowerDefenseDirectAcyclicGraph.TowerDefenseEdge> outgoingEdges = path.getEdges(node);
            assignNextTargetNode(attackingMobData, lastNodeIdentifier, outgoingEdges);

            npc.getDefaultGoalController().addGoal(new NPCTowerDefensePathfindGoal(npc, this, mob, attackingMobData), 3);
            if (towerDefenseOption.isDebug()) {
                mob.getWarlordsNPC()
                   .getMobHologram()
                   .getCustomHologramLines()
                   .add(new MobHologram.CustomHologramLine(() -> Component.text(attackingMobData.getPosition() + " - " +
                                   path.getNodeDistanceToEnd().get(path.getNodeIndex().get(attackingMobData.getTargetNode())),
                           NamedTextColor.GREEN
                   )));
            }
        }
    }

    @EventHandler
    public void onMobStartNavigating(NavigationBeginEvent event) {
        NPC npc = event.getNPC();
        // handle setting targetedBy/targeting
        EntityTarget entityTarget = npc.getNavigator().getEntityTarget();
        if (entityTarget == null) {
            return;
        }
        if (!(npc.data().get(WarlordsEntity.WARLORDS_ENTITY_METADATA) instanceof WarlordsNPC warlordsNPC)) {
            return;
        }
        if (!(Warlords.getPlayer(entityTarget.getTarget()) instanceof WarlordsNPC target)) {
            return;
        }
        TowerDefenseOption.TowerDefenseMobData mobData = mobs.get(warlordsNPC.getMob());
        TowerDefenseOption.TowerDefenseMobData targetedMobData = mobs.get(target.getMob());
        if (mobData == null || targetedMobData == null) {
            return;
        }
        mobData.setTargeting(target);
        targetedMobData.getTargetedBy().add(warlordsNPC);
    }

    public TowerDefenseDirectAcyclicGraph getPathFromData(TowerDefenseOption.TowerDefenseAttackingMobData mobData) {
        return paths.get(mobData.getSpawnLocation()).get(mobData.getPathIndex());
    }

    public static void assignNextTargetNode(
            TowerDefenseOption.TowerDefenseAttackingMobData mobData,
            int lastNodeIdentifier,
            List<TowerDefenseDirectAcyclicGraph.TowerDefenseEdge> outgoingEdges
    ) {
        int randomEdgeIndex = ThreadLocalRandom.current().nextInt(outgoingEdges.size());
        TowerDefenseDirectAcyclicGraph.TowerDefenseEdge edge = outgoingEdges.get(randomEdgeIndex);
        mobData.setCurrentNode(lastNodeIdentifier);
        mobData.setTargetNode(edge.getTo().hashCode());
        mobData.setEdgeIndex(randomEdgeIndex);
    }

    public void recalculateMobPositions() {
        // get path
        // get total path distance based on pathIndex
        List<TowerDefenseOption.TowerDefenseAttackingMobData> sortedPositions = mobs
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() instanceof TowerDefenseOption.TowerDefenseAttackingMobData)
                .sorted((entry1, entry2) -> {
                    AbstractMob mob1 = entry1.getKey();
                    AbstractMob mob2 = entry2.getKey();
                    TowerDefenseOption.TowerDefenseAttackingMobData data1 = (TowerDefenseOption.TowerDefenseAttackingMobData) entry1.getValue();
                    TowerDefenseOption.TowerDefenseAttackingMobData data2 = (TowerDefenseOption.TowerDefenseAttackingMobData) entry2.getValue();
                    TowerDefenseDirectAcyclicGraph pathFromData1 = getPathFromData(data1);
                    TowerDefenseDirectAcyclicGraph pathFromData2 = getPathFromData(data2);
                    // first check if either are attacking castle - auto first
                    if (data1.isAttackingCastle() || data2.isAttackingCastle()) {
                        return Boolean.compare(data2.isAttackingCastle(), data1.isAttackingCastle());
                    }
                    // then check equal path and waypoint index - if so, sort by distance to next waypoint
                    Map<Integer, Node<Location>> path1NodeIndex = pathFromData1.getNodeIndex();
                    if (pathFromData1.equals(pathFromData2) &&
                            data1.getCurrentNode() == data2.getCurrentNode() &&
                            data1.getTargetNode() == data2.getTargetNode() &&
                            data1.getEdgeIndex() == data2.getEdgeIndex()
                    ) {
                        Node<Location> currentNode = path1NodeIndex.get(data1.getCurrentNode());
                        List<TowerDefenseDirectAcyclicGraph.TowerDefenseEdge> outgoingEdges = pathFromData1.getEdges(currentNode);
                        TowerDefenseDirectAcyclicGraph.TowerDefenseEdge edge = outgoingEdges.get(data1.getEdgeIndex());
                        Node<Location> targetNode = path1NodeIndex.get(data1.getTargetNode());
                        return edge.getPathDirection().compare(
                                mob1.getNpc().getStoredLocation(),
                                mob2.getNpc().getStoredLocation(),
                                targetNode.getValue()
                        );
                    }
                    // finally sort by relative distance on current path
                    Map<Integer, Node<Location>> path2NodeIndex = pathFromData2.getNodeIndex();
                    return Double.compare(
                            pathFromData1.getNodeDistanceToEnd().get(path1NodeIndex.get(data1.getTargetNode())),
                            pathFromData2.getNodeDistanceToEnd().get(path2NodeIndex.get(data2.getTargetNode()))
                    );
                })
                .map(entry -> (TowerDefenseOption.TowerDefenseAttackingMobData) entry.getValue())
                .toList();
        for (int i = 0; i < sortedPositions.size(); i++) {
            TowerDefenseOption.TowerDefenseAttackingMobData data = sortedPositions.get(i);
            data.setPosition(i + 1);
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
        if (mob instanceof TowerDefenseMob towerDefenseMob && mobData instanceof TowerDefenseOption.TowerDefenseAttackingMobData attackingMobData) {
            int lastNodeIdentifier = attackingMobData.getTargetNode();
            TowerDefenseDirectAcyclicGraph path = getPathFromData(attackingMobData);
            Node<Location> node = path.getNodeIndex().get(lastNodeIdentifier);
            List<TowerDefenseDirectAcyclicGraph.TowerDefenseEdge> outgoingEdges = path.getEdges(node);
            if (outgoingEdges == null || outgoingEdges.isEmpty()) {
                Bukkit.getPluginManager().callEvent(new TowerDefenseMobCompletePathEvent(game, towerDefenseMob));
                return;
            }
            // random edge
            assignNextTargetNode(attackingMobData, lastNodeIdentifier, outgoingEdges);
            pathFindToNextWaypoint(mob, attackingMobData);
        }
    }

    public void pathFindToNextWaypoint(AbstractMob mob, TowerDefenseOption.TowerDefenseAttackingMobData mobData) {
        if (mobData.isAttackingCastle()) {
            return;
        }
        NPC npc = mob.getNpc();
        TowerDefenseDirectAcyclicGraph path = getPathFromData(mobData);
        Node<Location> currentNode = path.getNodeIndex().get(mobData.getCurrentNode());
        Node<Location> targetNode = path.getNodeIndex().get(mobData.getTargetNode());

        List<TowerDefenseDirectAcyclicGraph.TowerDefenseEdge> outgoingEdges = path.getEdges(currentNode);
        TowerDefenseDirectAcyclicGraph.TowerDefenseEdge randomEdge = outgoingEdges.get(mobData.getEdgeIndex());
        PathDirection randomEdgePathDirection = randomEdge.getPathDirection();

        Location nextTargetLocation = targetNode.getValue();
        Location lineTarget = randomEdgePathDirection.getForwardLocation(npc.getStoredLocation(), nextTargetLocation);
        npc.getNavigator().setStraightLineTarget(lineTarget);

        if (!towerDefenseOption.isDebug()) {
            return;
        }
        TextDisplay d = lineTarget.getWorld().spawn(lineTarget.clone().add(0, 3, 0), TextDisplay.class, display -> {
            display.text(Component.text("Next Target (" + randomEdgePathDirection.name() + ")", NamedTextColor.LIGHT_PURPLE));
            display.setBillboard(Display.Billboard.CENTER);
        });
        TextDisplay d2 = lineTarget.getWorld().spawn(nextTargetLocation.clone().add(0, 4, 0), TextDisplay.class, display -> {
            display.text(Component.text("Next Waypoint", NamedTextColor.DARK_PURPLE));
            display.setBillboard(Display.Billboard.CENTER);
        });
        TextDisplay d3 = lineTarget.getWorld().spawn(currentNode.getValue().clone().add(0, 6, 0), TextDisplay.class, display -> {
            display.text(Component.text("CURRENT NODE", NamedTextColor.RED));
            display.setBillboard(Display.Billboard.CENTER);
        });
        TextDisplay d4 = lineTarget.getWorld().spawn(targetNode.getValue().clone().add(0, 6, 0), TextDisplay.class, display -> {
            display.text(Component.text("TARGET NODE", NamedTextColor.DARK_RED));
            display.setBillboard(Display.Billboard.CENTER);
        });
        new BukkitRunnable() {
            @Override
            public void run() {
                d.remove();
                d2.remove();
                d3.remove();
                d4.remove();
            }
        }.runTaskLater(Warlords.getInstance(), 60);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (!TowerDefenseUtils.validInteractGame(game, warlordsEntity)) {
            return;
        }
        if (!TowerDefenseUtils.validInteract(event, "SUMMON_TROOPS_ITEM")) {
            return;
        }
        TowerDefensePlayerInfo playerInfo = towerDefenseOption.getPlayerInfo(warlordsEntity);
        playerInfo.setCurrentMobMenuPage(1);
        TowerDefenseMenu.openSummonTroopsMenu(player, warlordsEntity, this, playerInfo);
    }

    private Location getForwardLocation(NPC npc, double xyAmount, double yAmount) {
        return new LocationBuilder(npc.getStoredLocation()).forward(xyAmount).addY(yAmount);
    }

    /**
     * @param mob     mob to spawn
     * @param spawner player who spawned the mob - the mob will spawn on all other teams paths
     */
    public void spawnNewMob(TowerDefenseMob mob, @Nullable WarlordsEntity spawner) {
        Location randomSpawn = mob.getSpawnLocation();
        // copy data if spawner is a mob
        Location spawnLocation;
        Team attackingTeam;
        int currentNodeID;
        int targetNodeID;
        int edgeIndex;
        int randomPathIndex;
        if (spawner instanceof WarlordsNPC warlordsNPC) {
            AbstractMob npcMob = warlordsNPC.getMob();
            TowerDefenseOption.TowerDefenseMobData spawnerMobData = mobs.get(npcMob);
            if (spawnerMobData instanceof TowerDefenseOption.TowerDefenseAttackingMobData spawnerAttackingMobData) {
                TowerDefenseDirectAcyclicGraph path = getPathFromData(spawnerAttackingMobData);
                Map<Integer, Node<Location>> nodeIndex = path.getNodeIndex();
                Node<Location> currentNode = nodeIndex.get(spawnerAttackingMobData.getCurrentNode());
                Node<Location> targetNode = nodeIndex.get(spawnerAttackingMobData.getTargetNode());
                TowerDefenseDirectAcyclicGraph.TowerDefenseEdge pathLocation = path.getEdges(currentNode).get(spawnerAttackingMobData.getEdgeIndex());

                spawnLocation = pathLocation.getPathDirection().getRandomSpawnLocation(targetNode.getValue(), randomSpawn);
                randomSpawn = spawnerAttackingMobData.getSpawnLocation();
                attackingTeam = spawnerAttackingMobData.getAttackingTeam();
                currentNodeID = spawnerAttackingMobData.getCurrentNode();
                targetNodeID = spawnerAttackingMobData.getTargetNode();
                edgeIndex = spawnerAttackingMobData.getEdgeIndex();
                randomPathIndex = spawnerAttackingMobData.getPathIndex();
            } else {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage("Spawner mob data is null or not attacking mob data: " + spawnerMobData);
                return;
            }
        } else {
            spawnLocation = randomSpawn.clone().add(
                    ThreadLocalRandom.current().nextDouble(4) - 2,
                    0,
                    ThreadLocalRandom.current().nextDouble(4) - 2
            );
            attackingTeam = teamSpawnLocations.get(randomSpawn);
            edgeIndex = 0;
            // get random path
            List<TowerDefenseDirectAcyclicGraph> pathList = paths.get(randomSpawn);
            if (pathList == null) {
                ChatUtils.MessageType.TOWER_DEFENSE.sendErrorMessage("No path with given spawn: " + randomSpawn + "\n" + paths);
                return;
            }
            randomPathIndex = ThreadLocalRandom.current().nextInt(pathList.size());
            currentNodeID = pathList.get(randomPathIndex).getRoot().hashCode();
            targetNodeID = pathList.get(randomPathIndex).getRoot().hashCode();
        }
        mob.setSpawnLocation(spawnLocation);
        mob.setSpawner(spawner);
        Team team = spawner == null ? Team.GAME : spawner.getTeam();

        game.addNPC(mob.toNPC(game, team, this::modifyStats));
        mobs.put(mob, new TowerDefenseOption.TowerDefenseAttackingMobData(towerDefenseOption.getTicksElapsed(),
                attackingTeam,
                randomSpawn,
                randomPathIndex,
                currentNodeID,
                targetNodeID,
                edgeIndex,
                mobs.size() + 1
        ));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    protected void modifyStats(WarlordsNPC warlordsNPC) {
        warlordsNPC.getMob().onSpawn(towerDefenseOption);
    }

    public void spawnNewMob(TowerDefenseTowerMob mob, WarlordsTower spawner) {
        mob.setSpawner(spawner);
        game.addNPC(mob.toNPC(game, spawner.getTeam(), this::modifyStats));
        mobs.put(mob, new TowerDefenseOption.TowerDefenseDefendingMobData(towerDefenseOption.getTicksElapsed()));
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, mob));
    }

    public TowerDefenseSpawner add(TowerDefenseWave wave) {
        waves.add(wave);
        return this;
    }

    public TowerDefenseSpawner applyToAllWaves(Consumer<List<TowerDefenseWave>> waveConsumer) {
        waveConsumer.accept(waves);
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

    public void startPlayerWave(TowerDefenseWave wave) {
        activePlayerWaves.add(new GameRunnable(game) {
            @Override
            public void run() {
                wave.tick(towerDefenseOption);
            }
        }.runTaskTimer(0, 0));
    }

    public TowerDefenseOption getTowerDefenseOption() {
        return towerDefenseOption;
    }

    public ConcurrentHashMap<AbstractMob, TowerDefenseOption.TowerDefenseMobData> getMobs() {
        return mobs;
    }

    public Map<Location, Team> getTeamSpawnLocations() {
        return teamSpawnLocations;
    }

    public Map<Location, List<TowerDefenseDirectAcyclicGraph>> getPaths() {
        return paths;
    }

    public TowerDefenseMobInfo[] getCurrentUnlockedMobs() {
        return Arrays.stream(TowerDefenseMobInfo.VALUES)
                     .filter(towerDefenseMobInfo -> towerDefenseMobInfo.getWaveUnlocked() <= currentWave + 1)
                     .toArray(TowerDefenseMobInfo[]::new);
    }
}
