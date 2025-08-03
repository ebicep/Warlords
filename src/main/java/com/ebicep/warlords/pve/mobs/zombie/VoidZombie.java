package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AdvancedVoidShred;
import com.ebicep.warlords.pve.mobs.tiers.AdvancedMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class VoidZombie extends AbstractMob implements AdvancedMob {

    private static final int voidRadius = 4;

    public VoidZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Singularity",
                11000,
                0.1f,
                0,
                1500,
                2000,
                new VoidShred(),
                new AdvancedVoidShred(AbstractAbilityBuilder.create("zombieSingularityAdvancedVoidShred").pve(), 200, 300, -70, voidRadius, 10)
        );
    }

    public VoidZombie(
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
                new VoidShred(),
                new AdvancedVoidShred(AbstractAbilityBuilder.create("zombieSingularityAdvancedVoidShred").pve(), 200, 300, -70, voidRadius, 10)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.VOID_ZOMBIE;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 2);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 8 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation(),
                    voidRadius,
                    new CircumferenceEffect(Particle.FIREWORK, Particle.FIREWORK).particlesPerCircumference(0.6),
                    new DoubleLineEffect(Particle.EFFECT)
            ).playEffects();
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(
                deathLocation,
                FireworkEffect.builder()
                              .withColor(Color.WHITE)
                              .with(FireworkEffect.Type.BURST)
                              .withTrail()
                              .build(),
                1
        );
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_ZOMBIE_DEATH, 2, 0.4f);
    }

    private static class VoidShred extends AbstractAbility {

        public VoidShred() {
            super(AbstractAbilityBuilder.create("zombieSingularityVoidShred").pve());
        }

        @Override
        protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
            float healthDamage = wp.getMaxHealth() * 0.01f;
            wp.addInstance(InstanceBuilder
                    .damage()
                    .cause("Void Shred")
                    .source(wp)
                    .value(healthDamage)
            );
            return true;
        }
    }
}
