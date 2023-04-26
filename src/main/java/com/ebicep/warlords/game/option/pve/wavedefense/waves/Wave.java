package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

public interface Wave {

    AbstractMob<?> spawnRandomMonster(Location loc);

    AbstractMob<?> spawnMonster(Location loc);

    int getMonsterCount();

    int getDelay();

    Component getMessage();
}
