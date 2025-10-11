package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Transient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LegendaryGradient extends AbstractLegendaryWeapon implements PassiveCounter {

    private static final int REGEN_TICK_INTERVAL = 40;
    private static final float REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE = 8;
    private static final float REGEN_PER_INTERVAL = 2f;
    private static final float REGEN_PER_INTERVAL_UPGRADE = 0.75f;
    private static final float HEALTH_THRESHOLD = 10;
    private static final float HEALTH_THRESHOLD_UPGRADE = 5;

    @Transient
    private int tickCountdown = 0;

    public LegendaryGradient() {
    }

    public LegendaryGradient(UUID uuid) {
        super(uuid);
    }

    public LegendaryGradient(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        int interval = REGEN_TICK_INTERVAL;
        this.tickCountdown = interval;
        new GameRunnable(player.getGame()) {

            @Override
            public void run() {
                if (player.isDead()) {
                    tickCountdown = interval;
                    return;
                }
                tickCountdown--;
                if (tickCountdown <= 0) {
                    tickCountdown = interval;
                    float healValue = player.getMaxHealth() * ((REGEN_PER_INTERVAL + REGEN_PER_INTERVAL_UPGRADE * getTitleLevel()) / 100f);
                    float lowHealthThreshold = player.getMaxHealth() * ((HEALTH_THRESHOLD + HEALTH_THRESHOLD_UPGRADE * getTitleLevel()) / 100f);
                    if (player.getCurrentHealth() < lowHealthThreshold) {
                        healValue *= 2;
                        tickCountdown = 20;
                    }

                    player.addInstance(InstanceBuilder
                            .healing()
                            .cause("Gradient")
                            .source(player)
                            .value(healValue)
                    );
                }
            }
        }.runTaskTimer(0, 1);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Perpetually regenerate ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(REGEN_PER_INTERVAL + REGEN_PER_INTERVAL_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" of your max health every 2 seconds. When you are below ", NamedTextColor.GRAY))
                        .append(formatTitleUpgrade((HEALTH_THRESHOLD + HEALTH_THRESHOLD_UPGRADE * getTitleLevel()), "%"))
                        .append(Component.text(" health, the healing will be doubled and interval will be every second.", NamedTextColor.GRAY));
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.GRADIENT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getHealthBonusValue() {
        return 700;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 3;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 20;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 160;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(HEALTH_THRESHOLD + HEALTH_THRESHOLD_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(HEALTH_THRESHOLD + HEALTH_THRESHOLD_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(REGEN_PER_INTERVAL + REGEN_PER_INTERVAL_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(REGEN_PER_INTERVAL + REGEN_PER_INTERVAL_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

    @Override
    public int getCounter() {
        return tickCountdown / 20;
    }
}
