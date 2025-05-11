package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTimeWarp extends AbstractAbility implements PurpleAbilityIcon, AbilityStats<AbstractTimeWarp, AbstractTimeWarp.AbstractTimeWarpStats> {

    protected int tickDuration = 100;
    protected int warpHealPercentage = 30; //TODO
    private final AbstractTimeWarpStats stats = new AbstractTimeWarpStats();

    public AbstractTimeWarp() {
        super(AbstractAbilityBuilder.create("timeWarp").pvp());
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Activate to place a time rune on the ground. After ")
                .durationTicks(tickDuration)
                .text(", you will warp back to that location and restore ")
                .percent(warpHealPercentage, NamedTextColor.GREEN)
                .text(" of your health.")
                .build();
    }

    public int getTimesSuccessful() {
        return stats.timesSuccessful;
    }

    public int getWarpHealPercentage() {
        return warpHealPercentage;
    }

    public void setWarpHealPercentage(int warpHealPercentage) {
        this.warpHealPercentage = warpHealPercentage;
    }

    public int getTickDuration() {
        return tickDuration;
    }

    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public AbstractTimeWarpStats getAbilityStats() {
        return stats;
    }

    public static class AbstractTimeWarpStats extends AbstractAbilityStats<AbstractTimeWarp, AbstractTimeWarpStats> {

        @Field("times_successful")
        private int timesSuccessful = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Successful", timesSuccessful));
            return statsDisplay;
        }

        @Override
        public AbstractTimeWarpStats merge(AbstractTimeWarpStats other, int multiplier) {
            AbstractTimeWarpStats stats = super.merge(other, multiplier);
            stats.timesSuccessful = this.timesSuccessful + other.timesSuccessful * multiplier;
            return stats;
        }

        @Override
        public Class<AbstractTimeWarpStats> getClazz() {
            return AbstractTimeWarpStats.class;
        }

        @Override
        public AbstractTimeWarpStats create() {
            return new AbstractTimeWarpStats();
        }

        public void addTimesSuccessful() {
            timesSuccessful++;
        }
    }
}
