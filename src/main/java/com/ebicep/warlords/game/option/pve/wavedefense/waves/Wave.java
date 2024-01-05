package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;

import javax.annotation.Nullable;

public interface Wave {

    @Nullable
    AbstractMob spawnMonster(Location loc);

    int getMonsterCount();

    int getDelay();

    int getSpawnTickPeriod();

    Component getMessage();

    void tick(PveOption pveOption, int ticksElapsed);

}
