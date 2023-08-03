package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.luminary.EnergySeerBranchLuminary;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EnergySeerLuminary extends AbstractAbility implements PurpleAbilityIcon {

    // TODO:  reintegrate with abstract energy seer

    private float conversionAmount = 4;
    private int tickDuration = 100;
    private int energyRestore = 40;
    private int bonusDuration = 100;
    private float healingIncrease = 20;

    public EnergySeerLuminary() {
        super("Energy Seer", 0, 0, 26, 20, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Gain ")
                               .append(Component.text("30%", NamedTextColor.YELLOW))
                               .append(Component.text(" speed and receive energy equal to "))
                               .append(Component.text(format(conversionAmount) + "%", NamedTextColor.GREEN))
                               .append(Component.text(" of the healing done in the next "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds. If you healed for 4 instances, restore energy "))
                               .append(Component.text(energyRestore, NamedTextColor.YELLOW))
                               .append(Component.text(" and increase your healing by "))
                               .append(Component.text(format(healingIncrease) + "%", NamedTextColor.GREEN))
                               .append(Component.text(" for "))
                               .append(Component.text(format(bonusDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds after Energy Seer ends."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(wp.getLocation(), "arcanist.energyseer.activation", 2, 0.9f);
        for (int i = 0; i < 20; i++) {
            EffectUtils.displayParticle(Particle.SOUL_FIRE_FLAME, wp.getLocation(), 3, 0.3, 0.1, 0.3, 0.1);
        }
        wp.addSpeedModifier(wp, name, 30, tickDuration);
        AtomicInteger timesHealed = new AtomicInteger();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "SEER",
                EnergySeerLuminary.class,
                new EnergySeerLuminary(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                    if (timesHealed.get() >= 4 && wp.isAlive()) {
                        wp.addEnergy(wp, name, energyRestore);
                        giveBonusCooldown(wp);
                    }
                },
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 4 == 0) {
                                Location location = wp.getLocation();
                                location.add(0, 1.2, 0);
                                EffectUtils.displayParticle(
                                        Particle.SOUL_FIRE_FLAME,
                                        location,
                                        2,
                                        0.2,
                                        0,
                                        0.2,
                                        0.1
                                );
                            }
                        }
                )
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {
                    @EventHandler
                    public void onHealing(WarlordsDamageHealingFinalEvent event) {
                        if (event.isDamageInstance()) {
                            return;
                        }
                        float value = event.getValue();
                        if (value <= 0) {
                            return;
                        }
                        float energyValue = Math.min(value * convertToPercent(conversionAmount), 20);
                        if (energyValue <= 0) {
                            return;
                        }
                        wp.addEnergy(wp, name, energyValue);
                        timesHealed.getAndIncrement();
                    }
                };
            }
        });

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EnergySeerBranchLuminary(abilityTree, this);
    }

    private void giveBonusCooldown(WarlordsEntity we) {
        new RegularCooldown<>(
                name,
                "SEER",
                EnergySeerLuminary.class,
                new EnergySeerLuminary(),
                we,
                CooldownTypes.BUFF,
                cooldownManager -> {

                },
                bonusDuration
        ) {
            @Override
            public float doBeforeHealFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                return healingIncrease * convertToMultiplicationDecimal(healingIncrease);
            }
        };
    }

    public int getTickDuration() {
        return tickDuration;
    }

    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public float getHealingIncrease() {
        return healingIncrease;
    }

    public void setHealingIncrease(float healingIncrease) {
        this.healingIncrease = healingIncrease;
    }

    public float getConversionAmount() {
        return conversionAmount;
    }

    public void setConversionAmount(float conversionAmount) {
        this.conversionAmount = conversionAmount;
    }

    public int getEnergyRestore() {
        return energyRestore;
    }

    public void setEnergyRestore(int energyRestore) {
        this.energyRestore = energyRestore;
    }
}
