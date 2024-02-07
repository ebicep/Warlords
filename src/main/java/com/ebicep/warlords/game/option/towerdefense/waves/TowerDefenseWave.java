package com.ebicep.warlords.game.option.towerdefense.waves;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.Location;

import javax.annotation.Nullable;

public interface TowerDefenseWave {

    @Nullable
    AbstractMob spawnMob(Location location);

    int getSpawnCount();

    int getSpawnDelay();

    int getSpawnPeriod();

}
