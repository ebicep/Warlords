package com.ebicep.warlords.pve.mobs.mobtypes;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.EpicWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.RareWeapon;
import com.ebicep.warlords.util.java.RandomCollection;

import java.util.HashMap;

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

    default HashMap<MobDrops, HashMap<DifficultyIndex, Double>> mobDrops() {
        return new HashMap<>();
    }

}
