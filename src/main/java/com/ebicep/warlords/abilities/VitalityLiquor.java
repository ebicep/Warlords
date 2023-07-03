package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.apothecary.VitalityLiquorBranch;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class VitalityLiquor extends AbstractAbility implements PurpleAbilityIcon {

    public int numberOfAdditionalWaves = 0;
    private int duration = 3;
    private int vitalityRange = 8;
    private int energyPerSecond = 15;
    private float minWaveHealing = 268;
    private float maxWaveHealing = 324;

    public VitalityLiquor() {
        super("Vitality Liquor", 359, 485, 14, 30, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Discharge a shockwave of special potions around you, healing allies in the range for ")
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" health.\n\nEach enemy afflicted with your "))
                               .append(Component.text("LEECH", NamedTextColor.GREEN))
                               .append(Component.text(" effect within the range will cause the enemy to discharge an additional shockwave of vitality that heals "))
                               .append(Component.text("2", NamedTextColor.YELLOW))
                               .append(Component.text(" nearby allies for "))
                               .append(formatRangeHealing(minWaveHealing, maxWaveHealing))
                               .append(Component.text(" health and increase their energy regeneration by "))
                               .append(Component.text(energyPerSecond, NamedTextColor.YELLOW))
                               .append(Component.text(" for "))
                               .append(Component.text(duration, NamedTextColor.GOLD))
                               .append(Component.text(" seconds."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Number of Additional Waves", "" + numberOfAdditionalWaves));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        wp.subtractEnergy(energyCost, false);
        Utils.playGlobalSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 0.1f);
        Utils.playGlobalSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 2, 0.7f);
        new FallingBlockWaveEffect(player.getLocation(), vitalityRange, 1, Material.BIRCH_SAPLING).play();

        VitalityLiquor tempVitalityLiquor = new VitalityLiquor();
        wp.addHealingInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier
        );

        for (WarlordsEntity acuTarget : PlayerFilter
                .entitiesAround(player, vitalityRange, vitalityRange, vitalityRange)
                .aliveTeammatesOfExcludingSelf(wp)
        ) {
            acuTarget.addHealingInstance(
                    wp,
                    name,
                    minDamageHeal,
                    maxDamageHeal,
                    critChance,
                    critMultiplier
            );
        }

        for (WarlordsEntity enemyTarget : PlayerFilter
                .entitiesAround(player, vitalityRange, vitalityRange, vitalityRange)
                .aliveEnemiesOf(wp)
        ) {
            if (pveMasterUpgrade) {
                enemyTarget.addSpeedModifier(wp, "Vitality Slowness", -30, 20 * 3);
            }
            new CooldownFilter<>(enemyTarget, RegularCooldown.class)
                    .filterCooldownClass(ImpalingStrike.class)
                    .filterCooldownFrom(wp)
                    .findAny()
                    .ifPresent(regularCooldown -> {
                        Utils.playGlobalSound(enemyTarget.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 0.6f);
                        FireWorkEffectPlayer.playFirework(enemyTarget.getLocation(), FireworkEffect.builder()
                                                                                                   .withColor(Color.ORANGE)
                                                                                                   .with(FireworkEffect.Type.STAR)
                                                                                                   .build());

                        new GameRunnable(wp.getGame()) {
                            @Override
                            public void run() {
                                for (WarlordsEntity allyTarget : PlayerFilter
                                        .entitiesAround(enemyTarget, 6, 6, 6)
                                        .aliveTeammatesOf(wp)
                                        .closestFirst(enemyTarget)
                                        .limit(2)
                                ) {
                                    numberOfAdditionalWaves++;
                                    allyTarget.addHealingInstance(
                                            wp,
                                            name,
                                            minWaveHealing,
                                            maxWaveHealing,
                                            critChance,
                                            critMultiplier
                                    );
                                    allyTarget.getCooldownManager().removeCooldown(VitalityLiquor.class, false);
                                    allyTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                                            "Vitality Liquor",
                                            "VITAL",
                                            VitalityLiquor.class,
                                            tempVitalityLiquor,
                                            wp,
                                            CooldownTypes.BUFF,
                                            cooldownManager -> {
                                            },
                                            duration * 20
                                    ) {
                                        @Override
                                        public float addEnergyGainPerTick(float energyGainPerTick) {
                                            return energyGainPerTick + energyPerSecond / 20f;
                                        }
                                    });
                                }
                            }
                        }.runTaskLater(5);
                    });
        }

        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new VitalityLiquorBranch(abilityTree, this);
    }

    public float getMinWaveHealing() {
        return minWaveHealing;
    }

    public void setMinWaveHealing(float minWaveHealing) {
        this.minWaveHealing = minWaveHealing;
    }

    public float getMaxWaveHealing() {
        return maxWaveHealing;
    }

    public void setMaxWaveHealing(float maxWaveHealing) {
        this.maxWaveHealing = maxWaveHealing;
    }

    public int getEnergyPerSecond() {
        return energyPerSecond;
    }

    public void setEnergyPerSecond(int energyPerSecond) {
        this.energyPerSecond = energyPerSecond;
    }

    public int getVitalityRange() {
        return vitalityRange;
    }

    public void setVitalityRange(int vitalityRange) {
        this.vitalityRange = vitalityRange;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
