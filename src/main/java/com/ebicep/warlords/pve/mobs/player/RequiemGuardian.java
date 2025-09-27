package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.NoTarget;
import com.ebicep.warlords.pve.mobs.flags.NoTargetAbilities;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import org.bukkit.Location;

public class RequiemGuardian extends AbstractMob implements EliteMob, NoTargetAbilities, NoTarget {

    public RequiemGuardian(Location spawnLocation) {
        super(
                spawnLocation,
                "Requiem Guardian",
                6000,
                0.4f,
                20,
                600,
                900
        );
    }

    public RequiemGuardian(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.REQIUEM_GUARDIAN;
    }
}
