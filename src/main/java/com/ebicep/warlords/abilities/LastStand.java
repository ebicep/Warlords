package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.defender.LastStandBranch;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;


public class LastStand extends AbstractAbility implements OrangeAbilityIcon, Duration {

    public int playersLastStanded = 0;

    protected float amountPrevented = 0;

    private int radius = 7;
    private int selfTickDuration = 240;
    private int allyTickDuration = 120;
    private int selfDamageReductionPercent = 35;
    private int teammateDamageReductionPercent = 35;

    public LastStand() {
        super("Last Stand", 0, 0, 56.38f, 40);
    }

    public LastStand(float cooldown, float startCooldown) {
        super("Last Stand", 0, 0, cooldown, 40, startCooldown);
    }

    public LastStand(int selfDamageReductionPercent, int teammateDamageReductionPercent) {
        super("Last Stand", 0, 0, 56.38f, 40);
        this.selfDamageReductionPercent = selfDamageReductionPercent;
        this.teammateDamageReductionPercent = teammateDamageReductionPercent;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Enter a defensive stance, reducing all damage you take by ")
                               .append(Component.text(selfDamageReductionPercent + "%", NamedTextColor.RED))
                               .append(Component.text(" for "))
                               .append(Component.text(format(selfTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds and also reduces all damage nearby allies take by "))
                               .append(Component.text(teammateDamageReductionPercent + "%", NamedTextColor.RED))
                               .append(Component.text(" for "))
                               .append(Component.text(format(allyTickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. You are healed for the amount of damage prevented on allies." +
                                       (inPve ? "Additionally, constantly take aggro of nearby mobs." : "")))
                               .append(Component.text("\n\nHas a maximum range of "))
                               .append(Component.text(radius, NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Last Standed", "" + playersLastStanded));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {

        Utils.playGlobalSound(wp.getLocation(), "warrior.laststand.activation", 2, 1);

        LastStand tempLastStand = new LastStand(selfDamageReductionPercent, teammateDamageReductionPercent);
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        if (pveMasterUpgrade2) {
            for (SeismicWaveDefender ability : wp.getAbilitiesMatching(SeismicWaveDefender.class)) {
                modifiers.add(ability.getCooldown().addMultiplicativeModifierAdd("Enduring Defense", -.5f));
                modifiers.add(ability.getEnergyCost().addOverridingModifier("Enduring Defense", 30f));
            }
            for (GroundSlamDefender ability : wp.getAbilitiesMatching(GroundSlamDefender.class)) {
                modifiers.add(ability.getCooldown().addMultiplicativeModifierAdd("Enduring Defense", -.5f));
            }
        }
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "LAST",
                LastStand.class,
                tempLastStand,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.HARDENED_SCALES);
                    if (pveMasterUpgrade2) {
                        modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                    }
                },
                selfTickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if ((pveMasterUpgrade || pveMasterUpgrade2) && ticksLeft % 15 == 0) {
                        for (WarlordsEntity we : PlayerFilter
                                .entitiesAround(wp, radius, radius, radius)
                                .aliveEnemiesOf(wp)
                                .closestFirst(wp)
                        ) {
                            if (we instanceof WarlordsNPC) {
                                ((WarlordsNPC) we).getMob().setTarget(wp);
                            }
                        }
                    }
                })
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                float afterValue = currentDamageValue * convertToDivisionDecimal(selfDamageReductionPercent);
                tempLastStand.addAmountPrevented(currentDamageValue - afterValue);
                return afterValue;
            }

            @Override
            public void multiplyKB(Vector currentVector) {
                if (pveMasterUpgrade) {
                    currentVector.multiply(0.5);
                }
            }
        });

        for (WarlordsEntity standTarget : PlayerFilter
                .entitiesAround(wp, radius, radius, radius)
                .aliveTeammatesOf(wp)
                .excluding(wp)
        ) {
            playersLastStanded++;

            EffectUtils.playParticleLinkAnimation(wp.getLocation(), standTarget.getLocation(), Particle.VILLAGER_HAPPY);
            standTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "LAST",
                    LastStand.class,
                    tempLastStand,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    allyTickDuration
            ) {
                @Override
                public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * convertToDivisionDecimal(teammateDamageReductionPercent);
                }

                @Override
                public void onShieldFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    tempLastStand.addAmountPrevented(currentDamageValue);
                    wp.addAbsorbed(currentDamageValue);
                    wp.addHealingInstance(
                            wp,
                            name,
                            currentDamageValue,
                            currentDamageValue,
                            isCrit ? 100 : 0,
                            100,
                            EnumSet.of(InstanceFlags.LAST_STAND_FROM_SHIELD, InstanceFlags.IGNORE_CRIT_MODIFIERS)
                    );
                }

                @Override
                public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                    tempLastStand.addAmountPrevented(currentDamageValue);
                    wp.addAbsorbed(currentDamageValue);
                    wp.addHealingInstance(
                            wp,
                            name,
                            currentDamageValue,
                            currentDamageValue,
                            isCrit ? 100 : 0,
                            100,
                            EnumSet.of(InstanceFlags.IGNORE_CRIT_MODIFIERS)
                    );
                }
            });

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your Last Stand is now protecting ", NamedTextColor.GRAY))
                    .append(Component.text(standTarget.getName(), NamedTextColor.YELLOW))
                    .append(Component.text("!", NamedTextColor.GRAY))
            );

            standTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                    .append(Component.text("Last Stand", NamedTextColor.YELLOW))
                    .append(Component.text(" is now protecting you for ", NamedTextColor.GRAY))
                    .append(Component.text(format(allyTickDuration / 20f), NamedTextColor.GOLD))
                    .append(Component.text(" seconds!", NamedTextColor.GRAY))
            );
        }

        Location loc = wp.getEyeLocation();
        loc.setPitch(0);
        loc.setYaw(0);
        Matrix4d matrix = new Matrix4d();
        int distance = radius / 2;
        for (int i = 0; i < distance; i++) {
            loc.setYaw(loc.getYaw() + 360F / distance);
            matrix.updateFromLocation(loc);
            for (int c = 0; c < 20; c++) {
                double angle = c / 20D * Math.PI * 2;
                double width = 1.2;

                loc.getWorld().spawnParticle(
                        Particle.FLAME,
                        matrix.translateVector(wp.getWorld(), distance, Math.sin(angle) * width, Math.cos(angle) * width),
                        1,
                        0,
                        0,
                        0,
                        0,
                        null,
                        true
                );
            }

            for (int c = 0; c < 10; c++) {
                double width = 0.6;
                double angle = c / 10D * Math.PI * 2;

                loc.getWorld().spawnParticle(
                        Particle.REDSTONE,
                        matrix.translateVector(wp.getWorld(), distance, Math.sin(angle) * width, Math.cos(angle) * width),
                        1,
                        0,
                        0,
                        0,
                        0,
                        new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1),
                        true
                );
            }
        }

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LastStandBranch(abilityTree, this);
    }

    public void addAmountPrevented(float amountPrevented) {
        this.amountPrevented += amountPrevented;
    }

    public float getSelfDamageReduction() {
        return selfDamageReductionPercent;
    }

    public void setSelfDamageReductionPercent(int selfDamageReductionPercent) {
        this.selfDamageReductionPercent = selfDamageReductionPercent;
    }

    public float getTeammateDamageReduction() {
        return teammateDamageReductionPercent;
    }

    public void setTeammateDamageReductionPercent(int teammateDamageReductionPercent) {
        this.teammateDamageReductionPercent = teammateDamageReductionPercent;
    }

    public float getAmountPrevented() {
        return amountPrevented;
    }

    @Override
    public int getTickDuration() {
        return selfTickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.selfTickDuration = tickDuration;
    }

    @Override
    public void multiplyTickDuration(float multiplier) {
        this.selfTickDuration *= multiplier;
        this.allyTickDuration *= multiplier;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
