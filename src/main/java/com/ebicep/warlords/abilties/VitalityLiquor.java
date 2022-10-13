package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class VitalityLiquor extends AbstractAbility {

    public int numberOfAdditionalWaves = 0;

    private int vitalityRange = 8;
    private final int duration = 3;
    private int energyPerSecond = 15;
    private float minWaveHealing = 268;
    private float maxWaveHealing = 324;

    public VitalityLiquor() {
        super("Vitality Liquor", 359, 485, 14, 30, 25, 175);
    }

    @Override
    public void updateDescription(Player player) {
        description = "Discharge a shockwave of special potions around you, healing allies in the range for" +
                formatRangeHealing(minDamageHeal, maxDamageHeal) + "health." +
                "\n\nEach enemy afflicted with your §aLEECH §7effect within the range will cause the enemy to discharge an additional shockwave of vitality" +
                "that heals §e2 §7nearby allies for" + formatRangeHealing(minWaveHealing, maxWaveHealing) +
                "health and increase their energy regeneration by §e" + energyPerSecond + " §7for §6" + duration + " §7seconds.";
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
        Utils.playGlobalSound(player.getLocation(), Sound.GLASS, 2, 0.1f);
        Utils.playGlobalSound(player.getLocation(), Sound.BLAZE_DEATH, 2, 0.7f);
        new FallingBlockWaveEffect(player.getLocation(), 7, 1, Material.SAPLING, (byte) 2).play();

        VitalityLiquor tempVitalityLiquor = new VitalityLiquor();
        wp.addHealingInstance(
                wp,
                name,
                minDamageHeal,
                maxDamageHeal,
                critChance,
                critMultiplier,
                false,
                false
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
                    critMultiplier,
                    false,
                    false
            );
        }

        for (WarlordsEntity enemyTarget : PlayerFilter
                .entitiesAround(player, vitalityRange, vitalityRange, vitalityRange)
                .aliveEnemiesOf(wp)
        ) {
            new CooldownFilter<>(enemyTarget, RegularCooldown.class)
                    .filterCooldownClass(ImpalingStrike.class)
                    .filterCooldownFrom(wp)
                    .findAny()
                    .ifPresent(regularCooldown -> {
                        Utils.playGlobalSound(enemyTarget.getLocation(), Sound.GLASS, 2, 0.6f);
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
                                            critMultiplier,
                                            false,
                                            false
                                    );
                                    allyTarget.getCooldownManager().removeCooldown(VitalityLiquor.class);
                                    allyTarget.getCooldownManager().addCooldown(new RegularCooldown<VitalityLiquor>(
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
}
