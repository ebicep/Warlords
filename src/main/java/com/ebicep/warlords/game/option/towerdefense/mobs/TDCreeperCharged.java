package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class TDCreeperCharged extends TowerDefenseMob implements BasicMob {

    public TDCreeperCharged(Location spawnLocation) {
        this(
                spawnLocation,
                "Charged Creeper",
                1000,
                .3f,
                0,
                100,
                100
        );
    }

    public TDCreeperCharged(
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
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_CREEPER_CHARGED;
    }

}