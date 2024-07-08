package com.ebicep.warlords.pve.mobs.slime;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SlimeSize;
import org.bukkit.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class SlimyAnomaly extends AbstractMob implements BasicMob {

    private static final double hitRadius = 2.5;
    private final double shimmerRadius = 3;

    public SlimyAnomaly(Location spawnLocation) {
        super(
                spawnLocation,
                "Slimy Anomaly",
                3000,
                0.2f,
                20,
                0,
                0,
                new Shimmer()
        );
    }

    public SlimyAnomaly(
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
                new Shimmer()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SLIMY_ANOMALY;
    }

    @Override
    public void onNPCCreate() {
        npc.getOrAddTrait(SlimeSize.class).setSize(5);
        npc.data().set(NPC.Metadata.JUMP_POWER_SUPPLIER, (Function<NPC, Float>) npc -> .1f);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 4 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation(),
                    hitRadius,
                    new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE).particlesPerCircumference(0.75),
                    new DoubleLineEffect(Particle.SPELL)
            ).playEffects();
        }
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(warlordsNPC, shimmerRadius, shimmerRadius, shimmerRadius)
                .aliveEnemiesOf(warlordsNPC)
        ) {
            enemy.getCooldownManager().addRegularCooldown(
                    "Shimmer",
                    "SHM",
                    SlimyAnomaly.class,
                    null,
                    warlordsNPC,
                    CooldownTypes.DEBUFF,
                    cooldownManager -> {
                    },
                    4 * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 10 == 0) {
                            Location location = enemy.getLocation();
                            location.add(0, 1.5, 0);
                            location.getWorld().spawnParticle(
                                    Particle.SMOKE_NORMAL,
                                    location,
                                    1,
                                    0.3F,
                                    0.3F,
                                    0.3F,
                                    0.02F,
                                    null,
                                    true
                            );
                            location.getWorld().spawnParticle(
                                    Particle.SLIME,
                                    location,
                                    1,
                                    0.3F,
                                    0.3F,
                                    0.3F,
                                    0.5F,
                                    null,
                                    true
                            );

                        }

                        if (ticksLeft % 20 == 0) {
                            float healthDamage = enemy.getMaxHealth() * 0.04f;
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Shimmer")
                                    .source(warlordsNPC)
                                    .value(healthDamage)
                            );
                        }
                    })
            );
        }

        EffectUtils.playFirework(
                deathLocation,
                FireworkEffect.builder()
                   .withColor(Color.GREEN)
                   .with(FireworkEffect.Type.BALL_LARGE)
                   .withTrail()
                   .build(),
                1);
        EffectUtils.playHelixAnimation(deathLocation, shimmerRadius, 0, 255, 0);
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_SLIME_JUMP, 2, 0.5f);
    }

    private static class Shimmer extends AbstractAbility implements Damages<Shimmer.DamageValues> {

        public Shimmer() {
            super("Shimmer", 0.3f, 50);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {


            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(wp, hitRadius, hitRadius, hitRadius)
                    .aliveEnemiesOf(wp)
            ) {
                enemy.addInstance(InstanceBuilder
                        .damage()
                        .ability(this)
                        .source(wp)
                        .value(damageValues.shimmerDamage)
                );
            }
            return true;
        }

        private final DamageValues damageValues = new DamageValues();

        @Override
        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue shimmerDamage = new Value.SetValue(400);
            private final List<Value> values = List.of(shimmerDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }

    }

}
