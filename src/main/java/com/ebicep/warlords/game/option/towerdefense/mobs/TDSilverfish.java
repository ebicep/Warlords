package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.towerdefense.mobs.attributes.blocking.Unblockable;
import com.ebicep.warlords.game.option.towerdefense.mobs.attributes.type.AirType;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class TDSilverfish extends TowerDefenseMob implements BasicMob {

    public TDSilverfish(Location spawnLocation) {
        this(
                spawnLocation,
                "Silverfish",
                25,
                .4f,
                0,
                100,
                100
        );
    }

    public TDSilverfish(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
        setMobType(AirType.DEFAULT);
        setBlockingMode(Unblockable.DEFAULT);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_SILVERFISH;
    }

}