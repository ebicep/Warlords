package com.ebicep.warlords.pve.mobs.slime;

import com.ebicep.customentities.nms.pve.CustomSlime;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class BasicSlime extends AbstractSlime implements BasicMob {

    private static final double hitRadius = 2.5;
    private final double shimmerRadius = 3;

    public BasicSlime(Location spawnLocation) {
        super(
                spawnLocation,
                "Lunar Anomaly",
                MobTier.BASE,
                null,
                3000,
                0.5f,
                20,
                0,
                0,
                new Shimmer()
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
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
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, PveOption option) {
        super.onDeath(killer, deathLocation, option);
        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(warlordsNPC, shimmerRadius, shimmerRadius, shimmerRadius)
                .aliveEnemiesOf(warlordsNPC)
        ) {
            enemy.getCooldownManager().addRegularCooldown(
                    "Shimmer",
                    "SHM",
                    CustomSlime.class,
                    new CustomSlime(spawnLocation.getWorld()),
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
                            enemy.addDamageInstance(
                                    warlordsNPC,
                                    "Shimmer",
                                    healthDamage,
                                    healthDamage,
                                    0,
                                    100
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

    private static class Shimmer extends AbstractAbility {

        public Shimmer() {
            super("Shimmer", 0.3f, 50);
        }

        @Override
        public void updateDescription(Player player) {

        }

        @Override
        public List<Pair<String, String>> getAbilityInfo() {
            return null;
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
            wp.subtractEnergy(energyCost, false);

            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(wp, hitRadius, hitRadius, hitRadius)
                    .aliveEnemiesOf(wp)
            ) {
                enemy.addDamageInstance(wp, "Shimmer", 400, 400, 0, 100);
            }
            return true;
        }
    }

}
