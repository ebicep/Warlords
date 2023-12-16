package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

import java.util.EnumSet;

public class RougeGrimoire extends Grimoire {

    public RougeGrimoire(
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
        return 5;
    }

    @Override
    public EnumSet<Ability> getAbilities() {
        return EnumSet.noneOf(Ability.class);
    }

    @Override
    public EnumSet<Ability> getAbilitiesM1() {
        return EnumSet.of(
                Ability.FLAME_BURST,
                Ability.CONSECRATE_AVENGER,
                Ability.SEISMIC_WAVE_BERSERKER,
                Ability.RECKLESS_CHARGE,
                Ability.SPIRIT_LINK,
                Ability.INCENDIARY_CURSE
        );
    }

    @Override
    public EnumSet<Ability> getAbilitiesM2() {
        return EnumSet.of(
                Ability.FREEZING_BREATH,
                Ability.WATER_BREATH,
                Ability.CHAIN_LIGHTNING,
                Ability.BOULDER,
                Ability.SOUL_SHACKLE,
                Ability.SOOTHING_ELIXIR,
                Ability.SOULFIRE_BEAM,
                Ability.GUARDIAN_BEAM,
                Ability.RAY_OF_LIGHT
        );
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }

}
