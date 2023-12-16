package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.Location;

import java.util.EnumSet;

public class EventOrangeGrimoire extends EventGrimoire {

    public EventOrangeGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Orange Grimoire",
                12000,
                0,
                5,
                350,
                700
        );
    }

    public EventOrangeGrimoire(
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
        return 28;
    }

    @Override
    public EnumSet<Ability> getAbilities() {
        return EnumSet.noneOf(Ability.class);
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_ORANGE_GRIMOIRE;
    }

    @Override
    public EnumSet<Ability> getAbilitiesM1() {
        return EnumSet.of(
                Ability.HEALING_RAIN,
                Ability.HAMMER_OF_LIGHT,
                Ability.BERSERK,
                Ability.UNDYING_ARMY,
                Ability.VINDICATE,
                Ability.DIVINE_BLESSING
        );
    }

    @Override
    public EnumSet<Ability> getAbilitiesM2() {
        return EnumSet.of(
                Ability.INFERNO,
                Ability.ICE_BARRIER,
                Ability.INSPIRING_PRESENCE,
                Ability.LAST_STAND,
                Ability.DEATHS_DEBT,
                Ability.HEALING_TOTEM,
                Ability.ORDER_OF_EVISCERATE,
                Ability.DRAINING_MIASMA
        );
    }

}
