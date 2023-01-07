package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.util.java.RandomCollection;

public interface Mob {

    double dropRate();

    int commonDropChance();

    int rareDropChance();

    int epicDropChance();

    default AbstractWeapon generateWeapon(WarlordsPlayer warlordsPlayer) {
        return new RandomCollection<AbstractWeapon>()
                .add(commonDropChance(), new CommonWeapon(warlordsPlayer))
                .add(rareDropChance(), new RareWeapon(warlordsPlayer))
                .add(epicDropChance(), new EpicWeapon(warlordsPlayer))
                .next();
    }


}
