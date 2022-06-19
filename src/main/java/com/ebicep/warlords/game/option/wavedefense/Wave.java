package com.ebicep.warlords.game.option.wavedefense;

import java.util.Random;
import org.bukkit.Location;

public interface Wave {

    PartialMonster spawnRandomMonster(Location loc, Random random);

    int getMonsterCount();

    int getDelay();

    String getMessage();

}
