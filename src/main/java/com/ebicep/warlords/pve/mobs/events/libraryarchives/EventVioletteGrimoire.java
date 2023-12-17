package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

import java.util.EnumSet;

public class EventVioletteGrimoire extends EventGrimoire {

    public EventVioletteGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Violette Grimoire",
                12000,
                0.38f,
                5,
                350,
                700
        );
    }

    public EventVioletteGrimoire(
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
        return 9;
    }

    @Override
    public EnumSet<Ability> getAbilities() {
        return EnumSet.noneOf(Ability.class);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_VIOLETTE_GRIMOIRE;
    }

    @Override
    public EnumSet<Ability> getAbilitiesM1() {
        return EnumSet.of(
                Ability.LIGHT_INFUSION_CRUSADER,
                Ability.GROUND_SLAM_BERSERKER,
                Ability.EARTHLIVING_WEAPON,
                Ability.SHADOW_STEP,
                Ability.VITALITY_LIQUOR
        );
    }

    @Override
    public EnumSet<Ability> getAbilitiesM2() {
        return EnumSet.of(
                Ability.TIME_WARP_CRYOMANCER,
                Ability.WINDFURY_WEAPON,
                Ability.HEART_TO_HEART,
                Ability.ENERGY_SEER_LUMINARY
        );
    }

}
