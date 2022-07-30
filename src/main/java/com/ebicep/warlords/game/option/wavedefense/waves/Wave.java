package com.ebicep.warlords.game.option.wavedefense.waves;

import com.ebicep.warlords.game.option.wavedefense.PartialMonster;
import org.bukkit.Location;

import java.util.Random;

public interface Wave {

    PartialMonster spawnRandomMonster(Location loc, Random random);

    PartialMonster spawnMonster(Location loc);

    int getMonsterCount();

    int getDelay();

    String getMessage();
}
