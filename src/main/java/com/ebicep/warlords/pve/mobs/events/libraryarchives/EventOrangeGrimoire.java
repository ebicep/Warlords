package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;

import java.util.EnumSet;

public class EventOrangeGrimoire extends EventGrimoire {

    public EventOrangeGrimoire(Location spawnLocation) {
        this(
                spawnLocation,
                "Orange Grimoire",
                12000,
                0.38f,
                5,
                350,
                500
        );
    }

    public EventOrangeGrimoire(
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
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                name,
                "sT14LcB0+yMmBU9EiV6mQa7PvcktWls2R5xCpMfRHch9Vb5kN2L7Gv3ZSEr4632Kz+V4EDBNP4a/j0Typyd88fHRdXYg7HwwL68dPLD7XdGS43TVL93B+r3pTHiULvgEgBFEYV7u5BkD/dmyKM1MliluD5dnyapvV9wev2vxXEU3iklZBYyPfh148O8kFqjVJZIF1k13xZWEl4eV9wywMe7/Ns6kfvm3a9LnhtbtGcJunVFlsS9ru0i8uxv9ns5XeDo5I5Wewel43XrgGZrvZ7tbdvlb99ITP/xETscHeCJfHJrtKj9LDnPsxcVA8HgI4pDdEnKX0gsI0y+e8b3jkg2yv8fIeMXtcxOn4qHhcgVYdinCPECRBBLdd+m0HtAASFWKLlHWo9zoDuyNvrHJ77tWgeNVamlTComD/L69acd5yWJKWrsxrh17lW4VpEP031ptJ0W31qb61iZIBzcAR0GMHLGmJIf1SdOAJICJRT+vbljP3qG1eYAmEK7kVNmESJt9XQD9wQDhyLFpqj0RmSuHDrclKJbQYFDYdBXOhKMTgA6ZMMsHrxZw8kFSsbfNJ2pxhUuZ9Hrq2gBqSCmrxL6Xzhxxe7mOaKWrZZUH0Ar9y8Jhj8ehI9iojurIfr1+HSM6UJK9abXdWtpPog2MPjeRG8sEWGFroeW+g+Z4yLE=",
                "ewogICJ0aW1lc3RhbXAiIDogMTcwMzcyNDY1NzM0MSwKICAicHJvZmlsZUlkIiA6ICI4N2RlZmVhMTQwMWQ0MzYxODFhNmNhOWI3ZGQ2ODg0MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTcGh5bnhpdHMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjI5NzlmMDA1ZmYwOTcyNDc4N2UzMWQ2YzE4ODNiYjMzMTIwNjM5NWQ5YjQ5MzA3OWQzYzAzMmY3ODk0NjZlZiIKICAgIH0KICB9Cn0="
        );
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
