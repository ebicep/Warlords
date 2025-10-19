package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.BlueAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.AbstractWarlordsEntityEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractArcaneShield extends AbstractAbility implements BlueAbilityIcon, Duration, AbilityStats<AbstractArcaneShield, AbstractArcaneShield.ArcaneShieldStats> {

    private final ArcaneShieldStats stats = new ArcaneShieldStats();
    private int maxShieldHealth;
    private float shieldPercentage = 50;
    private int tickDuration = 120;

    public AbstractArcaneShield(AbstractAbilityBuilder builder) {
        super(builder);
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
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.shieldPercentage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("shieldPercentage"), float.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }

    @Override
    public ArcaneShieldStats getAbilityStats() {
        return stats;
    }

    public int getMaxShieldHealth() {
        return maxShieldHealth;
    }

    public static class ArcaneShieldStats extends AbstractAbilityStats<AbstractArcaneShield, ArcaneShieldStats> {

        @Field("times_broken")
        public int timesBroken = 0;

        @Field("total_absorbed")
        public float totalAbsorbed = 0;

        @Override
        public Class<ArcaneShieldStats> getClazz() {
            return ArcaneShieldStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Times Broken", timesBroken));
            statsDisplay.add(new AbilityStatDisplay("Total Absorbed", totalAbsorbed));
            return statsDisplay;
        }

        @Override
        public ArcaneShieldStats merge(ArcaneShieldStats other, int multiplier) {
            ArcaneShieldStats stats = super.merge(other, multiplier);
            stats.timesBroken = this.timesBroken + other.timesBroken * multiplier;
            stats.totalAbsorbed = this.totalAbsorbed + other.totalAbsorbed * multiplier;
            return stats;
        }

        @Override
        public ArcaneShieldStats create() {
            return new ArcaneShieldStats();
        }

    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Surround yourself with arcane energy, creating a shield that will absorb up to ")
                                               .percent(shieldPercentage, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" of your maximum health. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    public static class WarlordsArcaneShieldBrokenEvent extends AbstractWarlordsEntityEvent {

        private static final HandlerList handlers = new HandlerList();

        public static HandlerList getHandlerList() {
            return handlers;
        }


        public WarlordsArcaneShieldBrokenEvent(@Nonnull WarlordsEntity player) {
            super(player);
        }

        @Nonnull
        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

    }

    @Override
    public void updateCustomStats(WarlordsEntity warlordsEntity) {
        super.updateCustomStats(warlordsEntity);
        if (warlordsEntity != null) {
            setMaxShieldHealth((int) (warlordsEntity.getMaxHealth() * (getShieldPercentage() / 100f)));
            updateDescription(null);
        }
    }

    public void setMaxShieldHealth(int maxShieldHealth) {
        this.maxShieldHealth = maxShieldHealth;
    }

    public float getShieldPercentage() {
        return shieldPercentage;
    }

    public void setShieldPercentage(float shieldPercentage) {
        this.shieldPercentage = shieldPercentage;
    }
}
