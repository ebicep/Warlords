package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.util.java.RandomCollection;

import java.util.UUID;

public interface Mob {

    double dropRate();

    int commonDropChance();

    int rareDropChance();

    int epicDropChance();

    default AbstractWeapon generateWeapon(UUID uuid) {
        return new RandomCollection<AbstractWeapon>()
                .add(commonDropChance(), new CommonWeapon(uuid))
                .add(rareDropChance(), new RareWeapon(uuid))
                .add(epicDropChance(), new EpicWeapon(uuid))
                .next();
    }


}
