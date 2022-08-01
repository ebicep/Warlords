package com.ebicep.warlords.game.option.wavedefense2.waves2;

import com.ebicep.warlords.game.option.wavedefense2.mobs2.AbstractMob;
import org.bukkit.Location;

public interface Wave {

    AbstractMob<?> spawnRandomMonster(Location loc);

    AbstractMob<?> spawnMonster(Location loc);

    int getMonsterCount();

    int getDelay();

    String getMessage();
}
