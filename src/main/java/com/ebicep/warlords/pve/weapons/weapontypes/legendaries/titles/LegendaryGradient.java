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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LegendaryGradient extends AbstractLegendaryWeapon implements PassiveCounter {

    private static final int REGEN_TICK_INTERVAL = 100;
    private static final float REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE = 8;

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
        int interval = (int) (REGEN_TICK_INTERVAL - REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE * getTitleLevel());
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
                    float healValue = player.getMaxHealth() * .085f;
                    player.addInstance(InstanceBuilder
                            .healing()
                            .cause("Gradient")
                            .source(player)
                            .value(healValue)
                    );
                }
            }
        }.runTaskTimer(0, 0);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Perpetually regenerate 8.5% of your health every ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade((REGEN_TICK_INTERVAL - REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE * getTitleLevel()) / 20))
                        .append(Component.text(" seconds."));
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
        return Collections.singletonList(new Pair<>(
                formatTitleUpgrade((REGEN_TICK_INTERVAL - REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE * getTitleLevel()) / 20),
                formatTitleUpgrade((REGEN_TICK_INTERVAL - REGEN_TICK_INTERVAL_DECREASE_PER_UPGRADE * getTitleLevelUpgraded()) / 20)
        ));
    }

    @Override
    public int getCounter() {
        return tickCountdown / 20;
    }
}
