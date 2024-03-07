package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.Overheal;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.DrainingMiasmaBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class DrainingMiasma extends AbstractAbility implements OrangeAbilityIcon, Duration {

    public int playersHit = 0;
    protected int numberOfLeechProcd = 0;
    private int maxHealthDamage = 4;
    private int tickDuration = 100;
    private int leechDuration = 5;
    private int radius = 8;
    private float leechSelfAmount = 25;
    private float leechAllyAmount = 15;

    public DrainingMiasma() {
        super("Draining Miasma", 50, 50, 50, 40);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Summon a toxin-filled cloud around you, poisoning all enemies inside the area. Poisoned enemies take ")
                               .append(Component.text("50", NamedTextColor.RED))
                               .append(Component.text(" + "))
                               .append(Component.text(maxHealthDamage + "%", NamedTextColor.RED))
                               .append(Component.text(" of their max health as damage per second, for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. Enemies poisoned by your Draining Miasma are slowed by "))
                               .append(Component.text("25%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text("3", NamedTextColor.GOLD))
                               .append(Component.text(" seconds on cast. Has a radius of "))
                               .append(Component.text(radius, NamedTextColor.GOLD))
                               .append(Component.text(" blocks."))
                               .append(Component.text("\n\nEach enemy hit will be afflicted with "))
                               .append(Component.text("LEECH", NamedTextColor.GREEN))
                               .append(Component.text(" for "))
                               .append(Component.text(leechDuration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {


        Utils.playGlobalSound(wp.getLocation(), "rogue.drainingmiasma.activation", 2, 1.7f);
        Utils.playGlobalSound(wp.getLocation(), "shaman.earthlivingweapon.activation", 2, 0.65f);

        EffectUtils.playSphereAnimation(wp.getLocation(), 6, Particle.SLIME, 1);
        EffectUtils.playFirework(
                wp.getLocation(),
                FireworkEffect.builder()
                              .withColor(Color.LIME)
                              .with(FireworkEffect.Type.BALL_LARGE)
                              .build()
        );

        if (pveMasterUpgrade) {
            Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1);
            EffectUtils.playSphereAnimation(wp.getLocation(), radius, Particle.SLIME, 1);
            EffectUtils.playFirework(
                    wp.getLocation(),
                    FireworkEffect.builder()
                                  .withColor(Color.WHITE)
                                  .with(FireworkEffect.Type.BALL_LARGE)
                                  .build()
            );
        }

        DrainingMiasma tempDrainingMiasma = new DrainingMiasma();
        for (WarlordsEntity miasmaTarget : PlayerFilter
                .entitiesAround(wp, getRadius(), getRadius(), getRadius())
                .isAlive()
        ) {
            playersHit++;
            if (miasmaTarget.isEnemy(wp)) {
                Runnable cancelSlowness = miasmaTarget.addSpeedModifier(wp, "Draining Miasma Slow", -25, 3 * 20, "BASE");
                miasmaTarget.getCooldownManager().removeCooldown(DrainingMiasma.class, false);
                miasmaTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
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
                        tickDuration,
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 20 != 0) {
                                return;
                            }
                            Utils.playGlobalSound(miasmaTarget.getLocation(), Sound.BLOCK_SNOW_BREAK, 2, 0.4f);

                            for (int i = 0; i < 3; i++) {
                                EffectUtils.displayParticle(
                                        Particle.REDSTONE,
                                        miasmaTarget.getLocation().clone().add(
                                                (Math.random() * 2) - 1,
                                                1.2 + (Math.random() * 2) - 1,
                                                (Math.random() * 2) - 1
                                        ),
                                        1,
                                        0,
                                        0,
                                        0,
                                        0,
                                        new Particle.DustOptions(Color.fromRGB(30, 200, 30), 1)
                                );

                            }

                            float healthDamage = miasmaTarget.getMaxHealth() * maxHealthDamage / 100f;
                            healthDamage = DamageCheck.clamp(healthDamage);
                            miasmaTarget.addDamageInstance(
                                    wp,
                                    name,
                                    minDamageHeal.getCalculatedValue() + healthDamage,
                                    maxDamageHeal.getCalculatedValue() + healthDamage,
                                    0,
                                    100,
                                    EnumSet.of(InstanceFlags.DOT)
                            );
                        })
                ) {
                    @Override
                    public TextColor customActionBarColor() {
                        return NamedTextColor.RED;
                    }
                });

                if (pveMasterUpgrade) {
                    miasmaTarget.getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "Liquidizing Miasma",
                            "LIQ",
                            DrainingMiasma.class,
                            new DrainingMiasma(),
                            wp,
                            CooldownTypes.DEBUFF,
                            cooldownManager -> {
                                new FallingBlockWaveEffect(miasmaTarget.getLocation(), 3, 1, Material.BIRCH_SAPLING).play();
                                for (WarlordsEntity target : PlayerFilter
                                        .entitiesAround(miasmaTarget, 6, 6, 6)
                                        .aliveEnemiesOf(wp)
                                ) {
                                    float healthDamage = miasmaTarget.getMaxHealth() * 0.01f;
                                    healthDamage = DamageCheck.clamp(healthDamage);
                                    target.addDamageInstance(
                                            wp,
                                            name,
                                            minDamageHeal.getCalculatedValue() + healthDamage,
                                            maxDamageHeal.getCalculatedValue() + healthDamage,
                                            0,
                                            100
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
            } else {
                if (pveMasterUpgrade2) {
                    miasmaTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "Debuff Immunity",
                            "MIAS",
                            DrainingMiasma.class,
                            tempDrainingMiasma,
                            wp,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            tickDuration,
                            Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                                if (ticksElapsed % 20 != 0) {
                                    return;
                                }
                                float healing = miasmaTarget.getMaxHealth() * .02f;
                                miasmaTarget.addHealingInstance(
                                        wp,
                                        "Toxic Immunity",
                                        healing,
                                        healing,
                                        0,
                                        100,
                                        EnumSet.of(InstanceFlags.CAN_OVERHEAL_OTHERS)
                                );
                                Overheal.giveOverHeal(wp, miasmaTarget);
                            })
                    ));
                }
            }
        }

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new DrainingMiasmaBranch(abilityTree, this);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getLeechDuration() {
        return leechDuration;
    }

    public void setLeechDuration(int leechDuration) {
        this.leechDuration = leechDuration;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
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
