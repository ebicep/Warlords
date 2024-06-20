package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.CanReduceCooldowns;
import com.ebicep.warlords.abilities.internal.Duration;
import com.ebicep.warlords.abilities.internal.HitBox;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.achievements.types.ChallengeAchievements;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.crusader.InspiringPresenceBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class InspiringPresence extends AbstractAbility implements OrangeAbilityIcon, Duration, HitBox, CanReduceCooldowns {

    public int playersHit = 0;

    protected List<WarlordsEntity> playersAffected = new ArrayList<>();
    protected double energyGivenFromStrikeAndPresence = 0;

    private int speedBuff = 30;
    private FloatModifiable radius = new FloatModifiable(10);
    private int tickDuration = 240;
    private int energyPerSecond = 10;

    public InspiringPresence() {
        super("Inspiring Presence", 60f + 10.47f, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Your presence on the battlefield inspires your allies, increasing their energy regeneration by ")
                               .append(Component.text(energyPerSecond, NamedTextColor.YELLOW))
                               .append(Component.text(" per second and their movement by "))
                               .append(Component.text(speedBuff + "%", NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds.\n\nHas a maximum range of "))
                               .append(Component.text(format(radius.getCalculatedValue()), NamedTextColor.YELLOW))
                               .append(Component.text(" blocks."));
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
        Utils.playGlobalSound(wp.getLocation(), "paladin.inspiringpresence.activation", 2, 1);

        Runnable cancelSpeed = wp.addSpeedModifier(wp, "Inspiring Presence", speedBuff, tickDuration, "BASE");

        float rad = radius.getCalculatedValue();
        List<WarlordsEntity> teammatesNear = PlayerFilter
                .entitiesAround(wp, rad, rad, rad)
                .aliveTeammatesOfExcludingSelf(wp)
                .toList();

        InspiringPresence tempPresence = new InspiringPresence();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "PRES",
                InspiringPresence.class,
                tempPresence,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    cancelSpeed.run();
                    ChallengeAchievements.checkForAchievement(wp, ChallengeAchievements.PORTABLE_ENERGIZER);
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 4 == 0) {
                        Location location = wp.getLocation();
                        location.add(0, 1.5, 0);
                        EffectUtils.displayParticle(Particle.SMOKE_NORMAL, location, 1, 0.3, 0.3, 0.3, 0.02);
                        EffectUtils.displayParticle(Particle.SPELL, location, 2, 0.3, 0.3, 0.3, 0.5);
                    }
                })
        ) {
            @Override
            public float addEnergyGainPerTick(float energyGainPerTick) {
                tempPresence.addEnergyGivenFromStrikeAndPresence(energyPerSecond / 20d);
                return energyGainPerTick + energyPerSecond / 20f;
            }

            @Override
            public void onDamageFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (pveMasterUpgrade2) {
                    wp.addEnergy(wp, "Resilient Presence", 15);
                    teammatesNear.forEach(teammate -> teammate.addEnergy(teammate, "Resilient Presence", 15));
                }
            }
        });

        if (pveMasterUpgrade) {
            resetCooldowns(wp);
        }

        for (WarlordsEntity presenceTarget : teammatesNear) {
            playersHit++;
            tempPresence.getPlayersAffected().add(presenceTarget);
            if (pveMasterUpgrade) {
                resetCooldowns(presenceTarget);
            }

            wp.sendMessage(WarlordsEntity.GIVE_ARROW_GREEN
                    .append(Component.text(" Your ", NamedTextColor.GRAY))
                    .append(Component.text("Inspiring Presence", NamedTextColor.YELLOW))
                    .append(Component.text(" inspired " + presenceTarget.getName() + "!", NamedTextColor.GRAY))
            );
            presenceTarget.sendMessage(WarlordsEntity.RECEIVE_ARROW_GREEN
                    .append(Component.text(" " + wp.getName() + "'s ", NamedTextColor.GRAY))
                    .append(Component.text("Inspiring Presence", NamedTextColor.YELLOW))
                    .append(Component.text(" inspired you!", NamedTextColor.GRAY))
            );

            Runnable cancelAllySpeed = presenceTarget.addSpeedModifier(wp, "Inspiring Presence", speedBuff, tickDuration, "BASE");
            List<FloatModifiable.FloatModifier> modifiers;
            if (pveMasterUpgrade) {
                modifiers = presenceTarget.getAbilities()
                                          .stream()
                                          .map(ability -> ability.getCooldown().addMultiplicativeModifierMult(name + " Master", 0.8f))
                                          .toList();
            } else {
                modifiers = Collections.emptyList();
            }
            presenceTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                    name,
                    "PRES",
                    InspiringPresence.class,
                    tempPresence,
                    wp,
                    CooldownTypes.ABILITY,
                    cooldownManager -> {
                    },
                    cooldownManager -> {
                        cancelAllySpeed.run();
                        modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                    },
                    tickDuration,
                    Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    })
            ) {
                @Override
                public float addEnergyGainPerTick(float energyGainPerTick) {
                    tempPresence.addEnergyGivenFromStrikeAndPresence(energyPerSecond / 20d);
                    return energyGainPerTick + energyPerSecond / 20f;
                }
            });
        }

        return true;
    }

    public void addEnergyGivenFromStrikeAndPresence(double energyGivenFromStrikeAndPresence) {
        this.energyGivenFromStrikeAndPresence += energyGivenFromStrikeAndPresence;
    }

    private void resetCooldowns(WarlordsEntity we) {
        for (AbstractAbility ability : we.getAbilities()) {
            if (ability.getClass() == InspiringPresence.class) {
                continue;
            }
            ability.subtractCurrentCooldown(15);
        }
    }

    public List<WarlordsEntity> getPlayersAffected() {
        return playersAffected;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new InspiringPresenceBranch(abilityTree, this);
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade;
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public double getEnergyGivenFromStrikeAndPresence() {
        return energyGivenFromStrikeAndPresence;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }
}