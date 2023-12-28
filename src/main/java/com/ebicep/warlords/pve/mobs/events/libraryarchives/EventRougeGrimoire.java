package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;

import java.util.EnumSet;

public class EventRougeGrimoire extends EventGrimoire {

    public EventRougeGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Rouge Grimoire",
                12000,
                0.21f,
                5,
                350,
                700
        );
    }

    public EventRougeGrimoire(
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
        return Mob.EVENT_ROUGE_GRIMOIRE;
    }

    @Override
    public void onNPCCreate() {
        super.onNPCCreate();
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                name,
                "Xan7ArR2jjymqG2NXzpB4Fd+fO8804lMgLBsxHzEXgtnoaGzR4OQeYJn5katBdfxSvf8CccTXYjMLR4y49evP5pCK7anlsbEq+jGBWDKrwExtpk6TLPDp2IczhPXEosoZ8rZ9xfs0x4y6ZydrggDA44Wetpam8vqAa9sZvJxC7kjfFClVTb3zCQttRMgNSF1lr5fglNJw54knRohJ79xxKZk7ENMsZXBIBYRGvbAbrhsdSb0VTHUsOPvou4vuqUQP98Og4Tz/DEnzi9khoxnV3H3hEd9SZHiRIFsSZjlxvbm07Huvj4d0qpX+WomJFHdYdUR7TOqUOm7e3OcT/GZXm4EL5nqrfiXQykaGimaycmK1RCkp+yOjoqyKMDrQB99RBpS3y1cN+DM4/K9cSuEUm0DTpGUTA7zZJ55T8B4dnDirUoVP0/6okAt+uZIpJeEz+GuliJ6+VtaknNhPV2DzcHvY7CmmL1robaZGH7+/05JUYy84c82D5y4TkcsDA0wWX9vk+2WwMXG5HBv6xWfSoGuCAQ8G6ukYoPnKWfnPxztXfotLMTgeeraJJWb+LbZomUaqUYBPRGMq+NJZfZSYDtwyeX8mrORDpPvUpT2bsj1lByIIeiXnYr4hfBHm7MtA4pvKN46a2TrMGJiJAQdF8KmJelsERyDevVDSkT3QqU=",
                "ewogICJ0aW1lc3RhbXAiIDogMTcwMzcyNDU0NzEwNCwKICAicHJvZmlsZUlkIiA6ICI5YzM1ZGU3MjdmMzU0ZTVlYjFiOWRhOGViYTZhYzM1YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJtb21teXR3ZXJrZXVzZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MDE2MDEzNDY1NjVhOTRjZTY0MTAwZDRhZDk3ZmFmMzExN2ZkYTM2NTViNTY0YzE0MWI0NzA1NDQ3NzMwZjVlIgogICAgfQogIH0KfQ=="
        );
    }

}
