package com.ebicep.warlords.game.option.towerdefense.attributes;

import com.ebicep.warlords.game.option.towerdefense.towers.AbstractTower;
import com.ebicep.warlords.game.option.towerdefense.towers.TowerDefenseTowerMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public interface Spawner {

    static List<LocationUtils.LocationXYZ> getBlockSpawnLocations(Location center, float radius, Material material) {
        return LocationUtils.getCircleBlocks(center, (int) radius)
                            .stream()
                            .filter(block -> block.getType() == material)
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

}
