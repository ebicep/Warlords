package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.warlords.GameRunnable;

import java.util.UUID;

public class LegendaryGradient extends AbstractLegendaryWeapon {

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
        return "Perpetually regenerate 7% of your health every 5 seconds.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        new GameRunnable(player.getGame()) {

            @Override
            public void run() {
                if (player.isDead()) {
                    return;
                }
                float healValue = player.getMaxHealth() * .07f;
                player.addHealingInstance(player, "Gradient", healValue, healValue, 0, 100, false, false);
            }
        }.runTaskTimer(0, 5 * 20);
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.GRADIENT;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
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
    protected float getEnergyPerSecondBonusValue() {
        return 3;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 3;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 20;
    }
}
