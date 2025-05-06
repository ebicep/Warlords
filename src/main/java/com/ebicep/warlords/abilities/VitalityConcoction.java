package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.VitalityConcoctionBranch;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class VitalityConcoction extends AbstractAbility implements PurpleAbilityIcon, Duration, AbilityStats<VitalityConcoction, VitalityConcoction.VitalityConcoctionStats> {

    private final VitalityConcoctionStats stats = new VitalityConcoctionStats();
    private int tickDuration = 15;
    private int damageResistance = 80;
    private int speedBoost = 150;

    public VitalityConcoction() {
        super("Vitality Concoction", 12, 20);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Consume a powerful concoction, granting yourself an additional ")
                .percent(speedBoost, NamedTextColor.WHITE)
                .text(" movement speed, ")
                .percent(damageResistance, AbilityDescriptionBuilder.COLOR_BROWN)
                .text(" damage reduction, and an immunity to de-buffs for ")
                .durationTicks(tickDuration)
                .text(".")
                .emptyLine()
                .text("Vitality Concoction has reduced effectiveness when holding a flag.")
                .build();

    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 0.1f);
        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_BLAZE_DEATH, 2, 0.7f);

        wp.setFlagPickCooldown(1);

        new FallingBlockWaveEffect(wp.getLocation(), 4, 1, Material.BIRCH_SAPLING).play();

        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        if (pveMasterUpgrade2) {
            wp.doOnStaticAbility(ImpalingStrike.class, impalingStrike -> {
                modifiers.add(impalingStrike.getEnergyCost().addMultiplicativeModifierAdd("Concoction Party", -.75f));
            });
        }

        wp.addSpeedModifier(wp, name, wp.hasFlag() ? 40 : speedBoost, tickDuration, true);
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(
                name,
                "STIM",
                VitalityConcoction.class,
                new VitalityConcoction(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {

                },
                cooldownManager -> {
                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                },
                tickDuration
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * convertToDivisionDecimal(damageResistance);
            }

            @Override
            protected Listener getListener() {
                return CooldownManager.getDefaultDebuffImmunityListener(wp);
            }

        });

        if (pveMasterUpgrade) {
            for (WarlordsNPC we : PlayerFilterGeneric
                    .entitiesAround(wp, 5, 5, 5)
                    .aliveTeammatesOfExcludingSelf(wp)
                    .warlordsNPCs()
            ) {
                we.addInstance(InstanceBuilder
                        .healing()
                        .ability(this)
                        .source(wp)
                        .min(1045)
                        .max(1425)
                );
            }
        }

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new VitalityConcoctionBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public VitalityConcoctionStats getAbilityStats() {
        return stats;
    }

    public static class VitalityConcoctionStats extends AbstractAbilityStats<VitalityConcoction, VitalityConcoctionStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public VitalityConcoctionStats merge(VitalityConcoctionStats other, int multiplier) {
            VitalityConcoctionStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<VitalityConcoctionStats> getClazz() {
            return VitalityConcoctionStats.class;
        }

        @Override
        public VitalityConcoctionStats create() {
            return new VitalityConcoctionStats();
        }
    }
}