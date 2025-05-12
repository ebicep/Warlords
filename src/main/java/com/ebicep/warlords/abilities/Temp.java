package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class Temp extends AbstractAbility implements AbilityStats<Temp, Temp.TempStats> {

    private final TempStats stats = new TempStats();

    public Temp() {
        super(AbstractAbilityBuilder.create("placeholderAbility").pvp());
    }

    @Override
    protected void init(AbstractAbilityBuilder builder) {
        super.init(builder);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Placeholder Ability").build();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        return true;
    }

    @Override
    public TempStats getAbilityStats() {
        return stats;
    }

    public static class TempStats extends AbstractAbilityStats<Temp, TempStats> {

        @Override
        public Class<TempStats> getClazz() {
            return TempStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public TempStats merge(TempStats other, int multiplier) {
            TempStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public TempStats create() {
            return new TempStats();
        }

    }

}
