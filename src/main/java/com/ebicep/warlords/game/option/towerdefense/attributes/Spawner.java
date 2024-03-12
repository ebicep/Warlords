package com.ebicep.warlords.game.option.towerdefense.attributes;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseUtils;
import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerDefenseTowerMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public interface Spawner {

    static List<LocationUtils.LocationXYZ> getBlockSpawnLocations(AbstractTower tower, FloatModifiable range) {
        return getBlockSpawnLocations(
                tower.getTowerDefenseOption().getTowerBuildOption().getBuildableArea(tower.getTeam()),
                tower.getBottomCenterLocation().clone().add(0, -1, 0),
                range.getCalculatedValue(),
                tower.getTowerDefenseOption().getMobPathMaterial()
        );
    }

    static List<LocationUtils.LocationXYZ> getBlockSpawnLocations(List<Pair<Location, Location>> spawnableAreas, Location center, float radius, Material material) {
        return LocationUtils.getCircleBlocks(center, (int) radius)
                            .stream()
                            .filter(block -> block.getType() == material)
                            .filter(block -> spawnableAreas.stream().anyMatch(pair -> TowerDefenseUtils.insideArea(block.getLocation(), pair.getA(), pair.getB())))
                            .map(block -> {
                                Location location = block.getLocation();
                                return new LocationUtils.LocationXYZ(location.getX() + .5, location.getY() + 1, location.getZ() + .5);
                            })
                            .toList();

    }

    default Location getSpawnLocation(AbstractTower tower) {
        List<LocationUtils.LocationXYZ> spawnLocations = getBlockSpawnLocations();
        LocationUtils.LocationXYZ randomSpawnLocation = spawnLocations.get(ThreadLocalRandom.current().nextInt(spawnLocations.size()));
        return new Location(tower.getTopCenterLocation().getWorld(), randomSpawnLocation.x(), randomSpawnLocation.y(), randomSpawnLocation.z());
    }

    List<LocationUtils.LocationXYZ> getBlockSpawnLocations();

    List<TowerDefenseTowerMob> getSpawnedMobs();

    default void renderSpawnLocations(World world) {
        for (LocationUtils.LocationXYZ blockSpawnLocation : getBlockSpawnLocations()) {
            EffectUtils.displayParticle(
                    Particle.VILLAGER_HAPPY,
                    new Location(world, blockSpawnLocation.x(), blockSpawnLocation.y() + .1, blockSpawnLocation.z()),
                    1
            );
        }
    }

}
