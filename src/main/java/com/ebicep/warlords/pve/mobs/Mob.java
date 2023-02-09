package com.ebicep.warlords.pve.mobs;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.util.java.RandomCollection;

public interface Mob {

    double weaponDropRate();

    int commonWeaponDropChance();

    int rareWeaponDropChance();

    int epicWeaponDropChance();

    default AbstractWeapon generateWeapon(WarlordsPlayer warlordsPlayer) {
        return new RandomCollection<AbstractWeapon>()
                .add(commonWeaponDropChance(), new CommonWeapon(warlordsPlayer))
                .add(rareWeaponDropChance(), new RareWeapon(warlordsPlayer))
                .add(epicWeaponDropChance(), new EpicWeapon(warlordsPlayer))
                .next();
    }

//    MobDrops
//
//    double mobDropRate();


}
