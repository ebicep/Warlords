package com.ebicep.warlords.game.option.towerdefense.mobs.attributes.type;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.LocationBuilder;

public interface TDMobType {

    default void onSpawn(AbstractMob mob) {

    }

    default void onNextLocationSet(LocationBuilder nextTarget) {

    }

}

