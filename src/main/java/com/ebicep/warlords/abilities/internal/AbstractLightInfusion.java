package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLightInfusion extends AbstractAbility implements PurpleAbilityIcon, Duration, AbilityStats<AbstractLightInfusion, AbstractLightInfusion.AbstractLightInfusionStats> {

    protected static void playCastEffect(@Nonnull WarlordsEntity wp) {
        for (int i = 0; i < 10; i++) {
            EffectUtils.displayParticle(
                    Particle.EFFECT,
                    wp.getLocation().add(0, 1.5, 0),
                    3,
                    1,
                    0,
                    1,
                    .3
            );
        }
    }

    protected int tickDuration = 60;
    protected int speedBuff = 40;
    protected int energyGiven = 120;
    private final AbstractLightInfusionStats stats = new AbstractLightInfusionStats();

    public AbstractLightInfusion(float cooldown) {
        super("Light Infusion", cooldown, 0);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("You become infused with light, restoring ")
                .energy(energyGiven)
                .text(" and increasing your movement speed by ")
                .percent(speedBuff, NamedTextColor.WHITE)
                .text(" for ")
                .durationTicks(tickDuration)
                .text(".")
                .build();
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getSpeedBuff() {
        return speedBuff;
    }

    public void setSpeedBuff(int speedBuff) {
        this.speedBuff = speedBuff;
    }

    public int getEnergyGiven() {
        return energyGiven;
    }

    public void setEnergyGiven(int energyGiven) {
        this.energyGiven = energyGiven;
    }

    @Override
    public AbstractLightInfusionStats getAbilityStats() {
        return stats;
    }

    public static class AbstractLightInfusionStats extends AbstractAbilityStats<AbstractLightInfusion, AbstractLightInfusionStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public AbstractLightInfusionStats merge(AbstractLightInfusionStats other, int multiplier) {
            AbstractLightInfusionStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<AbstractLightInfusionStats> getClazz() {
            return AbstractLightInfusionStats.class;
        }

        @Override
        public AbstractLightInfusionStats create() {
            return new AbstractLightInfusionStats();
        }
    }

}