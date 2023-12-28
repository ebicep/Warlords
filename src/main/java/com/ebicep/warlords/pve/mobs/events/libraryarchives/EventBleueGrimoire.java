package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;

import java.util.EnumSet;

public class EventBleueGrimoire extends EventGrimoire {

    public EventBleueGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Rouge Grimoire",
                12000,
                0.38f,
                5,
                350,
                700
        );
    }

    public EventBleueGrimoire(
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
    public void onNPCCreate() {
        super.onNPCCreate();
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                name,
                "j+SJ67rQbi1VGxu6Jnc/RiWEhLu2aL6Vb9qyRWTXob9+cb979k7FGd++2p+L0j4iGSJaL2J+UwB77xxIGrZ0M7MdUdwBMZ4Iy7IL5Gt/Ngu2SuYoJvP6tR4kaxf4yOAO8OyCgncGdA/j3HFmM8ct9oOmlUa/L+/UTJZqhCWCqE4G9nMa9/02NW4BCfhTdcNO0VsPP1hhhh+VlIF8kgJ6V9ysVh93KM0BtXjh7btPhpZjucVrIZK6AP1M1u0dA4T0tpfupN/mp0+wEvu0jMBX7aORG6EyqXJD7HzGdGUb7/36cVaMYVEDRgK5dcWfaSWeCdriD6hXxD2ZsLWKKU1dDDte7Ra8j3dh6sZtE+wl2oD12ySkrN+Y3TtPV/FHHLB26IiM/hnVjeWFCf7sfkxU8Fod0UgFICNFP5j6tjVj3tqaAm/Y6I1ewTQbUq0LO0CcZDrc/ts+ODNn5ikpLcsrqwTTXHGPxJLEKcsO1w8OzNnyEDAFJmAaukvkdCRtifMzJIzbxhvTWcbuU29BwMl0no18Ysbm9omzpDyyWIzTLePBJ0ZZCUsX09yOfKisgjMZBd6CbrkhqqEE9km2a1C55QRad44NgPiOx4RHsrS2RGD0xUXxagP+PT8/w+mSe4G/q85NguL7Zn9wpEVAjQcvQf6l1xRPkgcIG8j4Qnizn5s=",
                "ewogICJ0aW1lc3RhbXAiIDogMTcwMzcyNDYyMDIwMSwKICAicHJvZmlsZUlkIiA6ICI4ZDYwNGY0NWM0OWQ0YWE2Yjc0MjhiNTJlYzcyYjliNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdGFyRG9ubiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lOTljNDZkMmU1NmNmNzI0MTE0NWRkYzQ4ZGY4MjNiOGU1NDRmMzQwYzdiZGZkNWRjZDk0MjliMTQxMDEzNDlhIgogICAgfQogIH0KfQ=="
        );
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
        return Mob.EVENT_BLEUE_GRIMOIRE;
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
