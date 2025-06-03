package com.ebicep.warlords.pve.mobs.skeleton;

import com.ebicep.warlords.abilities.Fireball;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.AbstractAbilityBuilder;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.WoundingCooldown;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import javax.annotation.Nonnull;
import java.util.Collections;


public class SkeletalSorcerer extends AbstractMob implements ChampionMob {

    public SkeletalSorcerer(Location spawnLocation) {
        super(
                spawnLocation,
                "Skeletal Sorcerer",
                8000,
                0.3f,
                10,
                800,
                1000,
                new Fireball(AbstractAbilityBuilder.create("skeletalSorcererFireball").pve()),
                new BlightedScorch()
        );
    }

    public SkeletalSorcerer(
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
                new Fireball(AbstractAbilityBuilder.create("skeletalSorcererFireball").pve()),
                new BlightedScorch()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SKELETAL_SORCERER;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);

        warlordsNPC.addKnockbackModifier(warlordsNPC, "KB RES", -100, -1);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        warlordsNPC.getSpeed().removeNegativeModifiers();
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        WoundingCooldown.addWoundingCooldown(
                receiver,
                name,
                attacker,
                50,
                100
        );
    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, @Nonnull PveOption option) {
        super.onDeath(killer, deathLocation, option);
        EffectUtils.playFirework(deathLocation, FireworkEffect.builder()
                                                              .withColor(Color.ORANGE)
                                                              .with(FireworkEffect.Type.BURST)
                                                              .withTrail()
                                                              .build()
        );
        Utils.playGlobalSound(deathLocation, Sound.ENTITY_SKELETON_DEATH, 2, 0.4f);
    }

    private static class BlightedScorch extends AbstractAbility {

        public BlightedScorch() {
            super(AbstractAbilityBuilder.create("skeletalSorcererBlightedScorch").pve());
        }

        @Override
        protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {


            EffectUtils.playSphereAnimation(wp.getLocation(), 6, Particle.FLAME, 1);
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(wp, 6, 6, 6)
                    .aliveEnemiesOf(wp)
            ) {
                enemy.getCooldownManager().removeCooldown(BlightedScorch.class, false);
                enemy.getCooldownManager().addCooldown(new RegularCooldown<>(
                        name,
                        "BLI",
                        BlightedScorch.class,
                        new BlightedScorch(),
                        wp,
                        CooldownTypes.DEBUFF,
                        cooldownManager -> {
                        },
                        4 * 20,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed2) -> {
                            if (ticksLeft % 20 == 0) {
                                float healthDamage = enemy.getMaxHealth() * 0.05f;
                                healthDamage = DamageCheck.clamp(healthDamage);
                                enemy.addInstance(InstanceBuilder
                                        .damage()
                                        .ability(this)
                                        .source(wp)
                                        .value(healthDamage)
                                );
                            }
                        })
                ));
            }
            return true;
        }

    }

}
