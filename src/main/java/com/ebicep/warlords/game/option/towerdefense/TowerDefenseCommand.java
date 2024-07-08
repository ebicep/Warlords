package com.ebicep.warlords.game.option.towerdefense;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.towerdefense.mobs.TowerDefenseMobInfo;
import com.ebicep.warlords.game.option.towerdefense.path.TowerDefenseDirectAcyclicGraph;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerRegistry;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.java.dag.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("towerdefense|td")
@CommandPermission("group.administrator")
public class TowerDefenseCommand extends BaseCommand {

    @Subcommand("build")
    public void build(@Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player, TowerRegistry tower) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        Location location = player.getLocation();
        location.setYaw(0);
        tower.create.apply(game, player.getUniqueId(), location).build();
    }

    @Subcommand("debug")
    public void debug(@Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption towerDefenseOption) {
                towerDefenseOption.toggleDebug();
                ChatChannels.sendDebugMessage(player, Component.text("Debug: " + towerDefenseOption.isDebug(), NamedTextColor.GREEN));
            }
        }
    }

    @Subcommand("togglemovement")
    public void toggleMovement(@Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption towerDefenseOption) {
                towerDefenseOption.toggleMovement();
                ChatChannels.sendDebugMessage(player, Component.text("Movement: " + towerDefenseOption.isMovement(), NamedTextColor.GREEN));
            }
        }
    }

    @Subcommand("removeall")
    public void removeAll(@Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerBuildOption towerBuildOption) {
                Map<AbstractTower, Integer> builtTowers = towerBuildOption.getBuiltTowers();
                for (AbstractTower builtTower : builtTowers.keySet()) {
                    builtTower.remove();
                }
                builtTowers.clear();
            }
        }
        ChatChannels.sendDebugMessage(player, Component.text("Removed all towers", NamedTextColor.GREEN));
    }

    @Subcommand("reloadtowers")
    public void reloadTowers(CommandIssuer issuer) {
        EnumSet<TowerRegistry> updated = TowerRegistry.updateCaches();
        List<TowerRegistry> notUpdated = new ArrayList<>();
        for (TowerRegistry value : TowerRegistry.VALUES) {
            if (!updated.contains(value)) {
                notUpdated.add(value);
            }
        }
        ChatChannels.sendDebugMessage(issuer, Component.text("Updated: ", NamedTextColor.GREEN)
                                                       .append(updated.stream()
                                                                      .sorted(Comparator.comparing(Enum::ordinal))
                                                                      .map(tower -> Component.text(tower.name(), NamedTextColor.YELLOW))
                                                                      .collect(Component.toComponent(Component.text(", ", NamedTextColor.GRAY))))
        );
        if (!notUpdated.isEmpty()) {
            ChatChannels.sendDebugMessage(issuer, Component.text("Not Updated: ", NamedTextColor.RED)
                                                           .append(notUpdated.stream()
                                                                             .map(tower -> Component.text(tower.name(), NamedTextColor.YELLOW))
                                                                             .collect(Component.toComponent(Component.text(", ", NamedTextColor.GRAY))))
            );
        }
    }

    @Subcommand("spawnmob")
    public void spawnMob(
            @Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player,
            TowerDefenseMobInfo mob,
            @Default("1") @Conditions("limits:min=0,max=25") Integer amount,
            @Optional Team team
    ) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption towerDefenseOption) {
                for (Team t : TeamMarker.getTeams(game)) {
                    if (t != team) {
                        continue;
                    }
                    for (int i = 0; i < amount; i++) {
                        towerDefenseOption.spawnNewMob(mob.getMob().createMob(towerDefenseOption.getRandomSpawnLocation(t)), (WarlordsEntity) null);
                    }
                    ChatChannels.sendDebugMessage(player, Component.text("Spawned " + amount + " Mobs", NamedTextColor.GREEN));
                }
                break;
            }
        }
    }

    @Subcommand("spawnmobhere")
    public void spawnMobHere(
            @Conditions("requireGame:gamemode=TOWER_DEFENSE") Player player,
            TowerDefenseMobInfo mob,
            @Default("1") @Conditions("limits:min=0,max=25") Integer amount,
            @Optional Team team
    ) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        for (Option option : game.getOptions()) {
            if (option instanceof TowerDefenseOption towerDefenseOption) {
                for (Team t : TeamMarker.getTeams(game)) {
                    if (t != team) {
                        continue;
                    }
                    Location playerLocation = player.getLocation();
                    // find closest node
                    Map<Location, List<Pair<TowerDefenseDirectAcyclicGraph, Node<Location>>>> suitablePathNodes = new HashMap<>();
                    TowerDefenseSpawner towerDefenseSpawner = towerDefenseOption.getTowerDefenseSpawner();
                    Map<Location, List<TowerDefenseDirectAcyclicGraph>> paths = towerDefenseSpawner.getPaths();
                    paths.forEach((location, towerDefenseDirectAcyclicGraphs) -> {
                        boolean found = false;
                        for (TowerDefenseDirectAcyclicGraph towerDefenseDirectAcyclicGraph : towerDefenseDirectAcyclicGraphs) {
                            for (Node<Location> node : towerDefenseDirectAcyclicGraph.getNodeIndex().values()) {
                                if (node.getValue().distanceSquared(playerLocation) < 4 * 4) {
                                    suitablePathNodes.computeIfAbsent(location, k -> new ArrayList<>()).add(new Pair<>(towerDefenseDirectAcyclicGraph, node));
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                    });
                    if (suitablePathNodes.isEmpty()) {
                        ChatChannels.sendDebugMessage(player, Component.text("No suitable path nodes found, stand near a node", NamedTextColor.RED));
                        return;
                    }
                    for (int i = 0; i < amount; i++) {
                        AbstractMob abstractMob = mob.getMob().createMob(towerDefenseOption.getRandomSpawnLocation(t));
                        towerDefenseOption.spawnNewMob(abstractMob, (WarlordsEntity) null);

                        // get random node to teleport to then set pathfinder
                        List<Location> locations = new ArrayList<>(suitablePathNodes.keySet());
                        // spawn location of path
                        Location spawnLocation = locations.get(ThreadLocalRandom.current().nextInt(locations.size()));
                        // random node
                        List<Pair<TowerDefenseDirectAcyclicGraph, Node<Location>>> suitablePaths = suitablePathNodes.get(spawnLocation);
                        Pair<TowerDefenseDirectAcyclicGraph, Node<Location>> randomPath = suitablePaths.get(ThreadLocalRandom.current().nextInt(suitablePaths.size()));
                        Node<Location> newCurrentNode = randomPath.getB();
                        TowerDefenseOption.TowerDefenseMobData mobData = towerDefenseOption.getMobsMap().get(abstractMob);
                        if (mobData instanceof TowerDefenseOption.TowerDefenseAttackingMobData attackingMobData) {
                            TowerDefenseSpawner.assignNextTargetNode(attackingMobData, newCurrentNode.hashCode(), randomPath.getA().getEdges().get(newCurrentNode));
                            abstractMob.getNpc().teleport(playerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                            towerDefenseSpawner.pathFindToNextWaypoint(abstractMob, attackingMobData);
                        }
                    }
                    ChatChannels.sendDebugMessage(player, Component.text("Spawned " + amount + " Mobs", NamedTextColor.GREEN));
                }
                break;
            }
        }
    }

}
