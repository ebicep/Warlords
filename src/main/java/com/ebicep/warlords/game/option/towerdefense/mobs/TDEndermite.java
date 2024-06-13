package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.towerdefense.mobs.attributes.blocking.Unblockable;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import org.bukkit.Location;

public class TDEndermite extends TowerDefenseMob implements BasicMob {

    public TDEndermite(Location spawnLocation) {
        this(
                spawnLocation,
                "Endermite",
                50,
                .4f,
                0,
                100,
                100
        );
    }

    public TDEndermite(
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
        setBlockingMode(Unblockable.DEFAULT);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_ENDERMITE;
    }

}