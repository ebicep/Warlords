package com.ebicep.warlords.pve.mobs.events.libraryarchives;

import com.ebicep.warlords.abilities.internal.Ability;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.pve.mobs.Mob;
import net.citizensnpcs.trait.SkinTrait;
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
    public void onNPCCreate() {
        super.onNPCCreate();
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinPersistent(
                name,
                "MPsRvazgElFA1uQVCcQu/67ZWrr9ouOLY2MDvFibNxmQE9HHu6IaT5gmPw55UDzRW1PjtagUZrxlTIACQoWefr3t7s5VttkM/iXOtVvAbF17Hiiic0bfSWlYBLUrtkQAGgpUYSEEps5UEa52YeiQEB6ABMYFFkV2jmztHrtDWHabsLlzrsWEWZB8eDIKVac2bXKMDGs1h/mygkZZ6PGYN6CpjOIXLWzcQBxqsWCnpLoPNBmQm5Tr21ziMR8XECdweCdJNOShAhAt4ZCWOxhGIZq4P7DRoedd+x/Z2/Mxp5Df7eMiOK4ALeKqbbNJjZovrfbOuogBHinWwhhvk7F38SfsfN8Qd0ZB2zAnfcXFCot2y7gJMmiw6f9O8wE51NJpYMUhuLGqupLlIgTgz8HB0FcIXZ8xT603ArmOzNS3qG7AxQBZTCLyWoVpBylZ7S45owT0h9AVglRSRusp3Tu486j9eDO1Ghx8yi9okgnI8unHnWbhAT/glPRBWEQNjZ2mLFsMcZljPQSo2B8r2rPuudSZ8ZvAH/HME2av5bSwjENc2zD9QiyOVAO5FecYOaWZDSc/T2mSexg2n+azSnvgrlyIe6nXCKzSeGLvQN1x8kRSsWvdfFY5M8uNamtjGyUT1XpDNQK2SV4/phnxKMiQLxGmRBfxlI2iPra28FJq46E=",
                "ewogICJ0aW1lc3RhbXAiIDogMTY5MTA0NjIyMjAwMiwKICAicHJvZmlsZUlkIiA6ICIzYTE5NDgyNTYyZTc0MzFkYmNmOGUwOWE4N2VhMmQ5OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNckxpYW0yNjE0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzI3ZTM5NzU5M2RhMmUyZDBmZDQyYmU0NWYxNWVlMjBkNGMwY2U1NWJmYWYzNGEyOGE1M2NkMWY0OGJjOTY3ZGUiCiAgICB9CiAgfQp9"
        );
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
