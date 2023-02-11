package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.warlords.GameRunnable;

import java.util.UUID;

public class LegendaryGradient extends AbstractLegendaryWeapon {

    private static final int REGEN_TICK_INTERVAL = 100;
    private static final float REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE = 8;

    public LegendaryGradient() {
    }

    public LegendaryGradient(UUID uuid) {
        super(uuid);
    }

    public LegendaryGradient(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "Perpetually regenerate 7% of your health every " + formatTitleUpgrade((REGEN_TICK_INTERVAL - REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE * getTitleLevel()) / 20) + " seconds.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        new GameRunnable(player.getGame()) {

            int ticksElapsed = 0;

            @Override
            public void run() {
                if (player.isDead()) {
                    ticksElapsed = 0;
                    return;
                }
                ticksElapsed++;
                if (ticksElapsed % (REGEN_TICK_INTERVAL - REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE * getTitleLevel()) == 0) {
                    float healValue = player.getMaxHealth() * .07f;
                    player.addHealingInstance(player, "Gradient", healValue, healValue, 0, 100, false, false);
                }
            }
        }.runTaskTimer(0, 0);
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
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 160;
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
}
