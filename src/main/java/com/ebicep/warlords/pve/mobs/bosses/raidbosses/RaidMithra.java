package com.ebicep.warlords.pve.mobs.bosses.raidbosses;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.SpawnMobAbility;
import com.ebicep.warlords.pve.mobs.tiers.BossMob;
import com.ebicep.warlords.pve.mobs.tiers.RaidBossMob;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class RaidMithra extends AbstractMob implements RaidBossMob {

    public RaidMithra(Location spawnLocation) {
        this(spawnLocation, "Mithra", 4_000_000, 0.35f, 20, 1200, 1600);
    }

    public RaidMithra(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        LivingEntity entity = (LivingEntity) warlordsNPC.getEntity();
        AttributeInstance scale = entity.getAttribute(Attribute.SCALE);
        if (scale != null) {
            scale.setBaseValue(3);
        }
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.RAID_MITHRA;
    }
}
