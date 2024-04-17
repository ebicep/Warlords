package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.BasicScoreboardOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.SpawnpointOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pve.CurrencyOnEventOption;
import com.ebicep.warlords.game.option.towerdefense.TowerBuildOption;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseSpawner;
import com.ebicep.warlords.game.option.towerdefense.path.TowerDefenseDirectAcyclicGraph;
import com.ebicep.warlords.game.option.towerdefense.waves.FixedWave;
import com.ebicep.warlords.game.option.towerdefense.waves.TowerDefenseDelayWaveAction;
import com.ebicep.warlords.game.option.towerdefense.waves.WaveEndCondition;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationFactory;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.dag.Node;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

import java.util.EnumSet;
import java.util.List;

import static com.ebicep.warlords.util.warlords.GameRunnable.SECOND;

public class TowerDefenseTest extends GameMap {

    public TowerDefenseTest() {
        super(
                "Tower Defense Test",
                32,
                12,
                60 * SECOND,
                "TD",
                3,
                GameMode.TOWER_DEFENSE
        );
    }

    @Override
    public List<Option> initMap(GameMode category, LocationFactory loc, EnumSet<GameAddon> addons) {
        List<Option> options = category.initMap(this, loc, addons);
        options.add(TeamMarker.create(Team.GAME, Team.BLUE).asOption());
        LocationBuilder blueSpawn = loc.addXYZ(2.5, 65, 0.5, 90, 0);
        LocationBuilder redSpawn = loc.addXYZ(-52.5, 65, 8.5, -90, 0);

        options.add(LobbyLocationMarker.create(blueSpawn, Team.BLUE).asOption());
//        options.add(LobbyLocationMarker.create(redSpawn, Team.RED).asOption());
        options.add(SpawnpointOption.forTeam(blueSpawn, Team.BLUE));
//        options.add(SpawnpointOption.forTeam(redSpawn, Team.RED));

        options.add(new TowerDefenseOption()
                        .addCastle(Team.BLUE, loc.addXYZ(-39.5, 70, -9.5), 100_000)
//                .addCastle(Team.RED, loc.addXYZ(-10.5, 70, 18.5), 100_000)
        );

        List<Location> bluePath1 = List.of(
                loc.addXYZ(-12.5, 65, 0.5),
                loc.addXYZ(-12.5, 65, -16.5),
                loc.addXYZ(-22.5, 65, -16.5),
                loc.addXYZ(-22.5, 65, -9.5),
                loc.addXYZ(-39.5, 65, -9.5)
        );
        List<Location> bluePath2 = List.of(
                loc.addXYZ(-12.5, 65, 0.5),
                loc.addXYZ(-26.5, 65, 0.5),
                loc.addXYZ(-26.5, 65, -9.5),
                loc.addXYZ(-39.5, 65, -9.5)
        );
        List<Location> redPath1 = List.of(
                loc.addXYZ(-37.5, 65, 8.5),
                loc.addXYZ(-37.5, 65, 25.5),
                loc.addXYZ(-27.5, 65, 25.5),
                loc.addXYZ(-27.5, 65, 18.5),
                loc.addXYZ(-10.5, 65, 18.5)
        );
        List<Location> redPath2 = List.of(
                loc.addXYZ(-37.5, 65, 8.5),
                loc.addXYZ(-23.5, 65, 8.5),
                loc.addXYZ(-23.5, 65, 18.5),
                loc.addXYZ(-10.5, 65, 18.5)
        );
//        options.add(new TowerDefenseSpawner()
//                        .addPath(blueSpawn, bluePath1)
//                        .addPath(blueSpawn, bluePath2)
//                        .addPath(redSpawn, redPath1)
//                        .addPath(redSpawn, redPath2)
//                .add(new FixedWave()
//                        .add(Mob.ZOMBIE_I, 1)
//                        .delay(5 * SECOND)
//                        .add(Mob.ZOMBIE_I, 5)
//                        .delay(10 * SECOND)
//                        .add(Mob.ZOMBIE_I, 5)
//                )
//                .add(new FixedWave()
//                        .add(Mob.ZOMBIE_I, 5)
//                )
//        );
        options.add(new TowerBuildOption()
                        .addBuildableArea(Team.BLUE, loc.addXYZ(-0.5, 60, 3.5), loc.addXYZ(-49.5, 80, -23.5))
//                .addBuildableArea(Team.RED, loc.addXYZ(-0.5, 60, 5.5), loc.addXYZ(-49.5, 80, 32.5))
        );

        for (Option option : options) {
            if (option instanceof TowerDefenseOption towerDefenseOption) {
                options.add(new CurrencyOnEventOption()
                        .startWith(10000)
                        .setCurrentCurrencyDisplay(warlordsEntity -> Component.text("Insignia: ", NamedTextColor.YELLOW)
                                                                              .append(Component.text("❂ " + NumberFormat.formatOptionalHundredths(warlordsEntity.getCurrency()),
                                                                                      NamedTextColor.GOLD
                                                                              )))
                        .setCurrencyRate(new CurrencyOnEventOption.CurrencyRate(
                                20 * 5,
                                warlordsEntity -> towerDefenseOption.getPlayerInfo(warlordsEntity).getIncomeRate(),
                                warlordsEntity -> Component.text("Income: ", NamedTextColor.DARK_AQUA)
                                                           .append(Component.text("❂ " + NumberFormat.formatOptionalHundredths(towerDefenseOption.getPlayerInfo(warlordsEntity)
                                                                                                                                                 .getIncomeRate()),
                                                                   NamedTextColor.GOLD
                                                           )),
                                nextCurrency -> Component.text("Next Income: ", NamedTextColor.GRAY).append(Component.text(nextCurrency, NamedTextColor.WHITE))
                        ))
                );
                break;
            }
        }

        options.add(new BasicScoreboardOption());

        Node<Location> blueSpawnNode = new Node<>(blueSpawn);

        Node<Location> node1 = new Node<>(loc.addXYZ(-12.5, 65, 0.5));
        Node<Location> node2 = new Node<>(loc.addXYZ(-12.5, 65, -16.5));
        Node<Location> node3 = new Node<>(loc.addXYZ(-22.5, 65, -16.5));
        Node<Location> node4 = new Node<>(loc.addXYZ(-22.5, 65, -9.5));
        Node<Location> node5 = new Node<>(loc.addXYZ(-39.5, 65, -9.5));

        Node<Location> node6 = new Node<>(loc.addXYZ(-26.5, 65, 0.5));
        Node<Location> node7 = new Node<>(loc.addXYZ(-26.5, 65, -9.5));

        TowerDefenseDirectAcyclicGraph bluePath = new TowerDefenseDirectAcyclicGraph(blueSpawnNode)
                .addNodes(node1, node2, node3, node4, node5, node6, node7)
                .addEdge(blueSpawnNode, node1)
                .addEdge(node1, node2)
                .addEdge(node2, node3)
                .addEdge(node3, node4)
                .addEdge(node4, node5)
                .addEdge(node1, node6)
                .addEdge(node6, node7)
                .addEdge(node7, node5);

        options.add(new TowerDefenseSpawner()
                .addPath(blueSpawn, bluePath)
                .add(new FixedWave()
                        .add(Mob.TD_ZOMBIE, 20, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_ZOMBIE, 35, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_ZOMBIE, 25, 10)
                        .add(Mob.TD_SKELETON, 5, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_ZOMBIE, 35, 10)
                        .add(Mob.TD_SKELETON, 10, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 5, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_ZOMBIE, 5, 10)
                        .add(Mob.TD_SKELETON, 15, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 10, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_ZOMBIE, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_SKELETON, 15, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 10, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_ZOMBIE, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_SKELETON, 15, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 5, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_HUSK, 5, 10)
                        .add(Mob.TD_ZOMBIE, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_SKELETON, 15, 10)
                        .add(Mob.TD_HUSK, 2, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 7, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_HUSK, 10, 10)
                        .add(Mob.TD_SPIDER, 5, 10)
                        .add(Mob.TD_ZOMBIE, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_SPIDER, 5, 10)
                        .add(Mob.TD_SKELETON, 20, 10)
                        .add(Mob.TD_HUSK, 7, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_SPIDER, 7, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 5, 10)
                )
                .add(new FixedWave()
                        .add(Mob.TD_HUSK, 10, 10)
                        .add(Mob.TD_STRAY, 5, 10)
                        .add(Mob.TD_SPIDER, 5, 10)
                        .add(Mob.TD_ZOMBIE, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_STRAY, 5, 10)
                        .add(Mob.TD_SPIDER, 10, 10)
                        .add(Mob.TD_SKELETON, 15, 10)
                        .add(Mob.TD_HUSK, 5, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 5, 10)
                        .add(Mob.TD_ZOMBIE_VILLAGER, 5, 10)
                        .add(Mob.TD_STRAY, 10, 10)
                        .add(Mob.TD_SPIDER, 5, 10)
                        .add(Mob.TD_ZOMBIE_BABY, 5, 10)
                )
                .applyToAllWaves(wave -> wave.forEach(w -> {
                            w.getActions().add(0, new TowerDefenseDelayWaveAction(20));
                            w.getEndConditions().add(WaveEndCondition.allMobsDeadAnySide());
                        })
                )
        );
        return options;
    }
}
