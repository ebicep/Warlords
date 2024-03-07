package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilities.EarthlivingWeapon;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
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
                new EarthlivingWeapon() {{ // Attacks are converted into Earth Living with double the proc chance as standard.
                    setProcChance(getProcChance() * 2);
                    setTickDuration(18000);
                }}
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
                new EarthlivingWeapon() {{ // Attacks are converted into Earth Living with double the proc chance as standard.
                    setProcChance(getProcChance() * 2);
                    setTickDuration(18000);
                }}
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

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

}
