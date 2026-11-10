package com.ebicep.warlords.pve.mobs.player;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.NoTarget;
import com.ebicep.warlords.pve.mobs.flags.NoTargetAbilities;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.pve.mobs.tiers.PlayerMob;
import org.bukkit.Location;
import org.bukkit.entity.Zombie;

public class RequiemGuardian extends AbstractMob implements PlayerMob, NoTargetAbilities, NoTarget {

    public RequiemGuardian(Location spawnLocation) {
        super(
                spawnLocation,
                "Requiem Guardian",
                6000,
                0.3f,
                15,
                450,
                700
        );
    }

    public RequiemGuardian(Location spawnLocation, String name, int maxHealth, float walkSpeed, float damageResistance, float minMeleeDamage, float maxMeleeDamage) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.REQUIEM_GUARDIAN;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        if (warlordsNPC.getEntity() instanceof Zombie zombie) {
            zombie.setBaby(true);
        }
    }
}
