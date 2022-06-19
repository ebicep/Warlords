package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.util.java.Pair;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface Wave {

    PartialMonster spawnRandomMonster(Location loc, Random random);

    int getMonsterCount();

    String getMessage();

}
