package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.EliteMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class ZombieWarped extends AbstractMob implements EliteMob {

    public ZombieWarped(Location spawnLocation) {
        super(
                spawnLocation,
                "Warped Guardian",
                11000,
                0.28f,
                20,
                600,
                800
        );
    }

    public ZombieWarped(
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
                maxMeleeDamage
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.ZOMBIE_WARPED;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.playFirework(
                warlordsNPC.getLocation(),
                FireworkEffect.builder()
                        .withColor(Color.PURPLE)
                        .with(FireworkEffect.Type.STAR)
                        .build()
        );
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 2, 0.7f);
        receiver.addSpeedModifier(attacker, "End Slowness", -20, 2 * 20);
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ENDERMAN_SCREAM, 2, 0.4f);
    }
}
