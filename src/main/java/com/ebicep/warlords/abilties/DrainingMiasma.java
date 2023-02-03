package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrainingMiasma extends AbstractAbility {

    protected int numberOfLeechProcd = 0;

    public int playersHit = 0;

    private int maxHealthDamage = 4;
    private int duration = 5;
    private int leechDuration = 5;
    private int enemyHitRadius = 8;
    private float leechSelfAmount = 25;
    private float leechAllyAmount = 15;

    public DrainingMiasma() {
        super("Draining Miasma", 50, 50, 50, 40);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Summon a toxin-filled cloud around you, poisoning all enemies inside the area. Poisoned enemies take §c50 §7+ §c" + maxHealthDamage +
                "% §7of their max health as damage per second, for §6" + duration + " §7seconds. " +
                "Enemies poisoned by your Draining Miasma are slowed by §e25% §7for §63 §7seconds on cast." +
                "\n\nEach enemy hit will be afflicted with §aLEECH §7for §6" + leechDuration + " §7seconds.";
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);

        Utils.playGlobalSound(wp.getLocation(), "rogue.drainingmiasma.activation", 2, 1.7f);
        Utils.playGlobalSound(wp.getLocation(), "shaman.earthlivingweapon.activation", 2, 0.65f);

        EffectUtils.playSphereAnimation(wp.getLocation(), 6, ParticleEffect.SLIME, 1);

        FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                .withColor(Color.LIME)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());

        if (pveUpgrade) {
            Utils.playGlobalSound(wp.getLocation(), Sound.WITHER_SPAWN, 10, 1);
            EffectUtils.playSphereAnimation(wp.getLocation(), enemyHitRadius, ParticleEffect.SLIME, 1);
            FireWorkEffectPlayer.playFirework(wp.getLocation(), FireworkEffect.builder()
                    .withColor(Color.WHITE)
                    .with(FireworkEffect.Type.BALL_LARGE)
                    .build());
        }

        int hitCounter = 0;
        DrainingMiasma tempDrainingMiasma = new DrainingMiasma();
        for (WarlordsEntity miasmaTarget : PlayerFilter
                .entitiesAround(wp, getEnemyHitRadius(), getEnemyHitRadius(), getEnemyHitRadius())
                .aliveEnemiesOf(wp)
        ) {
            hitCounter++;
            Runnable cancelSlowness = miasmaTarget.addSpeedModifier(wp, "Draining Miasma Slow", -25, 3 * 20, "BASE");
            miasmaTarget.getCooldownManager().removeCooldown(DrainingMiasma.class, false);
            miasmaTarget.getCooldownManager().addRegularCooldown(
                    name,
                    "MIAS",
                    DrainingMiasma.class,
                    tempDrainingMiasma,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        cancelSlowness.run();
                        if (tempDrainingMiasma.numberOfLeechProcd >= 150) {
                            ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.LIFELEECHER);
                        }
                    },
                    duration * 20,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (ticksElapsed % 20 == 0) {
                            Utils.playGlobalSound(miasmaTarget.getLocation(), Sound.DIG_SNOW, 2, 0.4f);

                            for (int i = 0; i < 3; i++) {
                                ParticleEffect.REDSTONE.display(
                                        new ParticleEffect.OrdinaryColor(30, 200, 30),
                                        miasmaTarget.getLocation().clone().add(
                                                (Math.random() * 2) - 1,
                                                1.2 + (Math.random() * 2) - 1,
                                                (Math.random() * 2) - 1
                                        ),
                                        500
                                );
                            }

                            float healthDamage = miasmaTarget.getMaxHealth() * maxHealthDamage / 100f;
                            if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                                healthDamage = DamageCheck.MINIMUM_DAMAGE;
                            }
                            if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                                healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                            }
                            // 4% current health damage.
                            miasmaTarget.addDamageInstance(
                                    wp,
                                    name,
                                    minDamageHeal + healthDamage,
                                    maxDamageHeal + healthDamage,
                                    0,
                                    100,
                                    false
                            );
                        }
                    })
            );
            playersHit += hitCounter;

            if (pveUpgrade) {
                miasmaTarget.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        "Liquidizing Miasma",
                        "LIQ",
                        DrainingMiasma.class,
                        new DrainingMiasma(),
                        wp,
                        CooldownTypes.ABILITY,
                        cooldownManager -> {
                            new FallingBlockWaveEffect(miasmaTarget.getLocation(), 3, 1, Material.SAPLING, (byte) 2).play();
                            for (WarlordsEntity target : PlayerFilter
                                    .entitiesAround(miasmaTarget, 6, 6, 6)
                                    .aliveEnemiesOf(wp)
                            ) {
                                float healthDamage = miasmaTarget.getMaxHealth() * 0.01f;
                                if (healthDamage < DamageCheck.MINIMUM_DAMAGE) {
                                    healthDamage = DamageCheck.MINIMUM_DAMAGE;
                                }
                                if (healthDamage > DamageCheck.MAXIMUM_DAMAGE) {
                                    healthDamage = DamageCheck.MAXIMUM_DAMAGE;
                                }
                                target.addDamageInstance(
                                        wp,
                                        name,
                                        minDamageHeal + healthDamage,
                                        maxDamageHeal + healthDamage,
                                        0,
                                        100,
                                        false
                                );
                            }
                        },
                        true
                ) {
                    @Override
                    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        return currentDamageValue * 0.75f;
                    }
                });
            }

            ImpalingStrike.giveLeechCooldown(
                    wp,
                    miasmaTarget,
                    leechDuration,
                    leechSelfAmount / 100f,
                    leechAllyAmount / 100f,
                    warlordsDamageHealingFinalEvent -> {
                        tempDrainingMiasma.numberOfLeechProcd++;
                    }
            );

        }

        return true;
    }

    public int getEnemyHitRadius() {
        return enemyHitRadius;
    }

    public void setEnemyHitRadius(int enemyHitRadius) {
        this.enemyHitRadius = enemyHitRadius;
    }

    public int getLeechDuration() {
        return leechDuration;
    }

    public void setLeechDuration(int leechDuration) {
        this.leechDuration = leechDuration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


    public int getMaxHealthDamage() {
        return maxHealthDamage;
    }

    public void setMaxHealthDamage(int maxHealthDamage) {
        this.maxHealthDamage = maxHealthDamage;
    }

    public float getLeechSelfAmount() {
        return leechSelfAmount;
    }

    public void setLeechSelfAmount(float leechSelfAmount) {
        this.leechSelfAmount = leechSelfAmount;
    }

    public float getLeechAllyAmount() {
        return leechAllyAmount;
    }

    public void setLeechAllyAmount(float leechAllyAmount) {
        this.leechAllyAmount = leechAllyAmount;
    }
}
