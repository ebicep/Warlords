package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TimeSurge extends AbstractAbility implements PurpleAbilityIcon, AbilityStats<TimeSurge, TimeSurge.TimeSurgeStats> {

    private TimeSurgeStats stats = new TimeSurgeStats();
    protected int healPercentage = 30;

    public TimeSurge() {
        super(AbstractAbilityBuilder.create("timeSurge").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.healPercentage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("healPercentage"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Restore ")
                .percent(healPercentage, NamedTextColor.GREEN)
                .text(" of your health.")
                .build();
    }


    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.timewarp.activation", 3, 1);
        wp.addInstance(InstanceBuilder
                .healing()
                .ability(this)
                .source(wp)
                .value(wp.getMaxHealth() * (healPercentage / 100f))
        );
        return true;
    }

    @Override
    public TimeSurgeStats getAbilityStats() {
        return stats;
    }

    public static class TimeSurgeStats extends AbstractAbilityStats<TimeSurge, TimeSurge.TimeSurgeStats> {

        @Override
        public Class<TimeSurge.TimeSurgeStats> getClazz() {
            return TimeSurge.TimeSurgeStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public TimeSurge.TimeSurgeStats merge(TimeSurge.TimeSurgeStats other, int multiplier) {
            TimeSurge.TimeSurgeStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public TimeSurge.TimeSurgeStats create() {
            return new TimeSurge.TimeSurgeStats();
        }

    }


}
