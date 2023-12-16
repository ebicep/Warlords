package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

import java.util.EnumSet;

public class BleueGrimoire extends Grimoire {

    public BleueGrimoire(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            int damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage,
            AbstractAbility... abilities
    ) {
        super(spawnLocation, name, maxHealth, walkSpeed, damageResistance, minMeleeDamage, maxMeleeDamage, abilities);
    }

    @Override
    public int getAbilityActivationPeriod() {
        return 12;
    }

    @Override
    public EnumSet<Ability> getAbilities() {
        return EnumSet.of(
                Ability.REPENTANCE
        );
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }

    @Override
    public EnumSet<Ability> getAbilitiesM1() {
        return EnumSet.of(
                Ability.ARCANE_SHIELD,
                Ability.BLOOD_LUST,
                Ability.LIGHTNING_ROD,
                Ability.CHAIN_HEAL,
                Ability.CONTAGIOUS_FACADE
        );
    }

    @Override
    public EnumSet<Ability> getAbilitiesM2() {
        return EnumSet.of(
                Ability.HOLY_RADIANCE_PROTECTOR,
                Ability.INTERVENE,
                Ability.ORBS_OF_LIFE,
                Ability.PRISM_GUARD,
                Ability.REMEDIC_CHAINS,
                Ability.MYSTICAL_BARRIER,
                Ability.SANCTIFIED_BEACON
        );
    }

}
