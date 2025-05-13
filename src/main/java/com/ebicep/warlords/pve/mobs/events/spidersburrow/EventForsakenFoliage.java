package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilities.EarthlivingWeapon;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.flags.Spider;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import org.bukkit.Location;

public class EventForsakenFoliage extends AbstractMob implements BossMinionMob, Spider {


    public EventForsakenFoliage(Location spawnLocation) {
        super(
                spawnLocation,
                "Forsaken Respite",
                2700,
                0.45f,
                0,
                300,
                450,
                new EarthlivingWeapon(AbstractAbilityBuilder.create("forsakenRespiteEarthlivingWeapon").pve().startFullCooldown())
        );
    }

    public EventForsakenFoliage(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new EarthlivingWeapon(AbstractAbilityBuilder.create("forsakenRespiteEarthlivingWeapon").pve().startFullCooldown())
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.EVENT_MITHRA_FORSAKEN_FOLIAGE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        int currentWave = option.getWaveCounter();
        if (currentWave % 5 == 0 && currentWave > 5) {
            float additionalHealthMultiplier = 1 + .15f * (currentWave / 5f - 1);
            warlordsNPC.setMaxHealthAndHeal(warlordsNPC.getMaxBaseHealth() * additionalHealthMultiplier);
        }
        warlordsNPC.getAbilities().get(0).onActivate(warlordsNPC);
    }

}
