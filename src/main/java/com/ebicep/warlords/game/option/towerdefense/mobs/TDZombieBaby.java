package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import net.citizensnpcs.trait.Age;
import org.bukkit.Location;

public class TDZombieBaby extends TowerDefenseMob implements BasicMob {

    public TDZombieBaby(Location spawnLocation) {
        this(
                spawnLocation,
                "Baby Zombie",
                35,
                .5f,
                0,
                100,
                100
        );
    }

    public TDZombieBaby(
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
    public void onNPCCreate() {
        super.onNPCCreate();
        npc.getOrAddTrait(Age.class).setAge(-24000);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.TD_ZOMBIE;
    }

}