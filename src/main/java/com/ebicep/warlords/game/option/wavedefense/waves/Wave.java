package com.ebicep.warlords.game.option.wavedefense.waves;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.Location;

public interface Wave {

    AbstractMob<?> spawnRandomMonster(Location loc);

    AbstractMob<?> spawnMonster(Location loc);

    int getMonsterCount();

    int getDelay();

    String getMessage();
}
